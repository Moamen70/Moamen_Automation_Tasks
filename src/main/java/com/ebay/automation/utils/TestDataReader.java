package com.ebay.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Reads external JSON scenario data instead of hardcoding it in the test. */
public final class TestDataReader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestDataReader() {
    }

    public static EbaySearchTestData readEbaySearchData() {
        String resource = "testdata/ebay-search-data.json";
        try (InputStream input = TestDataReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalStateException("Test data file not found on classpath: " + resource);
            }
            EbaySearchTestData data = OBJECT_MAPPER.readValue(input, EbaySearchTestData.class);
            validate(data);
            return data;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read test data file: " + resource, exception);
        }
    }

    private static void validate(EbaySearchTestData data) {
        if (data == null || isBlank(data.getSearchTerm()) || isBlank(data.getExpectedHost())
                || isBlank(data.getExpectedHomeTitleContains())) {
            throw new IllegalStateException("Test data JSON is incomplete. Check searchTerm, expectedHost, "
                    + "and expectedHomeTitleContains.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
