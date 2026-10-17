# eBay Search Automation (Java 17 + Selenium + TestNG)

## Automated flow

1. Launch the configured browser and open `https://www.ebay.com/`.
2. Validate the eBay main page using the expected host, title fragment, and visible search field.
3. Search for the query loaded from an external JSON file (default: `mazda mx-5`).
4. Validate that the search query is applied and at least one result card is visible.
5. Print eBay's result-count heading, the parsed total when available, and the number of result cards visible on the current page.
6. Close the browser using TestNG teardown, even if an assertion fails.


## Project structure

```text
src/
├── main/java/com/ebay/automation/
│   ├── config/ConfigManager.java
│   ├── driver/DriverFactory.java
│   ├── driver/DriverManager.java
│   ├── pages/BasePage.java
│   ├── pages/EbayHomePage.java
│   ├── pages/EbaySearchResultsPage.java
│   ├── utils/EbaySearchTestData.java
│   └── utils/TestDataReader.java
└── test/
    ├── java/com/ebay/automation/reporting/ExtentReportManager.java
    ├── java/com/ebay/automation/reporting/ExtentTestListener.java
    ├── java/com/ebay/automation/tests/EbaySearchTest.java
    └── resources/
        ├── config.properties
        └── testdata/ebay-search-data.json
pom.xml
testng.xml
```

## Requirements

- JDK 17 and `JAVA_HOME` configured.
- Maven 3.9 or later.
- Chrome installed by default; Edge and Firefox are also supported.
- Internet access for Maven dependencies, Selenium Manager, and eBay.

Verify the environment:

```bash
java -version
mvn -version
```

## Run from IntelliJ IDEA

1. Extract the ZIP.
2. Open the extracted folder as a Maven project (select `pom.xml` if prompted).
3. Set Project SDK and language level to Java 17.
4. Wait for Maven to download dependencies.
5. Run `testng.xml` or `EbaySearchTest`.
6. Keep `headless=false` to show the browser actions for screen recording.

## Run from the terminal

```bash
mvn clean test
```

Use Microsoft Edge:

```bash
mvn clean test -Dbrowser=edge
```

Use Firefox:

```bash
mvn clean test -Dbrowser=firefox
```

Run headless (not recommended for a proof video):

```bash
mvn clean test -Dheadless=true
```

## Test data and configuration

Change the search term and expected homepage values in `src/test/resources/testdata/ebay-search-data.json`. Change the URL, browser, waits, or report path in `src/test/resources/config.properties`. JVM properties such as `-Dbrowser=edge` override configuration properties.

## Reports and evidence

- Extent HTML report: `target/extent-report/ExtentReport.html`
- TestNG/Surefire reports: `target/surefire-reports/`
- When a test fails and a browser session is available, the Extent report attempts to attach a screenshot.
- Record the screen separately using Snipping Tool, Xbox Game Bar (`Win+Alt+R`), or OBS. Start recording before test execution and show the browser launch through its automatic closure.

