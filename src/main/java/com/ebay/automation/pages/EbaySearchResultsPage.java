package com.ebay.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Page Object for eBay search results and result-count information. */
public class EbaySearchResultsPage extends BasePage {
    private static final By SEARCH_INPUT = By.id("gh-ac");
    private static final By RESULT_CARDS = By.cssSelector(
            ".srp-results li.s-item, .srp-results li.s-card, .srp-river-results li.s-item");
    private static final By RESULTS_CONTAINER = By.cssSelector(".srp-results, .srp-river-results, .srp-river-answer");
    private static final By RESULT_COUNT_LABEL = By.cssSelector(
            ".srp-controls__count-heading, .srp-controls__count, h1.srp-controls__count-heading");

    public EbaySearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForResultsPage() {
        wait.until(ExpectedConditions.or(
                webDriver -> webDriver.getCurrentUrl().contains("_nkw="),
                ExpectedConditions.visibilityOfElementLocated(RESULTS_CONTAINER),
                ExpectedConditions.visibilityOfElementLocated(RESULT_COUNT_LABEL)));
        waitForDocumentReady();
    }

    public boolean isSearchTermApplied(String expectedSearchTerm) {
        String actual = "";
        List<WebElement> inputs = driver.findElements(SEARCH_INPUT);
        if (!inputs.isEmpty()) {
            actual = inputs.get(0).getAttribute("value");
        }
        return normalize(actual).contains(normalize(expectedSearchTerm));
    }

    public boolean hasAtLeastOneResult() {
        try {
            wait.until(webDriver -> getVisibleResultCards().size() > 0);
            return getVisibleResultCards().size() > 0;
        } catch (org.openqa.selenium.TimeoutException timeout) {
            return false;
        }
    }

    /** Counts visible result cards on the currently displayed page, not all matching listings. */
    public int getVisibleResultCount() {
        return getVisibleResultCards().size();
    }

    public String getResultCountLabel() {
        for (WebElement element : driver.findElements(RESULT_COUNT_LABEL)) {
            try {
                if (element.isDisplayed() && !element.getText().isBlank()) {
                    return element.getText().trim().replaceAll("\\s+", " ");
                }
            } catch (StaleElementReferenceException ignored) {
                // Continue to the next matching count element if eBay redraws the page.
            }
        }
        return "eBay result-count heading was not found";
    }

    /** Extracts the total shown by eBay (e.g. '1-48 of 2,345 results'); returns -1 if unavailable. */
    public long getReportedTotalResultCount() {
        String text = getResultCountLabel();
        Matcher totalMatcher = Pattern.compile("(?i)\\bof\\s+(?:over\\s+)?([\\d,]+)\\+?\\s+results?\\b").matcher(text);
        if (totalMatcher.find()) {
            return parseCount(totalMatcher.group(1));
        }
        Matcher simpleMatcher = Pattern.compile("(?i)\\b([\\d,]+)\\+?\\s+results?\\b").matcher(text);
        if (simpleMatcher.find()) {
            return parseCount(simpleMatcher.group(1));
        }
        return -1L;
    }

    private List<WebElement> getVisibleResultCards() {
        return driver.findElements(RESULT_CARDS).stream()
                .filter(element -> {
                    try {
                        return element.isDisplayed();
                    } catch (StaleElementReferenceException ignored) {
                        return false;
                    }
                })
                .toList();
    }

    private long parseCount(String value) {
        try {
            return Long.parseLong(value.replace(",", ""));
        } catch (NumberFormatException ignored) {
            return -1L;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
