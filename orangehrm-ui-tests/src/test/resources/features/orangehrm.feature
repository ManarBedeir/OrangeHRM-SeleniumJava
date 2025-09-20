@ui
Feature: Manage Users in OrangeHRM (UI)

  Background:
    Given I am logged in as Admin
    When I navigate to the Admin tab
    And I get the current number of records

  Scenario Outline: Add and delete a user from Admin
    And I add a new user with username "<username>" and employee "<employee>"
    Then the number of records should increase by 1
    When I search for "<username>"
    And I delete the user "<username>"
    Then the number of records should decrease by 1


  # the test will add+delete each.
    Examples:
      | username    | employee |
      | manarb_001  | a  |
      | manarb_002  |  b|
