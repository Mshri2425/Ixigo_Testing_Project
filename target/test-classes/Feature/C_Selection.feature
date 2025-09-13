Feature: Applying Filters

  Scenario Outline: Application of filters for selecting the flight
    Given the user is on the selection page
    And the user clicks third filter departure as "<Departure>"
    And the user clicks fourth filter arrival as "<Arrival>"
    Then select the first available flight

  Examples:
    | Departure                         | Arrival                     |
    | Departure from Chennai 06AM-12PM  | Arrival at Mumbai 06AM-12PM |
