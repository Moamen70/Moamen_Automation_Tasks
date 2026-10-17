package com.ebay.automation.utils;

/** POJO mapped from src/test/resources/testdata/ebay-search-data.json. */
public class EbaySearchTestData {
    private String searchTerm;
    private String expectedHost;
    private String expectedHomeTitleContains;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getExpectedHost() {
        return expectedHost;
    }

    public void setExpectedHost(String expectedHost) {
        this.expectedHost = expectedHost;
    }

    public String getExpectedHomeTitleContains() {
        return expectedHomeTitleContains;
    }

    public void setExpectedHomeTitleContains(String expectedHomeTitleContains) {
        this.expectedHomeTitleContains = expectedHomeTitleContains;
    }
}
