package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ArithmeticTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[1];
		inputRows[0] = "=1+2*2/3-8\t=99/11+1*0-34\t=0*23/5\t=5*C1-A1\t=3-D1/c1\t=1+2+3/1/2*A1\t=f1/0+b1\t=7777777777777777777*d1";
		ssResult = SpreadSheet.solve(1, 10, inputRows);
		result = new String[1][10];
		result[0][0] = "-6";
		result[0][1] = "-34";
		result[0][2] = "0";
		result[0][3] = "6";
		result[0][4] = "#" + ErrMsg.DIVBY0;
		result[0][5] = "-18";
		result[0][6] = "#" + ErrMsg.DIVBY0;
		result[0][7] = "#" + ErrMsg.OVERFLOW;
		result[0][8] = "";
		result[0][9] = "";		
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
