package com.sample.frameworksetup;

import org.testng.annotations.Test;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class driverProgram
{
	int i = 0;
	public static WebDriver driver = new ChromeDriver();
	
	@DataProvider(name="SearchProvider")
	public String[][] getDataFromDataprovider() throws IOException
	{
		String filePath = "C:\\Users\\spk19\\git\\newTestFramework\\";
		String fileName = "TestData.xlsx";
		String sheetName = "TestCases";
		File file =    new File(filePath+fileName);
	  	FileInputStream inputStream = new FileInputStream(file);
		Workbook workbook = null;
		String fileExt = fileName.substring(fileName.indexOf("."));

		if(fileExt.equals(".xlsx"))
		{
			workbook = new XSSFWorkbook(inputStream);
		}
		else if(fileExt.equals(".xls"))
		{
		    workbook = new HSSFWorkbook(inputStream);
		}
		
		Sheet worksheet = workbook.getSheet(sheetName);
		int rowcount = worksheet.getLastRowNum();
		int colcount = worksheet.getRow(0).getLastCellNum();
		String[][] testcases = new String[rowcount][colcount];
		
		for (int i = 0; i<rowcount; i++)
		{
			Row row = worksheet.getRow(i+1);
			colcount = row.getLastCellNum();
			for (int j = 0; j < row.getLastCellNum(); j++)
			{
				testcases[i][j]=row.getCell(j).getStringCellValue();
			}
	  	}
		return testcases;
	  }
	
	  @Test(dataProvider="SearchProvider", enabled=false)
	  public void looptestcase(String testCaseNo, String testCaseDescription, String testCaseRunSel, String testCaseBrowser) throws InterruptedException
	  {
		  boolean testresult = false;
		  testExecution testExecution = new testExecution();
		  
		  System.out.println("in testing1");
		  
		  //System.out.println("testCase : "+testCaseNo+", testCaseDescription : "+testCaseDescription+", testCaseRunSel : "+testCaseRunSel+", testCaseBrowser : "+testCaseBrowser);
		  if (testCaseRunSel.equalsIgnoreCase("Yes"))
		  {  
			  testresult = testExecution.executeTestCase(testCaseNo, testCaseDescription, testCaseRunSel, testCaseBrowser);
			  if (testresult){
				  System.out.println(testCaseNo+" is pass");
			  }
			  else{
				  System.out.println(testCaseNo+" is failed");
			  }
		  }
		  else{
			  System.out.println(testCaseNo+" is skipped");
		  }
	  }
	  
	  @Test (enabled=false)
	  public void testing2()
	  {
		  System.out.println("in testing2");
		  propertyFile pf = new propertyFile();
		  pf.loadPropertyFile();
		  //System.out.println("urllink : "+pf.getPropertyValue("urllink"));
	  }
	  
	  @Test (enabled=true)
	  public void testing3()
	  {
		  System.out.println("\nin testing3");
		  
		  driver.get("https://www.yahoo.com");
		  screenShots screenshots = new screenShots();
		  screenshots.getScreenShot("testing3", driver);
		  screenshots.logScreenShot("testing3", driver);
		  //Reporter.setCurrentTestResult(null);
	  }
	  
	  @Test (enabled=true)
	  public void testing4()
	  {
		  System.out.println("\nin testing4");
		  
		  driver.get("https://www.msn.com");
		  screenShots screenshots = new screenShots();
		  screenshots.getScreenShot("testing41", driver);
		  screenshots.logScreenShot("testing41", driver);
		  
		  JavascriptExecutor jse = (JavascriptExecutor)driver;
		  jse.executeScript("scroll(0, 250);");
		  
		  screenshots.getScreenShot("testing42", driver);
		  screenshots.logScreenShot("testing42", driver);
		  //Reporter.setCurrentTestResult(null);
	  }
	  
	  
	  @BeforeSuite
	  public void beforeSuite() {
		  System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//chromedriver.exe");
	  }
	  
	  @AfterSuite
	  public void afterSuite() {
		  driver.close();
	  }
	  
	  @BeforeMethod
	  public void beforeMethod()
	  {
	  }
	
	  @AfterMethod
	  public void afterMethod()
	  {
	  }
	  
	  @BeforeClass
	  public void beforeClass()
	  {
	  }
	
	  @AfterClass
	  public void afterClass()
	  {
	  }
	
	  @BeforeTest
	  public void beforeTest()
	  {
	  }
	  
	  @AfterTest
	  public void afterTest()
	  {
	  }
}
