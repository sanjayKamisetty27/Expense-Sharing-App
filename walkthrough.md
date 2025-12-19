# Walkthrough - Expense Sharing Application Testing & Refactoring

## 1. Frontend Refactoring
We have reorganized the Thymeleaf templates into a structured directory layout:
- `src/main/resources/templates/users/`
- `src/main/resources/templates/groups/`
- `src/main/resources/templates/expenses/`
- `src/main/resources/templates/balances/`
- `src/main/resources/templates/fragments/`

Updated all Controllers to point to these new locations.

## 2. Backend Testing
Implemented a comprehensive testing suite:
- **Unit Tests**: `UserServiceTest`, `GroupServiceTest`, `ExpenseServiceTest`.
    - Covered Equal, Exact, and Percentage split logic.
- **Integration Tests**: `ExpenseControllerIntegrationTest`.
    - Verifies end-to-end flow using H2 in-memory database.
- **Dependencies**: Added H2 and Selenium to `pom.xml`.

## 3. UI Automation
Implemented Selenium-based UI tests in `ExpenseSharingUITest.java`:
- Validates Home Page, User Creation, and Group Creation.
- Runs in headless Chrome mode for CI compatibility.

## 4. How to Run Tests
Run the following command in the terminal to execute all tests:
```bash
mvn test
```

## 5. Documentation
- `TESTING.md` contains detailed manual test scenarios and SQL queries.
