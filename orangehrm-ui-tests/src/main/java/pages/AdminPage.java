package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class AdminPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Nav / actions
    private final By adminTab            = By.xpath("//span[normalize-space()='Admin']");
    private final By addBtn              = By.xpath("//button[normalize-space()='Add']");

    // Search area
    private final By searchUsername      = By.xpath("//label[text()='Username']/../following-sibling::div//input");
    private final By searchBtn           = By.xpath("//button[normalize-space()='Search']");
    private final By resetBtn            = By.xpath("//button[normalize-space()='Reset']");

    // Table + states (robust)
    private final By tableRoot           = By.cssSelector(".oxd-table");
    private final By tableBody           = By.cssSelector(".oxd-table-body");
    private final By tableRows           = By.cssSelector(".oxd-table-body .oxd-table-card");
    private final By emptyState          = By.xpath("//span[normalize-space()='No Records Found']");

    // Row delete (per-row action)
    private final By confirmDeleteBtn    = By.xpath("//button[normalize-space()='Yes, Delete']");
    private final By successDeletedToast = By.xpath("//p[contains(@class,'oxd-text') and contains(.,'Successfully Deleted')]");

    // (Legacy) bulk delete – kept for compatibility but not used
    private final By selectAllCheckbox   = By.cssSelector("div.oxd-table-header input[type='checkbox'], div.oxd-table-header .oxd-checkbox-input");
    private final By firstRowCheckbox    = By.cssSelector(".oxd-table-card input[type='checkbox'], .oxd-table-card .oxd-checkbox-input");
    private final By bulkDeleteBtn       = By.xpath("//button[normalize-space()='Delete Selected']");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public void goToAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(adminTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRoot));
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableBody));
    }

    /** Wait until either rows are present or the "No Records Found" state is visible. */
    private void waitForResultsOrEmpty() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(emptyState),
                ExpectedConditions.presenceOfAllElementsLocatedBy(tableRows)
        ));
    }

    /** Wait for table refresh by staleness of the previous body. */
    private void waitTableRefresh(WebElement oldBody) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(12))
                    .until(ExpectedConditions.stalenessOf(oldBody));
        } catch (TimeoutException ignored) {
            // If staleness didn't trigger (rare), continue to state wait below
        }
        waitForResultsOrEmpty();
    }

    /** Count visible rows robustly (no dependence on labels). */
    public int getRecordsCount() {
        waitForResultsOrEmpty();
        if (!driver.findElements(emptyState).isEmpty()) return 0;
        return driver.findElements(tableRows).size();
    }

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
    }

    /** Search by Username and wait for the grid to actually refresh. */
    public void searchUser(String usernameValue) {
        WebElement oldBody = wait.until(ExpectedConditions.visibilityOfElementLocated(tableBody));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchUsername));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        input.sendKeys(usernameValue);

        driver.findElement(searchBtn).click();
        waitTableRefresh(oldBody);
    }

    /** Clear filters and refresh the grid (prefer the built-in Reset; fallback to manual clear). */
    public void resetSearch() {
        WebElement oldBody = wait.until(ExpectedConditions.visibilityOfElementLocated(tableBody));

        List<WebElement> reset = driver.findElements(resetBtn);
        if (!reset.isEmpty() && reset.get(0).isDisplayed() && reset.get(0).isEnabled()) {
            reset.get(0).click();
            waitTableRefresh(oldBody);
            return;
        }

        // Fallback if Reset is missing
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchUsername));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.DELETE);
        driver.findElement(searchBtn).click();
        waitTableRefresh(oldBody);
    }



    private WebElement rowByUsernameExact(String uname) {
        String rowXpath =
                "//div[contains(@class,'oxd-table-body')]//div[contains(@class,'oxd-table-card')]" +
                        "[.//div[contains(@class,'oxd-table-cell')][normalize-space(.)='" + uname + "']]";
        return new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(rowXpath)));
    }
    public void deleteUserByUsername(String uname) {
        // Always filter to the target username
        searchUser(uname);

        if (!driver.findElements(emptyState).isEmpty()) {
            throw new NoSuchElementException("No Records Found for username: " + uname);
        }

        WebElement row = rowByUsernameExact(uname);

        // Click the trash icon inside that row
        By rowTrash = By.xpath(".//button[.//i[contains(@class,'bi-trash')]]");
        WebElement trashBtn = row.findElement(rowTrash);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", trashBtn);
        new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.elementToBeClickable(trashBtn)).click();

        // Confirm
        new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();

        // Wait for the toast (authoritative signal)
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(successDeletedToast));
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.invisibilityOfElementLocated(successDeletedToast));

        // Hard refresh of the grid to avoid stale/virtualized views
        WebElement oldBody = new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(ExpectedConditions.visibilityOfElementLocated(tableBody));
        driver.findElement(searchBtn).click();  // re-run the same search
        try {
            new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.stalenessOf(oldBody));
        } catch (TimeoutException ignored) {}
        waitForResultsOrEmpty();

        // Verify gone; if still present, retry once after a page refresh
        boolean stillThere = false;
        try {
            rowByUsernameExact(uname);
            stillThere = true;
        } catch (TimeoutException ignored) {}

        if (stillThere) {
            // Retry once with a full page refresh (handles slow backend / caching)
            driver.navigate().refresh();
            wait.until(ExpectedConditions.visibilityOfElementLocated(tableRoot));
            waitForResultsOrEmpty();
            searchUser(uname);
            if (!driver.findElements(emptyState).isEmpty()) return; // now gone

            WebElement row2 = rowByUsernameExact(uname);
            WebElement trash2 = row2.findElement(rowTrash);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", trash2);
            new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.elementToBeClickable(trash2)).click();
            new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
            new WebDriverWait(driver, Duration.ofSeconds(12)).until(ExpectedConditions.visibilityOfElementLocated(successDeletedToast));
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(successDeletedToast));
            waitForResultsOrEmpty();

            // Final verification
            try {
                rowByUsernameExact(uname);
                throw new AssertionError("User still present after delete + retry: " + uname);
            } catch (TimeoutException ignored) {}
        }
    }


    // ---------------------------
    // Legacy bulk delete (optional)
    // ---------------------------
    /** Not recommended for this test. Kept only for compatibility. */
    public void deleteFirstRowViaToolbar() {
        int before = getRecordsCount();
        if (before == 0) return;

        List<WebElement> header = driver.findElements(selectAllCheckbox);
        if (!header.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(header.get(0))).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(firstRowCheckbox)).click();
        }

        wait.until(ExpectedConditions.elementToBeClickable(bulkDeleteBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();

        //wait.until(ExpectedConditions.visibilityOfElementLocated(successDeletedToast));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(successDeletedToast));
        waitForResultsOrEmpty();
    }
}



