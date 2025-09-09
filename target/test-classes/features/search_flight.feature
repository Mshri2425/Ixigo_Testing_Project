Feature: Search Round Trip Flights

  @SearchFlight
  Scenario Outline: Search round-trip flights and see results
    Given the user is on the homepage
    When the user selects "Round Trip" trip type
    And the user enters origin as "<from>"
    And the user enters destination as "<to>"
    And the user selects departure date as "<depart_date>"
    And the user selects return date as "<return_date>"
    And the user sets travellers as "<adults>" adults, "<children>" children, "<infants>" infants and class as "<travel_class>"
    And the user clicks Search
    Then search results should be displayed

    Examples:
      | from           | to           | depart_date | return_date | adults | children | infants | travel_class |
      | MAA - Chennai  | BOM - Mumbai | 2025-12-22  | 2025-12-28  | 1      | 0        | 0       | Economy      |
