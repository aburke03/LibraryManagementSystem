import java.util.*;

/**
 * Represents a member of the library.
 * Tracks identifying details and a list of borrowed books.
 */

public class Member {
    private String name;
    private String email;
    private String memberId;
    private List<Book> borrowedBooks;

    /**
     * Constructs a new member with the given name, email, and ID.
     *
     * @throws IllegalArgumentException if any field is null.
     */
    public Member(String name, String email, String memberId) {
        if (name == null || email == null || memberId == null) {
            throw new IllegalArgumentException("null field!");
        }
        this.name = name;
        this.email = email;
        this.memberId = memberId;
        this.borrowedBooks = new ArrayList<>();
    }


    //Returns a formatted string with member details.
    public String getMemberInfo() {
        return String.format("ID: %s | Name: %s | Email: %s\n", memberId, name, email);
    }

    //Returns the list of books currently borrowed by the member.
    public List<Book> getBorrowedBookList() {
        return borrowedBooks;
    }

    //Adds a book to the list of borrowed books if not already present.
    public void addBorrowedBook(Book book) {
        if (!borrowedBooks.contains(book)) borrowedBooks.add(book);
    }

    /**
     * Removes a book from the borrowed list by its ID.
     *
     * @throws IllegalArgumentException if the ID is null.
     */
    public void removeBorrowedBook(String bookId) {
        if (bookId == null) throw new IllegalArgumentException("null field!");
        borrowedBooks.removeIf(book -> book.getBookId().equals(bookId));
    }

    //Updates the member's name and email.
    public void updateMemberInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }


    //Returns the member's name.
    public String getName() {
        return name;
    }


    //Returns the member's email address.
    public String getEmail() {
        return email;
    }


    //Returns the member's unique ID.
    public String getMemberId() {
        return memberId;
    }
}
