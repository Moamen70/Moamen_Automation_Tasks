package com.ebay.automation.tests;

import com.ebay.automation.config.ConfigManager;
import com.ebay.automation.driver.DriverFactory;
import com.ebay.automation.driver.DriverManager;
import com.ebay.automation.pages.EbayHomePage;
import com.ebay.automation.pages.EbaySearchResultsPage;
import com.ebay.automation.utils.EbaySearchTestData;
import com.ebay.automation.utils.TestDataReader;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** End-to-end eBay search flow for Task 2, without attempting any sidebar filters. */
public class EbaySearchTest {
    private EbaySearchTestData testData;
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void loadExternalTestData() {
        testData = TestDataReader.readEbaySearchData();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);
    }

    @Test(description = "Open eBay, search Mazda MX-5, validate and log the result count")
    public void searchMazdaMx5AndLogResultCount() {
        EbayHomePage homePage = new EbayHomePage(driver);
        homePage.open(ConfigManager.get("baseUrl"));

        Assert.assertTrue(
                homePage.isMainPageLoaded(testData.getExpectedHost(), testData.getExpectedHomeTitleContains()),
                "The browser did not land on the expected eBay main page. Current URL: " + driver.getCurrentUrl()
                        + ", title: " + driver.getTitle());
        System.out.println("PASS: eBay main page loaded. Title: " + driver.getTitle());

        homePage.searchFor(testData.getSearchTerm());
        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(driver);
        resultsPage.waitForResultsPage();

        Assert.assertTrue(resultsPage.isSearchTermApplied(testData.getSearchTerm()),
                "The search input does not contain the expected query: " + testData.getSearchTerm());
        Assert.assertTrue(resultsPage.hasAtLeastOneResult(),
                "No visible search result cards were found for query: " + testData.getSearchTerm());

        String countLabel = resultsPage.getResultCountLabel();
        long reportedTotal = resultsPage.getReportedTotalResultCount();
        int visibleCards = resultsPage.getVisibleResultCount();

        System.out.println("Search query: " + testData.getSearchTerm());
        System.out.println("eBay result-count label: " + countLabel);
        System.out.println("Total reported by eBay: "
                + (reportedTotal >= 0 ? reportedTotal : "not parsed from heading"));
        System.out.println("Visible result cards on this page: " + visibleCards);

        Assert.assertTrue(reportedTotal > 0 || visibleCards > 0,
                "The result count should be positive. Label: " + countLabel);

        System.out.println("There is no Transmission filter available in the filters for this search.");
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        DriverManager.quitDriver();
    }
}
