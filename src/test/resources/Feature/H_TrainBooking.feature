Feature: Book a Train Ticket on ixigo

  @BookTrain
  Scenario Outline: Search and book a train with 2A class
    Given the user has searched trains from "<from>" to "<to>" on "<date>"
    When the user selects the first available train
    And the user chooses class "<class>"
    And the user clicks on Show Availability
    And the user clicks on Book for the first available option
    Then the login popup should be displayed
    And the test should stop

    Examples:
      | from                        | to              | date        | class |
      | Chennai - All stations(MAS) | Pune Jn (PUNE)  | 2025-10-22  | 2A    |
