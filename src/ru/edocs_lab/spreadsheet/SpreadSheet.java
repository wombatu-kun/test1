package ru.edocs_lab.spreadsheet;

import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public final class SpreadSheet {
	public static final int MAX_ROWS = 99;
	public static final int MAX_COLS = 26;
	
	private final int mRowCount;
	private final int mColCount;
	private final String mSeparator;
	private Cell mSS[][];
	private HashSet<String> mPassedCells;
	private CountDownLatch mInitRowCounter;	
	
	private SpreadSheet(int rowCount, int colCount, String lines[], String sep) {
		mRowCount = rowCount;
		mColCount = colCount;
		mSeparator = sep;
		mSS = new Cell[rowCount][colCount];
		mPassedCells = new HashSet<>();		
	}
	
	public static String[][] solve(int rowCount, int colCount, String lines[], String sep) {
		if (0<rowCount && rowCount<=MAX_ROWS && 0<colCount && colCount<=MAX_COLS && lines!=null
				&& sep!=null && !sep.equals("")) {
			SpreadSheet ss = new SpreadSheet(rowCount, colCount, lines, sep);
			ss.initCells(lines); //в многопоточном режиме вычисляем всё, что без ссылок
			ss.evaluateCells(); //рекурсивно дорешиваем оставшиеся ячейки со ссылками
			return ss.toArray();
		} else {
			return null;
		}		
	}
	
	private void initCells(String lines[]) {		
		//не учитываем лишние строки (обрезаем до указанной размерности)
		int inputRowsMin = getRowCount()<=lines.length ? getRowCount() : lines.length;
		setInitRowCounter(inputRowsMin);
		ExecutorService execServ = Executors.newFixedThreadPool(inputRowsMin);
		for(int row=0; row<inputRowsMin; row++) {
			execServ.execute(new InitRowThread(row, lines[row]));
		}
		//добиваем недостающие строки ячейками с NULL'ями
		for(int row=inputRowsMin; row<getRowCount(); row++) {
			parseInputLine(row, "");
		}
		try {
			getInitRowCounter().await();
		} catch(InterruptedException e) {
			//shit happens, idunno
		}
		execServ.shutdown();
	}
	
	private class InitRowThread implements Runnable {
		private int row;
		private String line;		
		InitRowThread(int rowIdx, String rowStr){
			row = rowIdx;
			line = rowStr;
			new Thread(this);			
		}		
		public void run() {
			parseInputLine(row, line);
			getInitRowCounter().countDown();
		}
	}
	
	private void parseInputLine(int row, String line) {
		int inputColsMin;
		if (line != null) {
			String cols[] = line.split(mSeparator);
			//не учитываем лишние данные (обрезаем до указанной размерности)
			inputColsMin = getColCount()<=cols.length ? getColCount() : cols.length;
			for(int col=0; col<inputColsMin; col++) {
				createCell(row, col, cols[col]);
			}
		} else {
			inputColsMin = 0;
		}	
		//добиваем недостающие ячейки NULL'ями
		for(int col=inputColsMin; col<getColCount(); col++) {
			createCell(row, col, "");
		}
	}
	
	private void createCell(int row, int col, String inStr) {
		String label = getLabelByIdx(row, col);
		if(inStr==null || inStr.isEmpty()){
			mSS[row][col] = new NullCell(label);
			return;
		}
		switch(inStr.charAt(0)) {
		case '\'':
			mSS[row][col] = new TextCell(label, inStr);					
			break;
		case '=': //ячейка с голой ссылкой обрабатывается отдельно, потому что 
			//результат вычисления может быть как числом, так и текстом или NULLём,
			//а ссылка на текст или NULL в данном случае не является ошибкой.
			if (inStr.toUpperCase().matches("^=[A-Z][1-9][0-9]*$")) {
				mSS[row][col] = new LinkCell(label, inStr);
				break;
			}
			//для прочих выражений с= проходим в default
		default: //числа без=
			mSS[row][col] = new NumberCell(label, inStr); 
		}
	}
	
	private void evaluateCells() {
		for(int row=0; row<getRowCount(); row++) {
			for(int col=0; col<getColCount(); col++) {
				Cell cell = mSS[row][col];				
				if (!cell.isProcessed()) {
					getPassedCells().clear();
					cell.evaluate();
				}
			}
		}
	}
	
	private String getLabelByIdx(int row, int col) {
		return (char)('A' + (char)col) + String.valueOf(row+1);
	}
	
	private int[] getIdxByLabel(String label) {
		if (label==null || label.length()<2) {
			return null;
		}
		int idx[] = new int[2];
		try{
			idx[0] = Integer.valueOf(label.substring(1))-1;//строка			
		} catch(NumberFormatException e) {
			return null;
		}
		idx[1] = (int)(label.charAt(0) - 'A');//столбец
		if (0<=idx[0] && idx[0]<getRowCount() && 0<=idx[1] && idx[1]<getColCount()) {
			return idx;
		} else {
			return null;
		}
	}
	
	private Cell getCellByLabel(String label) {
		int idx[] = getIdxByLabel(label);
		if (idx != null) {
			return mSS[idx[0]][idx[1]];
		} else {
			return null;
		}
	}
	
	private class TextCell extends Cell {		
		TextCell(String label, String inStr) {
			super(label, inStr);
			setType(Type.TEXT);
			evaluateLocally();
		}		
		protected void evaluateLocally() {
			setOutput(getInput().substring(1));
			setProcessed(true);
		}
		public void evaluate() {
			if (!isProcessed()) {
				evaluateLocally();
			}
		}
	}	
	
	private class NullCell extends Cell {		
		NullCell(String label) {
			super(label, "");
			setType(Type.NULL);
			evaluateLocally();
		}		
		protected void evaluateLocally() {
			setProcessed(true);
		}
		public void evaluate() {
			if (!isProcessed()) {
				evaluateLocally();
			}
		}
	}
	
	private class LinkCell extends Cell {		
		LinkCell(String label, String inStr) {
			super(label, inStr.replace(" ", "").toUpperCase());
			setType(Type.LINK);
			evaluateLocally();
		}		
		protected void evaluateLocally() {
			String linkLabel = getInput().substring(1);
			if(linkLabel.equals(getLabel())) {
				setError(ErrMsg.LOOP);
			} else if (getIdxByLabel(linkLabel) == null) {
				setError(ErrMsg.OUT_OF_RANGE);
			}
		}
		public void evaluate() {			
			if (!isProcessed()) {
				if(getPassedCells().add(getLabel())) {
					Cell link = getCellByLabel(getInput().substring(1));
					if (link != null) {
						if (!link.isProcessed()) {
							link.evaluate();
						}
						setType(link.getType());
						setOutput(link.getOutput());						
						setProcessed(true);
					} else {//нет такой ячейки
						setError(ErrMsg.OUT_OF_RANGE);
					}
				} else {//ячейка не добавилась => уже проходили => зациклились
					setError(ErrMsg.CYCLE);
				}
			}
		}
	}	
	
	private class NumberCell extends Cell {		
		NumberCell(String label, String inStr) {
			super(label, inStr.replace(" ", "").toUpperCase());
			setType(Type.NUMBER);
			evaluateLocally();
		}		
		protected void evaluateLocally() {
			if (getInput().matches(SHLAK_PTRN)) {
				setError(ErrMsg.GARBAGE);
				return;
			}
			if (getInput().charAt(0) != '=') {
				try {
					Long.parseLong(getInput());
					setOutput(getInput());
					setProcessed(true);
				} catch (NumberFormatException e) {
					setError(ErrMsg.GARBAGE);					
				}				
				return;
			}
			if (getInput().matches("^=.+/0.*")) {
				setError(ErrMsg.DIV_BY_ZERO);
				return;
			}
			if (getInput().matches("^=.*" + getLabel() + "[^0-9]*.*")) {
				setError(ErrMsg.LOOP);
				return;
			}
			if (!getInput().matches(".*[A-Z].*")) {
				Pattern ptOper = Pattern.compile("[=*/+-][0-9]+");
				doCellExpressionResult(ptOper);											
			}
		}
		public void evaluate() {
			if (!isProcessed()) {
				if(getPassedCells().add(getLabel())) {
					Pattern ptOper = Pattern.compile("([=*/+-][0-9]+)|([=*/+-][A-Z][1-9][0-9]*)");
					doCellExpressionResult(ptOper);
				} else {
					setError(ErrMsg.CYCLE);
				}				
			}
		}		
		private void doCellExpressionResult(Pattern ptOper) {			
			try {
				Long val = parseExpression(ptOper);
				if (val != null) {
					setOutput(String.valueOf(val));
					setProcessed(true);
				} else {
					//либо parseExpression установил ошибку DIV_BY_ZERO
					//либо parseExpression->getCurrentOperandOrStop установил ошибку по ссылке
					//в любом случае, ячейка вычислена
				}
			} catch (NumberFormatException e) {
				setError(ErrMsg.GARBAGE);
			} catch (ArithmeticException e) {
				setError(ErrMsg.OVERFLOW);
			}
		}
		private Long parseExpression(Pattern oper) 
				throws NumberFormatException, ArithmeticException {
			try (Scanner scan = new Scanner(getInput())) {
				String part = scan.findInLine(oper);
				Long total = new Long(0);
				Long operand;
				while(part != null) {
					operand = getCurrentOperandOrStop(part.substring(1));
					if (operand == null) {//установили ошибку из ячейки по ссылке
						return null; //дальше не надо
					} 
					switch(part.charAt(0)) {
					case '=':
						total = operand;
						break;
					case '+':
						total = Math.addExact(total, operand);
						break;
					case '-':
						total = Math.subtractExact(total, operand);
						break;
					case '*':
						total = Math.multiplyExact(total, operand);
						break;
					case '/':
						if (operand != 0) {
							total = total / operand;
						} else {
							setError(ErrMsg.DIV_BY_ZERO);
							return null;
						}							
					}
					part = scan.findInLine(oper);
				}
				return total;
			} 
		}
		private Long getCurrentOperandOrStop(String str) throws NumberFormatException {
			Long operand = null;
			if (str.charAt(0) > '9') {//начинается с буквы => операнд-ссылка
				Cell link = getCellByLabel(str);
				if (link != null) {
					if (!link.isProcessed()) {
						link.evaluate();
					}
					switch(link.getType()) {
					case ERROR:
						setType(Type.ERROR);
						setOutput(link.getOutput());
						setProcessed(true);
						break;
					case TEXT:
						setError(ErrMsg.TEXT_OPERAND);
						break;
					case NULL:
						setError(ErrMsg.NULL_OPERAND);
						break;
					case NUMBER:
						operand = Long.parseLong(link.getOutput());
						break;
					default:
						//не может быть
					}
				} else {//нет такой ячейки
					setError(ErrMsg.OUT_OF_RANGE);
				}
			} else { //просто число
				operand = Long.parseLong(str);
			}						
			return operand;
		}
	}	
	
	
	private int getRowCount() {
		return mRowCount;
	}

	private int getColCount() {
		return mColCount;
	}

	private HashSet<String> getPassedCells() {
		return mPassedCells;
	}

	private CountDownLatch getInitRowCounter() {
		return mInitRowCounter;
	}

	private void setInitRowCounter(int maxVal) {
		mInitRowCounter = new CountDownLatch(maxVal);
	}
	
	private String[][] toArray() {
		String result[][] = new String[getRowCount()][getColCount()];
		for(int row=0; row<getRowCount(); row++) {
			for(int col=0; col<getColCount(); col++) {
				result[row][col] = mSS[row][col].getOutput();
			}
		}
		return result;
	}
}
