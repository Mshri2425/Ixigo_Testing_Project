Feature: Flight Search Results - Filters

  As a user
  I want to apply specific filters on the flight search results page
  So that I can narrow down flights and choose the first available one

  Background:
    Given the user is on the homepage
    When the user selects "Round Trip" trip type
    And the user enters origin as "MAA - Chennai"
    And the user enters destination as "PNQ - Pune"
    And the user selects departure date as "2025-12-05"
    And the user selects return date as "2025-12-10"
    And the user sets travellers as "1" adults, "0" children, "0" infants and class as "Economy"
    And the user clicks Search
    Then search results should be displayed

  @Filter
  Scenario Outline: Apply a filter on flight results and select the first flight
    When the user applies "<filter_type>" filter
    Then "<expected_result>" should be displayed
    And the user selects the first available flight

    Examples:
    | filter_type                    | expected_result                                                     |
    | Non-Stop                       | Only non-stop flights should be displayed                           |
    | IndiGo                         | Only IndiGo flights should be displayed                             |
    | Departure from MAA Before 6AM  | Only flights departing from Chennai before 6AM should be displayed  |
    | Arrival at PNQ Before 6AM      | Only flights arriving at Pune before 6AM should be displayed        |
