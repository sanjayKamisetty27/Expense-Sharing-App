# Expense Sharing Application

A simple Splitwise-like application built with Spring Boot, MySQL, and Thymeleaf.

## Features
- User Management: Create and view users.
- Group Management: Create groups and add members.
- Expense Management: Add expenses with Equal, Exact, or Percentage splits.
- Balance Tracking: View simplified balances within a group.
- Settlement: (Implicit via balance view)

## Prerequisites
- Java 17+
- Maven
- MySQL 8+

## Setup
1. **Database Setup**:
   - Create a MySQL database named `expense_db`.
   - Update `src/main/resources/application.properties` with your MySQL username and password.

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/expense_db
   spring.datasource.username=root
   spring.datasource.password=root
   ```

2. **Build and Run**:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Application**:
   - Open your browser and go to `http://localhost:8080`.

## Usage
1. Create Users.
2. Create a Group.
3. Add Users to the Group.
4. Add Expenses to the Group.
5. View Balances to see who owes whom.
