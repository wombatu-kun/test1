package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Test;

public class DimensionTest {

	@Test
	public void test() {
		String ssResult[][];
		String inputRows[] = new String[3];
		ssResult = SpreadSheet.solve(-4, 5, inputRows, "\t");
		assertNull(ssResult);
		ssResult = SpreadSheet.solve(3, 0, inputRows, "\t");
		assertNull(ssResult);
		ssResult = SpreadSheet.solve(100, 5, inputRows, "\t");
		assertNull(ssResult);
		ssResult = SpreadSheet.solve(3, 27, inputRows, "\t");
		assertNull(ssResult);
		ssResult = SpreadSheet.solve(2, 5, null, "\t");
		assertNull(ssResult);
		
		inputRows[0] = "11\t12\t13\t14";
		inputRows[1] = "21\t22\t23\t24";
		inputRows[2] = "31\t32\t33\t34";
		ssResult = SpreadSheet.solve(2, 2, inputRows, "\t");
		String result[][] = new String[2][2];
		result[0][0] = "11";
		result[0][1] = "12";
		result[1][0] = "21";
		result[1][1] = "22";		
		assertArrayEquals(result, ssResult);
		
		inputRows = new String[2];
		inputRows[0] = "11";
		inputRows[1] = "21\t22\t23";
		ssResult = SpreadSheet.solve(3, 2, inputRows, "\t");
		result = new String[3][2];
		result[0][0] = "11";
		result[0][1] = "";
		result[1][0] = "21";
		result[1][1] = "22";
		result[2][0] = "";
		result[2][1] = "";
		assertArrayEquals(result, ssResult);
	}

}
