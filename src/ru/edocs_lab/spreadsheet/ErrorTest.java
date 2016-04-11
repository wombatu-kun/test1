package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ErrorTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[3];
		inputRows[0] = "=A1\t=3/b3\t=a2+2\t=d3*1+e3\t=E3/81";		
		inputRows[1] = "=b2-7\t=2*c1\t=z2/5\t=b3-b8\t=c0*44";
		inputRows[2] = "=2-a3*4\t=C3-42\t=40+2\t\t'ololo!11";
		result = new String[3][5];
		result[0][0] = "#" + ErrMsg.LOOP;
		result[0][1] = "#" + ErrMsg.DIVBY0;
		result[0][2] = "#" + ErrMsg.CYCLE;
		result[0][3] = "#" + ErrMsg.NULLOPRND;
		result[0][4] = "#" + ErrMsg.TEXTOPRND;
		result[1][0] = "#" + ErrMsg.CYCLE;
		result[1][1] = "#" + ErrMsg.CYCLE;
		result[1][2] = "#" + ErrMsg.OUTOFRNG;
		result[1][3] = "#" + ErrMsg.OUTOFRNG;
		result[1][4] = "#" + ErrMsg.GARBAGE;
		result[2][0] = "#" + ErrMsg.LOOP;
		result[2][1] = "0";
		result[2][2] = "42";
		result[2][3] = "";
		result[2][4] = "ololo!11";
		ssResult = SpreadSheet.solve(3, 5, inputRows);
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
