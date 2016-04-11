package ru.edocs_lab.spreadsheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpreadSheetDemo {
	private static final String COL_SEPARATOR = "\t";
	private static final String ROW_SEPARATOR = "\n";
	
	public static void main(String[] args) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String inStr = reader.readLine();
			int dims[] = parseDimensions(inStr);
			if (dims != null) {
				String inputRows[] = new String[dims[0]];
				for (int row = 0; row < dims[0]; row++) {
					inputRows[row] = reader.readLine();
				}
				String result[][] = SpreadSheet.solve(dims[0], dims[1], inputRows, COL_SEPARATOR);
				printResult(result);
			} else {
				System.out.println("Format: rowCount\tcolumnCount");
				System.out.printf("0 < rowCount <= %d, \t0 <= columnCount < %d", SpreadSheet.MAX_ROWS,
						SpreadSheet.MAX_COLS);
			}
		}
	}

	private static int[] parseDimensions(String inStr) {
		try {
			if (!inStr.contains(COL_SEPARATOR)) {
				return null;
			}
			String strDims[] = inStr.split(COL_SEPARATOR, 2);
			int dims[] = new int[2];
			dims[0] = Integer.parseInt(strDims[0]);
			dims[1] = Integer.parseInt(strDims[1]);
			if (0 < dims[0] && dims[0] <= SpreadSheet.MAX_ROWS && 0 < dims[1] && dims[1] <= SpreadSheet.MAX_COLS) {
				return dims;
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static void printResult(String result[][]) {
		if (result == null) {
			return;
		}
		for (int row = 0; row < result.length; row++) {
			System.out.print(result[row][0]);
			for (int col = 1; col < result[row].length; col++) {
				System.out.print(COL_SEPARATOR + result[row][col]);
			}
			System.out.print(ROW_SEPARATOR);
		}
	}

}
