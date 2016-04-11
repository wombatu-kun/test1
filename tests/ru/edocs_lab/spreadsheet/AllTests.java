package ru.edocs_lab.spreadsheet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ArithmeticTest.class, DimensionTest.class, ErrorTest.class, ExampleTest.class, LinkTest.class,
		LocalTest.class, ShlakTest.class })
public class AllTests {

}
