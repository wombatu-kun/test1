package ru.edocs_lab.spreadsheet;

enum Type {TEXT, NUMBER, ERROR, LINK, NULL, X3};
enum ErrMsg {GARBAGE, DIV_BY_ZERO, LOOP, CYCLE, OVERFLOW, NULL_OPERAND, TEXT_OPERAND, OUT_OF_RANGE};

public abstract class Cell {
	protected static final String SHLAK_PTRN = "(^=.*[^-+/*A-Z0-9]+.*)|(.*[^0-9]$)|(^[^=1-9].*)|(.*[0-9][A-Z].*)|(.*[A-Z][-*/+].*)|(.*[A-Z][A-Z]+.*)|(.*[-*/+][-*/+]+.*)|(.*[^1-9]0[0-9]+.*)|(^=[-*/+].*)|(.*[A-Z]0.*)";
	//непустую не-' строку будем считать шлаком, если: есть посторонние символы; заканчивается не на цифру;
	//начинается не с = или с цифры; есть цифра перед буквой; есть операция после буквы; две и больше буквы рядом;
	//два и больше действия рядом; ряд цифр, начинающийся с нуля; начинается с =операция; ячейка с индексом 0;
	
	private final String mLabel;	
	private final String mIn;
	private Type mType;
	private String mOut;	
	private boolean mIsProcessed;
	
	protected Cell(String label, String inStr) {
		mLabel = label;
		mIn = inStr;
		setProcessed(false);
		setType(Type.X3);
		setOutput("");
	}

	protected abstract void evaluateLocally();
	public abstract void evaluate();
	
	protected String getInput() {
		return mIn;
	}

	public boolean isProcessed() {
		return mIsProcessed;
	}
	
	public Type getType() {
		return mType;
	}
	
	public String getOutput() {
		return mOut;
	}	

	protected String getLabel() {
		return mLabel;
	}

	protected void setProcessed(boolean isProcessed) {
		mIsProcessed = isProcessed;
	}

	protected void setType(Type type) {
		mType = type;
	}
	
	protected void setOutput(String out) {
		mOut = out;
	}
	
	protected void setError(ErrMsg e) {
		setType(Type.ERROR);
		setOutput("#" + e.toString());
		setProcessed(true);
	}
	
	@Override
	public String toString() {
		return getOutput();
	}
}
