package com.ebay.automation.pages;

import com.ebay.automation.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Shared Selenium actions and explicit-wait helpers used by all Page Objects. */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getInt("explicitWaitSeconds")));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            // One retry after scrolling often resolves overlays/sticky headers without hiding real failures.
            scrollIntoView(element);
            waitForClickable(locator).click();
        }
    }

    protected void click(WebElement element) {
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            scrollIntoView(element);
            element.click();
        }
    }

    protected void type(By locator, String value) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    protected boolean isDisplayed(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        for (WebElement element : elements) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                // The page is re-rendering; keep looking for a live match.
            }
        }
        return false;
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    protected void waitForDocumentReady() {
        wait.until(webDriver -> "complete".equals(
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }

    protected boolean tryClickIfVisible(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed() && element.isEnabled()) {
                    element.click();
                    return true;
                }
            } catch (RuntimeException ignored) {
                // Consent banners vary by geography; failure to find/dismiss one must not fail every run.
            }
        }
        return false;
    }
}
