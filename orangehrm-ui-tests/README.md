# OrangeHRM UI Tests (Selenium + Cucumber + TestNG, POM)

Implements the assessment scenario:
1. Login to OrangeHRM demo
2. Go to **Admin**
3. Read number of records
4. Add a new user
5. Verify count increased by 1
6. Search user
7. Delete user
8. Verify count decreased by 1

## Tech
- Java 17
- Selenium 4
- Cucumber 7
- TestNG
- WebDriverManager

## Structure
```
src
 ├─ main/java/pages
 │   ├─ LoginPage.java
 │   ├─ AdminPage.java
 │   └─ AddUserPage.java
 └─ test/java
     ├─ steps
     │   ├─ Hooks.java
     │   └─ OrangeHRMSteps.java
     └─ runner
         └─ TestRunner.java
src/test/resources/features/orangehrm.feature
```

## Run
```bash
mvn -q test
```
Generates an HTML report at `target/cucumber-report.html`.

### Tip: unique username
The feature uses a placeholder `testuser_%ts%`. Replace it with a unique value (or parameterize via Scenario Outline) before running to avoid collisions on the shared demo.
