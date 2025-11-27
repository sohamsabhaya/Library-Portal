# Library Management System

Simple Java-based Library Management System. This repository contains a Java source file and an SQL dump to create or populate the database used by the application.

Files
- `libraryportal.java` — main Java source for the library portal application.
- `library (2).sql` — SQL script (dump) to create tables / seed data for the project.

Prerequisites
- Java Development Kit (JDK) 8 or newer installed and `javac`/`java` available on PATH.
- A relational database (MySQL / MariaDB recommended) if you want persistent storage.
- JDBC driver for your database (e.g., MySQL Connector/J) if you run the Java app against MySQL.

Setup
1. Import the SQL data into your database (example for MySQL):

   ```powershell
   mysql -u <username> -p <database_name> < "library (2).sql"
   ```

2. Place the JDBC driver JAR on the classpath when compiling/running, if required.

Compile & Run (basic)
1. Open a terminal in the `LIBRARY MANAGEMENT SYSTEM` folder.
2. Compile:

   ```powershell
   javac libraryportal.java
   ```

3. Run:

   ```powershell
   java libraryportal
   ```

Notes
- The Java filename is `libraryportal.java`. Ensure the class name inside the file matches the filename (Java is case-sensitive for class/file mapping).
- If the program uses a database connection, you may need to edit connection parameters inside `libraryportal.java` (host, port, database name, user, password) before compiling.
- If you prefer, you can set up a small IDE (Eclipse, IntelliJ IDEA, VS Code with Java extensions) to import, edit, and run the project more conveniently.

Contact / Next steps
- If you want, I can:
  - Inspect `libraryportal.java` and add a small build script or `README`-based run script.
  - Create a `build` folder and include a sample classpath command that includes the JDBC driver.

Run script (PowerShell)
- A small helper script `run.ps1` is included to compile and run the application from PowerShell.
- Example (with a MySQL Connector/J jar placed in the same folder):

   ```powershell
   .\run.ps1 -JdbcJar ".\mysql-connector-java-8.0.33.jar"
   ```

- Example (if the JDBC driver is already available on the runtime classpath):

   ```powershell
   .\run.ps1
   ```

- The script compiles `libraryportal.java` and runs the `libraryportal` class. It uses `;` as a classpath separator for Windows.

Notes about database connectivity
- The application currently uses the following DB settings inside the source:
   - URL: `jdbc:mysql://localhost:3306/library`
   - User: `root`
   - Password: `` (empty)
- If your database uses different credentials or host/port, update the constants in `libraryportal.java` (inside the `Library` inner class) before compiling.
