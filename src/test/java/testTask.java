import au.com.bytecode.opencsv.CSVReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 28.12.2015.
 */
public class testTask {


    String email = "";

    WebDriver driver;
    @BeforeTest
    public void startDriver() {
        driver = new FirefoxDriver();
    }
    @AfterTest
    public void stopDriver() {
        driver.close();
    }


    /*** Goto www.nascar.com

     * Register

     * Login with the registered credentials

     * Logout
     *
     */
    @Test
    public void firstTest() {

        openPage("http://www.nascar.com");
        buttonClick("//*[@class='gigyaRegisterDialog']");
        fillData("register");
        buttonClick("//*[@id='gigya-register-screen']//*[@name='data.terms']");
        buttonClick("//*[@id='gigya-register-screen']//*[@class='gigya-input-submit']");
        buttonClick("//*[@id='myProfileLink']");
        buttonClick("//*[@id='myProfileWrap']//*[@class=\"gigya-input-submit logout\"]");
        buttonClick("//*[@class='gigyaLoginDialog']");
        fillData("login");
        buttonClick("//*[@id='gigya-login-screen']//*[@class='gigya-input-submit']");
        buttonClick("//*[@id='myProfileLink']");
        buttonClick("//*[@id='myProfileWrap']//*[@class=\"gigya-input-submit logout\"]");


    }

    /**
     *Goto www.yahoo.com and fetch the Yahoo sites (Mail, Autos etc) in the Left dynamically and click over each site
     *to verify if the page loads in 7 seconds
     */
    @Test
    public void secondTest()
    {
        openPage("www.yahoo.com");

        int i = getCountofelements("//*[@class='navlist']/li");

       Assert.assertTrue(clickAndCheckPageLoad(i),"One of page doesn't loads in 7 second. See test run log");


    }

    /**
    * Fill data from csv file to field
    * generate random email from timestamp
    * save email value into variable
    *@param filename - file in resources (without extension)
    *
    *
    */


    public void fillData(String filename) {


        CSVReader csvReader = null;
        List<String[]> list = null;
        try {
            csvReader = new CSVReader(new FileReader(new File("build\\resources\\test\\"+filename+".csv")));
            list = csvReader.readAll();
        } catch ( FileNotFoundException e) {
            e.printStackTrace();
        }

         catch (IOException e) {
            e.printStackTrace();
        }


        String[][] dataArr = new String[list.size()][];
        dataArr = list.toArray(dataArr);
        for (int i=0; i< dataArr.length;i++ )
        {   waitforLoad();
            waitforvisibility(dataArr[i][0]);

            if (dataArr[i][1].contains("STAMP")) {
                dataArr[i][1] = dataArr[i][1].replace("STAMP", Integer.toString(Math.abs((int) Calendar
                        .getInstance().getTime().getTime())));
            email = dataArr[i][1];
            }
            if(dataArr[i][1].equals("email"))
            {
                dataArr[i][1]= email;
            }
            System.out.println("Set to "+dataArr[i][0]+ "value "+dataArr[i][1]);
            driver.findElement(By.xpath(dataArr[i][0])).sendKeys(dataArr[i][1]);
        }
    }

    /**
    *Click on element on page using xpath
    *
    * @param xpath xpath of element
    *
     */
    public void buttonClick(String xpath)
    {   waitforLoad();
        waitforvisibility(xpath);
        System.out.println("Click on button "+xpath);
        driver.findElement(By.xpath(xpath)).click();
//        waitforLoad();
    }

    public void openPage(String url)
    {
        System.out.println("Open page "+url);
        driver.get(url);
    }

    /**
     * Wait for page to load during 30 sec
     */
    public void waitforLoad()
    {
       waitforLoad(30);
    }

    /**
     * Wait for page to load
     * @param time time to load page
     * @return true if page loaded & false if not loaded
     */
    public boolean waitforLoad(int time)
    {
       System.out.println("Wait for page loading...");
       ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>()

        {
         public Boolean apply(WebDriver driver) {
          return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
        }
            };

        WebDriverWait wait = new WebDriverWait(driver, time);

        return wait.until(pageLoadCondition);

    }

    /**
     * Wait for visibility of element on page
     * fail - if not visible
     * @param xpath locator of element on page (xpath)
     */
    public void waitforvisibility(String xpath)
    {
        System.out.println("Wait for element "+ xpath);
        try {
            WebDriverWait wait = new WebDriverWait(driver, 60);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        }
        catch (Exception e)
        {
            Assert.fail("Element is not visible, Xpath is"+ xpath);
    }
    }


    /**
     * get count of elements on page
     * @param xpath locator of element on page
     * @return count of elements on page
     */
    public int getCountofelements(String xpath)
    {
        waitforLoad();
        waitforvisibility(xpath);
        return driver.findElements(By.xpath(xpath)).size();

    }

    /**
     * Click on each element //*[@class='navlist']/li[i] & check page load
     * Console weaning if Page doesn't loads in 7 second
     * @param count counts of elemtns
     * @return true if all pages loads in 7 second
     */
    public boolean clickAndCheckPageLoad(int count)
    {
        boolean load = true;
        for (int i=1; i<count; i++)
        {
            buttonClick("//*[@class='navlist']/li["+i+"]");
            if(waitforLoad(7)) {
                waitforLoad();
                System.out.println("WARNING Page doesn't loads in 7 seconds");
                load=  false;
            }
            else
                System.out.println("Page loads in 7 seconds");
            openPage("www.yahoo.com");


        }
        return load;
    }



}
