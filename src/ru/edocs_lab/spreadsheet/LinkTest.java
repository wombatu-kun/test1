package ru.edocs_lab.spreadsheet;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LinkTest {
	String ssResult[][];
	String result[][];
	
	@Before
	public void setUp() throws Exception {
		String inputRows[] = new String[3];
		inputRows[0] = "=a2\t=B1\t=D1\t=E1\t=C1";
		inputRows[1] = "=b2\t=C2\t=d2\t=e2\t=42";
		inputRows[2] = "=b3\t\t=d3\t=e3\t'�"; 
		result = new String[3][5];
		result[0][0] = "42";
		result[0][1] = "#" + ErrMsg.LOOP;
		result[0][2] = "#" + ErrMsg.CYCLE;
		result[0][3] = "#" + ErrMsg.CYCLE;
		result[0][4] = "#" + ErrMsg.CYCLE;		
		for(int col=0; col<5; col++) {
			result[1][col] = "42";			
		}
		result[2][0] = "";
		result[2][1] = "";
		result[2][2] = "�";
		result[2][3] = "�";
		result[2][4] = "�";
		ssResult = SpreadSheet.solve(3, 5, inputRows);
	}

	@Test
	public void test() {
		assertArrayEquals(result, ssResult);
	}

}
