package testProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class propertyFile
{
	Properties prop = null;
	
	public void loadPropertyFile()
	{
		prop = new Properties();
		String propfilename = "config.properties";
		InputStream inputstream = getClass().getClassLoader().getResourceAsStream(propfilename);
		//InputStream inputstream = getClass().getResourceAsStream("C:\\Selenium Workspace\\newTestFramework\\resources\\config.properties");
		if (inputstream != null)
		{
			//System.out.println(new File(".").getAbsolutePath());
			try{
				prop.load(inputstream);
			}
			catch(IOException e){
				System.out.println("Received ioexception" + e);
			}
		}
		else
		{
			System.out.println("Received error while loading the property values");
		}
		System.out.println("urllink : "+prop.getProperty("urllink"));
	}
	
	public String getPropertyValue(String propertyname)
	{
		return prop.getProperty(propertyname);
	}
}
