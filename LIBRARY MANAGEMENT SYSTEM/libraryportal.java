import java.sql.*;
import java.util.Scanner;

class Queue {
    private Book[] queue;
    private int front, rear, size;

    public Queue(int capacity) {
        queue = new Book[capacity];
        front = -1;
        rear = -1;
        size = 0;
    }

    boolean isEmpty() {
        return size == 0;
    }

    boolean isFull() {
        return size == queue.length;
    }

    void enqueue(Book book) {
        if (isFull()) {
            System.out.println("Queue is full. Cannot enqueue.");
            return;
        }
        if (front == -1) {
            front = 0;
        }
        rear = (rear + 1) % queue.length;
        queue[rear] = book;
        size++;
    }

    Book dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty. Cannot dequeue.");
            return null;
        }
        Book book = queue[front];
        front = (front + 1) % queue.length;
        size--;
        if (isEmpty()) {
            front = -1;
            rear = -1;
        }
        return book;
    }

    int getSize() {
        return size;
    }
}

class Book {
    private int id;
    private String title;
    private String author;
    private int quantity;

    public Book(int id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    int getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getAuthor() {
        return author;
    }

    int getQuantity() {
        return quantity;
    }

    void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

class libraryportal {
    static Scanner scanner = new Scanner(System.in);

    static class User {
        private int id;
        private static String name;
        private String password;

        public User(int id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }

        int getId() {
            return id;
        }

        String getName() {
            return name;
        }

        String getPassword() {
            return password;
        }
    }

    class Library extends Queue {
        final static String DB_URL = "jdbc:mysql://localhost:3306/library";
        final static String USER = "root";
        final static String PASS = "";
        private static final String ADMIN_NAME = "admin";
        private static final String ADMIN_PASSWORD = "password";

        public Library(int capacity) {
            super(capacity);
        }

   
        boolean verifyUser(User user) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn
                            .prepareStatement("SELECT * FROM user WHERE uname = ? AND upassword = ?")) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getPassword());
                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Returns true if a matching user is found
            }
        }

        // Method to verify admin
        boolean verifyAdmin(String name, String password) {
            return ADMIN_NAME.equals(name) && ADMIN_PASSWORD.equals(password);
        }

        // Method to get all available books
        Queue getAllAvailableBooks() throws SQLException {
            Queue queue = new Queue(100); // Assuming a maximum of 100 available books
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM book WHERE bquantity > 0")) {
                while (rs.next()) {
                    Book book = new Book(rs.getInt("bid"), rs.getString("btitle"), rs.getString("bauthor"),
                            rs.getInt("bquantity"));
                    queue.enqueue(book);
                }
            }
            System.out.println("Fetched " + queue.getSize() + " available books.");
            return queue;
        }

        Queue searchBooksByName(String bookName) throws SQLException {
            Queue matchingQueue = new Queue(100); // Assuming a maximum of 100 matching books
            Queue allBooks = getAllAvailableBooks(); // Fetch all available books

            while (!allBooks.isEmpty()) {
                Book book = allBooks.dequeue();
                if (book.getTitle().equalsIgnoreCase(bookName)) {
                    matchingQueue.enqueue(book);
                }
            }
            return matchingQueue; // Return the queue of matching books (can be empty)
        }

        // Method to remove a book
        void removeBook(int bookId) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM book WHERE bid = ?")) {
                stmt.setInt(1, bookId);
                stmt.executeUpdate();
                System.out.println("Book removed with ID: " + bookId);
            }
        }

        // Method to get all users
        void getAllUsers() throws SQLException {
            System.out.println("Users:");
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM user")) {
                while (rs.next()) {
                    int userId = rs.getInt("uid");
                    String userName = rs.getString("uname");
                    String userPassword = rs.getString("upassword");
                    System.out.println("ID: " + userId + " Name: " + userName);
                }
            }
        }

        // Method to remove a user using a stored procedure
        void removeUser(int userId) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    CallableStatement stmt = conn.prepareCall("{call remove_user(?)}")) {
                stmt.setInt(1, userId);
                stmt.execute();
                System.out.println("User removed with ID: " + userId);
            }
        }

        // Method to return a book
        void returnBook(int bookId, String username) throws SQLException {
            Book book = getBookById(bookId);

            book.setQuantity(book.getQuantity() + 1);

            updateBookQuantity(book);

            insertReturnRecord(username, bookId, book.getTitle());

            System.out.println("Book with ID: " + bookId + " returned by " + username);
        }

        // Method to borrow a book
        void borrowBook(int bookId, String username) throws SQLException {
            // Retrieve the book from the database
            Book book = getBookById(bookId);

            if (book.getQuantity() >= 0) {
                book.setQuantity(book.getQuantity() - 1);

                updateBookQuantity(book);

                insertBorrowRecord(username, bookId, book.getTitle());

                System.out.println("Book with ID: " + bookId + " borrowed by " + username);
            } else {
                System.out.println("Book with ID: " + bookId + " is not available for borrowing.");
            }
        }

        void addUser(String name, String password) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    CallableStatement stmt = conn.prepareCall("{call add_user(?, ?)}")) {
                stmt.setString(1, name);
                stmt.setString(2, password);
                stmt.execute();

                try (PreparedStatement getIdStmt = conn.prepareStatement("SELECT LAST_INSERT_ID()")) {
                    ResultSet rs = getIdStmt.executeQuery();
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        System.out.println("User added successfully!");
                        System.out.println("User ID: " + userId + ", Name: " + name + ", Password: " + password);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        }

        private Book getBookById(int bookId) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE bid = ?")) {
                stmt.setInt(1, bookId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Book(rs.getInt("bid"), rs.getString("btitle"), rs.getString("bauthor"),
                            rs.getInt("bquantity"));
                }
            }
            throw new SQLException("Book not found with ID: " + bookId);
        }

        private void updateBookQuantity(Book book) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement("UPDATE book SET bquantity = ? WHERE bid = ?")) {
                stmt.setInt(1, book.getQuantity());
                stmt.setInt(2, book.getId());
                stmt.executeUpdate();
            }
        }

        private void insertBorrowRecord(String username, int bookId, String bookTitle) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO borrow (uname, uid, bid, btitle) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, username);
                stmt.setInt(2, getUserId(username)); // Assuming a method to get user ID by username
                stmt.setInt(3, bookId);
                stmt.setString(4, bookTitle);
                stmt.executeUpdate();
            }
        }

        private void insertReturnRecord(String username, int bookId, String bookTitle) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO returned (uname, uid, bid, btitle) VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, username);
                stmt.setInt(2, getUserId(username)); // Assuming a method to get user ID by username
                stmt.setInt(3, bookId);
                stmt.setString(4, bookTitle);
                stmt.executeUpdate();
            }
        }

        private int getUserId(String username) throws SQLException {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    PreparedStatement stmt = conn.prepareStatement("SELECT uid FROM user WHERE uname = ?")) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("uid");
                }
            }
            throw new SQLException("User not found with username: " + username);
        }
    }

    void librarianMenu(Library library) throws SQLException {
        while (true) {
            System.out.println("\nLibrarian Menu:");
            System.out.println("1. View Available Books");
            System.out.println("2. Remove Book");
            System.out.println("3. View Users");
            System.out.println("4. Remove User");
            System.out.println("5. Search Books by Name");
            System.out.println("6. Logout");
            int librarianChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (librarianChoice) {
                case 6:
                    return;
                case 1:
                    Queue availableBooks = library.getAllAvailableBooks();
                    System.out.println("Available Books:");
                    while (!availableBooks.isEmpty()) {
                        Book book = availableBooks.dequeue();
                        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: "
                                + book.getAuthor() + ", Quantity: " + book.getQuantity());
                    }
                    break;
                case 2:
                    System.out.print("Enter book ID to remove: ");
                    int bookId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    library.removeBook(bookId);
                    break;
                case 3:
                    library.getAllUsers();
                    break;
                case 4:
                    System.out.print("Enter user ID to remove: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    library.removeUser(userId);
                    break;
                case 5:
                    System.out.print("Enter book name to search: ");
                    String bookName = scanner.nextLine();
                    Queue matchingBooks = library.searchBooksByName(bookName);
                    System.out.println("Matching Books:");
                    while (!matchingBooks.isEmpty()) {
                        Book book = matchingBooks.dequeue();
                        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() +
                                ", Author: " + book.getAuthor() + ", Quantity: " + book.getQuantity());
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    void userMenu(Library library) throws SQLException {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. View Available Books");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Logout");
            int userChoice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (userChoice) {
                case 5:
                    return;
                case 1:
                    Queue availableBooks = library.getAllAvailableBooks();
                    System.out.println("Available Books:");
                    while (!availableBooks.isEmpty()) {
                        Book book = availableBooks.dequeue();
                        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: "
                                + book.getAuthor() + ", Quantity: " + book.getQuantity());
                    }
                    break;
                case 2:
                    System.out.print("Enter book name to search: ");
                    String bookName = scanner.nextLine();
                    Queue matchingBooks = library.searchBooksByName(bookName);
                    System.out.println("Matching Books:");
                    while (!matchingBooks.isEmpty()) {
                        Book book = matchingBooks.dequeue();
                        System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() +
                                ", Author: " + book.getAuthor() + ", Quantity: " + book.getQuantity());
                    }
                    break;
                case 3:
                    System.out.print("Enter book ID to borrow: ");
                    int bookId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    library.borrowBook(bookId, User.name);
                    break;
                case 4:
                    System.out.print("Enter book ID to return: ");
                    bookId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    library.returnBook(bookId, User.name);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method for user login
    void login(Library library) throws SQLException {
        while (true) {
            System.out.println("\nLogin as:");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 3:
                    return;
                case 1:
                    // Admin login
                    System.out.print("Enter admin name: ");
                    String adminName = scanner.nextLine();
                    System.out.print("Enter admin password: ");
                    String adminPassword = scanner.nextLine();

                    if (library.verifyAdmin(adminName, adminPassword)) {
                        System.out.println("Admin login successful!");
                        librarianMenu(library);
                    } else {
                        System.out.println("Invalid admin credentials.");
                    }
                    break;
                case 2:
                    // User login
                    System.out.println("Login as:");
                    System.out.println("1. New User");
                    System.out.println("2. Existing User");
                    int loginChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    switch (loginChoice) {
                        case 1: {
                            System.out.println("Enter name:");
                            String name = scanner.nextLine();
                            System.out.println("Enter password:");
                            String password = scanner.nextLine();
                            library.addUser(name, password);
                        }
                            break;
                        case 2: {
                            System.out.print("Enter your name: ");
                            String userName = scanner.nextLine();
                            System.out.print("Enter your password: ");
                            String userPassword = scanner.nextLine();
                            User user = new User(0, userName, userPassword); 

                            if (library.verifyUser(user)) {
                                System.out.println("User login successful!");
                                userMenu(library);
                            } else {
                                System.out.println("Invalid user credentials.");
                            }
                            break;
                        }
                    }
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading JDBC driver: " + e.getMessage());
        }
        libraryportal system = new libraryportal();
        Library library = system.new Library(100); // Initialize library with a capacity of 100 books
        System.out.println("Welcome to the Library Management System!");
        system.login(library); 
    }
}