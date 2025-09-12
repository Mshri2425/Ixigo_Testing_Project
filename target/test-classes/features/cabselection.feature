Feature: Cab Selection and Booking

  Scenario Outline: Positive cab selection and booking flow
    Given user is on the cab results page
    When user selects "<cab_type>" cab type
    And user books "<provider>" cab
    Then booking confirmation page should be displayed

    Examples:
      | cab_type   | provider   |
      | Hatchback  | GOZO CABS  |
