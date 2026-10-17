package com.ebay.automation.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ebay.automation.config.ConfigManager;

import java.io.File;

/** Creates a single HTML report for the TestNG suite. */
public final class ExtentReportManager {
    private static ExtentReports extentReports;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            String reportPath = ConfigManager.get("reportPath");
            File reportFile = new File(reportPath);
            File parent = reportFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Could not create report directory: " + parent.getAbsolutePath());
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);
            sparkReporter.config().setDocumentTitle("eBay Automation Test Report");
            sparkReporter.config().setReportName("Search Mazda MX-5 and validate result count");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "Selenium + TestNG + POM");
            extentReports.setSystemInfo("Java", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", ConfigManager.get("browser"));
        }
        return extentReports;
    }
}
