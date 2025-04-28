import java.util.*;

/**
 * Command-line interface for interacting with the Library management system,
 * with full-time vs. volunteer authentication.
 */
public class Interface {
    private Library library         = new Library();
    private LibraryAccounts accounts = new LibraryAccounts();
    private Scanner scanner;

    // Authentication state
    private boolean isFullTime          = false;
    private String  currentLibrarianCode = null;

    public void start() {
        scanner = new Scanner(System.in);
        // Print group names on startup
        System.out.println("----------------------------------------------------------------");
        System.out.println("                   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("                         GROUP G");
        System.out.println("     Austin Burke, Logan Remondet, Emory Kiser, Ricky Liang");
        System.out.println("----------------------------------------------------------------");

        // Entry point: authenticate user and start main CLI loop
        authenticateUser();

        while (true) {
            System.out.println("\nLIBRARY MANAGEMENT SYSTEM:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add Member");
            System.out.println("4. Remove Member");
            System.out.println("5. Checkout/Purchase Book");
            System.out.println("6. Return Book");
            System.out.println("7. View All Books");
            System.out.println("8. View All Members");
            System.out.println("9. Add Donation");
            System.out.println("10. Withdraw Salary");
            System.out.println("11. Exit");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine();
            int choice;
            try {
                // Parse user menu choice
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.");
                continue;
            }

            // Dispatch user choice to corresponding action
            switch (choice) {
                case 1  -> addBook();
                case 2  -> removeBook();
                case 3  -> addMember();
                case 4  -> removeMember();
                case 5  -> checkoutBook();
                case 6  -> returnBook();
                case 7  -> viewBooks();
                case 8  -> viewMembers();
                case 9  -> addDonation();
                case 10 -> withdrawSalary();
                case 11 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void authenticateUser() {
        // Prompt for and validate fulltime librarian code
        System.out.print("Enter Full‑Time Librarian Code (or press Enter to access as a Volunteer Librarian): ");
        String code = scanner.nextLine().trim();
        if (!code.isEmpty() && accounts.getLibrarians().authenticate(code)) {
            isFullTime = true;
            currentLibrarianCode = code;
            System.out.println("Authenticated as full‑time librarian: "
                    + accounts.getLibrarians().getName(code));
        } else {
            isFullTime = false;
            System.out.println("Proceeding as volunteer librarian (limited permissions).");
        }
    }

    private Book promptBookDetails() {
        // Collect book details from CLI and return a new Book instance
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter year: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year; defaulting to 0.");
            year = 0;
        }
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();
        return new Book(title, author, year, isbn, bookId, genre);
    }

    private void addBook() {
        // Add a new book to the library
        Book book = promptBookDetails();
        if (library.addBook(book)) System.out.println("Added book: " + book.getBookInfo());
    }

    private void removeBook() {
        // Remove a book by its ID if it exists
        System.out.print("Enter book ID to remove: ");
        String id = scanner.nextLine();
        library.removeBook(id);
        System.out.println("Removed book ID " + id + " (if it existed).");
    }

    private void addMember() {
        // Create and add a new member to the library
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter member ID: ");
        String id = scanner.nextLine();
        Member member = new Member(name, email, id);
        if (library.addMember(member)) System.out.println("Added member: " + member.getMemberInfo());
    }

    private void removeMember() {
        // Revoke a member's membership (fulltime only)
        if (!isFullTime) {
            System.out.println("Only full‑time librarians may revoke memberships.");
            return;
        }
        System.out.print("Enter member ID to remove: ");
        String id = scanner.nextLine();
        library.revokeMembership(id);
        System.out.println("Revoked membership for ID " + id + " (if it existed).");
    }

    private void checkoutBook() {
        // Handle book checkout, including purchase flow for fulltime librarians
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        Member member = library.getMemberById(memberId);
        if (member == null) {
            System.out.println("Invalid member ID.");
            return;
        }

        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();
        Book book = library.getBookById(bookId);

        if (book == null) {
            if (isFullTime) {
                System.out.print("Book not found. Purchase and add it? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    // Attempt book purchase and handle insufficient funds
                    try {
                        double cost = accounts.orderNewBook();
                        accounts.getLibrarians().recordBookPurchase(currentLibrarianCode, cost);
                        System.out.println("Purchased for $" + cost);
                        System.out.println("Now enter its details:");
                        Book newBook = promptBookDetails();
                        library.addBook(newBook);
                        book = newBook;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Purchase failed: " + e.getMessage());
                        return;
                    }
                } else {
                    System.out.println("Purchase cancelled; checkout aborted.");
                    return;
                }
            } else {
                System.out.println("Book not found. Please call a full‑time librarian for assistance.");
                return;
            }
        }

        library.checkoutBook(member, book);
        System.out.println("Checked out \"" + book.getName() + "\" to " + member.getName());
    }

    private void returnBook() {
        // Handle returning a checked out book
        System.out.print("Enter member ID: ");
        String memberId = scanner.nextLine();
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();
        Member member = library.getMemberById(memberId);
        Book book = library.getBookById(bookId);
        if (member == null || book == null) {
            System.out.println("Invalid member or book ID.");
            return;
        }
        library.returnBook(member, book);
        System.out.println("Returned \"" + book.getName() + "\" from " + member.getName());
    }

    private void viewBooks() {
        // Display all books currently in the library
        for (Book book : library.getAllBooks()) {
            System.out.println(book.getBookInfo());
        }
        if (library.getAllBooks().isEmpty()) System.out.println("No books currently in System!");
    }

    private void viewMembers() {
        // Display all registered library members
        for (Member member : library.getAllMembers()) {
            System.out.println(member.getMemberInfo());
        }
        if (library.getAllMembers().isEmpty()) System.out.println("No members currently in System!");
    }

    private void addDonation() {
        if (!isFullTime) {
            System.out.println("Only full‑time librarians may add donations.");
            return;
        }
        System.out.print("Enter donation amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }
        // catch any IllegalArgumentException from negative or other invalid amounts
        try {
            accounts.addDonation(amount);
            System.out.println("Donation added. New balance: $" + accounts.getOperatingCashBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdrawSalary() {
        // Withdraw salary from operating cash and record it (fulltime only)
        if (!isFullTime) {
            System.out.println("Only full‑time librarians may withdraw salary.");
            return;
        }
        System.out.print("Enter salary withdrawal amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }
        try {
            accounts.withdrawSalary(currentLibrarianCode, amount);
            System.out.println("Withdrew $" + amount + ". New balance: $"
                    + accounts.getOperatingCashBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
