package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ShlakTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[1];
		inputRows[0] = "=a1+2.4\t=2+\t-5+2\t=29*1b\t=s+6\t=23/aa5+1\t=c2++65\t=a1-02\t=-4+1\t=20/5+3*b0-10*21";
		result = new String[1][10];
		for(int col=0; col<10; col++) {
			result[0][col] = "#" + ErrMsg.GARBAGE;
		}
		ssResult = SpreadSheet.solve(1, 10, inputRows, "\t");
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
