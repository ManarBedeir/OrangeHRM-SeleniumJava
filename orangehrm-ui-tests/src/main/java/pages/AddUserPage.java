
package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class AddUserPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    public AddUserPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private final By userRoleDropdown = By.xpath("//label[text()='User Role']/../following-sibling::div//i");
    private final By statusDropdown = By.xpath("//label[text()='Status']/../following-sibling::div//i");
    private final By employeeName = By.xpath("//input[@placeholder='Type for hints...']");
    private final By username = By.xpath("//label[text()='Username']/../following-sibling::div//input");
    private final By password = By.xpath("//label[text()='Password']/../following-sibling::div//input");
    private final By confirmPassword = By.xpath("//label[text()='Confirm Password']/../following-sibling::div//input");
    private final By saveBtn = By.xpath("//button[normalize-space()='Save']");



    private void chooseFromDropdown(String visibleText) {
        By option = By.xpath("//div[@role='listbox']//span[normalize-space()='"+ visibleText +"']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void createUser(String uname, String pwd, String employee) {
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        chooseFromDropdown("ESS");

        WebElement emp = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeName));
        emp.clear();
        emp.sendKeys(employee);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement suggestion = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//div[@role='listbox']//span[contains(.,'l')]")));
        suggestion.click();
        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        chooseFromDropdown("Enabled");

        wait.until(ExpectedConditions.visibilityOfElementLocated(username)).sendKeys(uname);
        driver.findElement(password).sendKeys(pwd);
        driver.findElement(confirmPassword).sendKeys(pwd);

        driver.findElement(saveBtn).click();

        // Stable: wait for the success toast (replace the old "Records Found" wait)
        By successToast = By.xpath("//p[contains(@class,'oxd-text') and contains(.,'Successfully Saved')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(successToast));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(successToast));



    }

}
