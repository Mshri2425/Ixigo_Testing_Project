Feature: Flight Search Results - Filters

  As a user
  I want to apply specific filters on the flight search results page
  So that I can narrow down flights and choose the first available one

  Background:
    Given the user is on the homepage
    When the user selects "Round Trip" trip type
    And the user enters origin as "Chennai"
    And the user enters destination as "Mumbai"
    And the user sets travellers as "1" adults, "0" children, "0" infants and class as "Economy"
    And the user clicks "Search"
    Then search results should be displayed

  @Filter
  Scenario Outline: Apply a filter on flight results and select the first flight
    When the user applies the "<filter_type>" filter
    Then "<expected_result>" should be displayed
    And the user selects the first available flight

    Examples:
      | filter_type                        | expected_result                                                     |
      | Non-Stop                           | Only non-stop flights should be displayed                           |
      | IndiGo                             | Only IndiGo flights should be displayed                             |
      | Departure from Chennai Before 6AM  | Only flights departing from Chennai before 06:00 should be displayed |
      | Arrival at Mumbai Before 6AM       | Only flights arriving at Mumbai before 06:00 should be displayed     |
