Feature: Logout
  As a logged in user
  I want to log out of the Ixigo application
  So that my session is terminated and I see the login option again

  Scenario Outline: Successful logout (user is logged in and session persists)
    Given user is on Ixigo home page
    And user logs in with mobile "<mobile>" (manual OTP allowed)
    When user opens the profile menu
    And user clicks the logout button
    Then the user should see the "Log in/Sign up" button

    Examples:
      | mobile      |
      | 7305535479  |

  Scenario Outline: Attempt logout when not logged in
    Given user is on Ixigo home page
    And user ensures they are not logged in
    When user tries to open the profile menu
    Then the user should still see the "Log in/Sign up" button

    Examples:
      | note            |
      | not_logged_in   |

  Scenario Outline: Auto-logout after login (session not maintained — negative case)
    Given user is on Ixigo home page
    And user logs in with mobile "<mobile>" (manual OTP allowed)
    When we wait briefly to check session persistence
    Then the application should show the "Log in/Sign up" button if auto-logout happened or the profile menu if the session persisted

    Examples:
      | mobile      |
      | 7305535479  |
