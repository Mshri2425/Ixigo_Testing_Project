Feature: Search Trains on ixigo

  @SearchTrain
  Scenario Outline: Search trains between two stations and see results
    Given the user is on the ixigo homepage
    And the user navigates to the "Trains" tab
    When the user enters train origin as "<from>"
    And the user enters train destination as "<to>"
    And the user selects departure date as "<date>"
    And the user clicks the Search button
    Then train search results should be displayed

    Examples:
      | from                        | to              | date       |
      | Chennai - All stations(MAS) | Pune Jn (PUNE)  | 2025-10-22 |
