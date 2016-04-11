package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ExampleTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[3];
		inputRows[0] = "12\t=c2\t3\t'Sample";
		inputRows[1] = "=a1+b1*c1/5\t=a2*B1\t=B3-C3\t'Spread";
		inputRows[2] = "'Test\t=4-3\t5\t'Sheet";
		result = new String[3][4];
		result[0][0] = "12";
		result[0][1] = "-4";
		result[0][2] = "3";
		result[0][3] = "Sample";
		result[1][0] = "4";
		result[1][1] = "-16";
		result[1][2] = "-4";
		result[1][3] = "Spread";
		result[2][0] = "Test";
		result[2][1] = "1";
		result[2][2] = "5";
		result[2][3] = "Sheet";
		ssResult = SpreadSheet.solve(3, 4, inputRows, "\t");
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
