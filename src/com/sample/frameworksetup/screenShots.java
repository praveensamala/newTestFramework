package com.sample.frameworksetup;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

public class screenShots
{
	//To take the screenshot
	public void getScreenShot(String testcase, WebDriver driver)
	{
		 File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		 String filename = "\\screenshots\\"+testcase+".png";
		 try {
			 FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+filename));
		 }
		 catch (IOException e) {
			 System.out.println("Received IOException in getScreenShot : "+e);
		 }
	}
	
	//To log the screenshot to the report
	public void logScreenShot(String testcase, WebDriver driver)
	{
		String filepath  = System.getProperty("user.dir")+"\\screenshots\\"+testcase+".png";
		Reporter.log("<br> <img src="+filepath+" /> <br>");
	}
}
