Feature: Ixigo Hotel Booking

  Scenario Outline: Search hotels with valid details (data from Excel)
    When user navigates to Hotels section
    And user enters destination "<destination>"
    And user selects "<rooms>" room and "<guests>" guests
    And user clicks Search
    Then search results for "<destination>" should be displayed

    Examples:
      | destination | rooms | guests |
      | Mumbai      | 1     | 2      |
