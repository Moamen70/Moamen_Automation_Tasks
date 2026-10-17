package com.ebay.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page Object for eBay's home page. */
public class EbayHomePage extends BasePage {
    private static final By SEARCH_INPUT = By.id("gh-ac");
    private static final By SEARCH_BUTTON = By.id("gh-search-btn");
    private static final By PRIVACY_ACCEPT_BUTTONS = By.cssSelector(
            "#gdpr-banner-accept, button[aria-label*='Accept all'], button[aria-label*='Accept All']");

    public EbayHomePage(WebDriver driver) {
        super(driver);
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
        waitForDocumentReady();
        dismissPrivacyConsentIfPresent();
        waitForVisible(SEARCH_INPUT);
    }

    public boolean isMainPageLoaded(String expectedHost, String expectedTitleFragment) {
        String currentUrl = driver.getCurrentUrl().toLowerCase();
        String title = driver.getTitle().toLowerCase();
        return currentUrl.contains(expectedHost.toLowerCase())
                && title.contains(expectedTitleFragment.toLowerCase())
                && isDisplayed(SEARCH_INPUT);
    }

    public void searchFor(String searchTerm) {
        type(SEARCH_INPUT, searchTerm);
        // Clicking Search is preferred where present; Enter is the fallback for minor layout variations.
        if (isDisplayed(SEARCH_BUTTON)) {
            try {
                click(SEARCH_BUTTON);
            } catch (RuntimeException clickFailure) {
                pressEnter(SEARCH_INPUT);
            }
        } else {
            pressEnter(SEARCH_INPUT);
        }
    }

    private void dismissPrivacyConsentIfPresent() {
        tryClickIfVisible(PRIVACY_ACCEPT_BUTTONS);
        // Text-based fallback for regional versions of eBay's consent dialog.
        tryClickIfVisible(By.xpath("//button[normalize-space()='Accept all' or normalize-space()='Accept All' or normalize-space()='I agree']"));
    }
}
