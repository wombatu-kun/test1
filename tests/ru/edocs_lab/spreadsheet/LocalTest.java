package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LocalTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[1];
		inputRows[0] = "100\t'ololo\t\t=d1\t=A5\t=42\t=1+2*4-8-10\t=2*9/0-3\td2\t=99999999999999*99999999999";
		result = new String[1][10];
		result[0][0] = "100";
		result[0][1] = "ololo";
		result[0][2] = "";
		result[0][3] = "#" + ErrMsg.LOOP;
		result[0][4] = "#" + ErrMsg.OUT_OF_RANGE;
		result[0][5] = "42";
		result[0][6] = "-6";
		result[0][7] = "#" + ErrMsg.DIV_BY_ZERO;
		result[0][8] = "#" + ErrMsg.GARBAGE;
		result[0][9] = "#" + ErrMsg.OVERFLOW;
		ssResult = SpreadSheet.solve(1, 10, inputRows, "\t");
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
