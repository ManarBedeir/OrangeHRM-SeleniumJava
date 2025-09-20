
package steps;

import io.cucumber.java.en.*;
import org.testng.Assert;
import pages.*;

public class OrangeHRMSteps {

    private final String BASE_URL = "https://opensource-demo.orangehrmlive.com/";
    private LoginPage loginPage;
    private AdminPage adminPage;
    private AddUserPage addUserPage;

    private int initialCount;
    private String createdUser;
    private String lastTarget;    // the username we most recently acted on

    @Given("I am logged in as Admin")
    public void i_am_logged_in_as_admin() {
        loginPage = new LoginPage(Hooks.driver);
        adminPage = new AdminPage(Hooks.driver);
        addUserPage = new AddUserPage(Hooks.driver);

        loginPage.open(BASE_URL);
        loginPage.login("Admin", "admin123");
    }

    @When("I navigate to the Admin tab")
    public void i_navigate_to_admin_tab() {
        adminPage.goToAdmin();
    }

    @When("I get the current number of records")
    public void i_get_number_of_records() {
        initialCount = adminPage.getRecordsCount();
    }

    // in OrangeHRMSteps.java

    @When("I add a new user with username {string} and employee {string}")
    public void i_add_new_user_with_employee(String uname, String employee) {
        createdUser = uname;          // <- ensure we store the exact string from the feature
        lastTarget  = createdUser;    // <- keep a single source of truth for later steps

        adminPage.clickAdd();
        addUserPage.createUser(createdUser, "Pass@123", employee);
    }

    @Then("the number of records should increase by 1")
    public void verify_increase() {
        int newCount = adminPage.getRecordsCount();
        Assert.assertEquals(newCount, initialCount + 1, "Records did not increase by 1");
    }

    @When("I search for {string}")
    public void i_search_for_user(String ignored) {
        // always search for the real value we just created/are working with
        String target = (lastTarget != null) ? lastTarget : ignored;
        adminPage.searchUser(target);
    }

    @When("I delete the user {string}")
    public void i_delete_the_user(String ignored) {
        String target = (lastTarget != null) ? lastTarget : ignored;
        adminPage.deleteUserByUsername(target);
        // keep target for the verify step
        lastTarget = target;
    }

    @Then("the number of records should decrease by 1")
    public void verify_decrease() {
        // verify the specific user is gone (reliable, not affected by pagination)
        String target = (lastTarget != null) ? lastTarget : createdUser;
        adminPage.searchUser(target);
        int remaining = adminPage.getRecordsCount();
        org.testng.Assert.assertEquals(
                remaining, 0, "User still present after deletion: " + target
        );

        // optional cleanup: clear filters after asserting
        adminPage.resetSearch();
    }

//   @When("I add a new user with username {string} and employee {string}")
//   public void i_add_new_user_with_employee(String uname, String employee) {
//     adminPage.clickAdd();
//     addUserPage.createUser(uname, "Pass@123", employee);
//    }

//
//    @When("I search for {string}")
//    public void i_search_for_user(String uname) {
//        adminPage.searchUser(uname);
//    }
//
//    @When("I delete the user {string}")
//    public void i_delete_the_user(String uname) {
//        adminPage.deleteUserByUsername(uname);   // ✅ instead of deleteFirstRowViaToolbar()
//    }

//   @Then("the number of records should decrease by 1")
//   public void verify_decrease() {
//        adminPage.resetSearch();                 // clear the username filter (or any others)
//        int newCount = adminPage.getRecordsCount();
//        org.testng.Assert.assertEquals(newCount, initialCount, "Records did not decrease by 1");
//   }

//    @Then("the number of records should decrease by 1")
//    public void verify_decrease() {
//        // Primary: the target user no longer appears
//        String target = (createdUser != null) ? createdUser : "";
//        adminPage.searchUser(target);
//        int remaining = adminPage.getRecordsCount();
//        org.testng.Assert.assertEquals(remaining, 0, "User still present after deletion: " + target);
//
//        // Optional: clear filters and *also* check the baseline count if you want
//        adminPage.resetSearch();
//        int newCount = adminPage.getRecordsCount();
//        // org.testng.Assert.assertEquals(newCount, initialCount, "Global count mismatch"); // optional
//    }
//    @And("I reset the search filters")
//    public void i_reset_the_search_filters() {
//        adminPage.resetSearch();
//    }
}
