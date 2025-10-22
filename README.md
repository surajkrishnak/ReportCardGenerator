# Student Report Card Generator 🎓

This is a Java Swing application for creating and managing student report cards. It uses a MySQL database to store all student and marks data.

## Features
- Enter student details and marks for 7 subjects.
- Generate a report with total, percentage, grade, and PASS/FAIL result.
- Save all reports to a MySQL database.
- View a table of all saved student reports.

## How to Run

1.  **Database Setup:**
    - Make sure you have MySQL Server running.
    - Create the database and table using the following SQL:
    ```sql
    CREATE DATABASE school;
    USE school;
    CREATE TABLE report_cards (
        roll_no VARCHAR(20) PRIMARY KEY,
        name VARCHAR(100),
        father_name VARCHAR(100),
        mother_name VARCHAR(100),
        maths INT,
        computer_science INT,
        physics INT,
        chemistry INT,
        geography INT,
        history INT,
        hindi INT,
        total INT,
        percentage DOUBLE,
        grade VARCHAR(5),
        result VARCHAR(10)
    );
    ```

2.  **Dependencies:**
    - You must have the [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) JAR file.
    - **Note:** The database password is NOT included for security. You must edit the `connectToDB()` method in `ReportCardGenerator.java` with your own credentials.

3.  **Compile and Run:**
    ```bash
    # Compile
    javac -cp ".;path/to/mysql-connector-j.jar" ReportCardGenerator.java

    # Run
    java -cp ".;path/to/mysql-connector-j.jar" ReportCardGenerator
    ```