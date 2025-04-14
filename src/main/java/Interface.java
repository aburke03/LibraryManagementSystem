import java.util.*;

public class Interface {
    private Library library = new Library();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        int choice;
        do {
            System.out.println("\nLIBRARY MANAGEMENT SYSTEM:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Add Member");
            System.out.println("4. Remove Member");
            System.out.println("5. Checkout Book");
            System.out.println("6. Return Book");
            System.out.println("7. View All Books");
            System.out.println("8. View All Members");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addBook();
                case 2 -> removeBook();
                case 3 -> addMember();
                case 4 -> removeMember();
                case 5 -> checkoutBook();
                case 6 -> returnBook();
                case 7 -> viewBooks();
                case 8 -> viewMembers();
                case 9 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 9);
    }

    private void addBook() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter year: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        Book book = new Book(title, author, year, isbn, bookId, genre);
        library.addBook(book);
    }

    private void removeBook() {
        System.out.print("Enter book ID to remove: ");
        String id = scanner.nextLine();
        library.removeBook(id);
    }

    private void addMember() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter member ID: ");
        String id = scanner.nextLine();
        Member member = new Member(name, email, id);
        library.addMember(member);
    }

    private void removeMember() {
        System.out.print("Enter member ID to remove: ");
        String id = scanner.nextLine();
        library.revokeMembership(id);
    }

    private void checkoutBook() {
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
        library.checkoutBook(member, book);
    }

    private void returnBook() {
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
    }

    private void viewBooks() {
        for (Book book : library.getAllBooks()) {
            System.out.println(book.getBookInfo());
        }
    }

    private void viewMembers() {
        for (Member member : library.getAllMembers()) {
            System.out.println(member.getMemberInfo());
        }
    }
}
