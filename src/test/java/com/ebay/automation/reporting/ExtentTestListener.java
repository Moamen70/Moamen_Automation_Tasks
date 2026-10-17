package com.ebay.automation.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.ebay.automation.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** Adds TestNG results, and screenshots on failure, to the Extent HTML report. */
public class ExtentTestListener implements ITestListener {
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentReportManager.getInstance().createTest(result.getMethod().getMethodName());
        test.info("Test started: " + result.getMethod().getDescription());
        CURRENT_TEST.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.pass("Test passed.");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.fail(result.getThrowable());
            WebDriver driver = DriverManager.getDriver();
            if (driver != null) {
                try {
                    String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                    test.fail("Screenshot at failure", MediaEntityBuilder
                            .createScreenCaptureFromBase64String(screenshot, "Failure screenshot")
                            .build());
                } catch (RuntimeException screenshotFailure) {
                    test.warning("Could not capture failure screenshot: " + screenshotFailure.getMessage());
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.skip("Test skipped.");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.getInstance().flush();
        CURRENT_TEST.remove();
    }
}
