import java.util.*;

/**
 * Manages the collections of books and members in the library system.
 * Provides methods for CRUD operations and book checkout/return processes.
 */

public class Library {
    private Map<String, Book> allBooks = new HashMap<>();
    private Map<String, Member> allMembers = new HashMap<>();

    //Adds a new book to the library.
    public void addBook(Book book) {
        allBooks.put(book.getBookId(), book);
    }

    //Removes a book from the library by its ID.
    public void removeBook(String bookId) {
        allBooks.remove(bookId);
    }

    //Adds a new member to the library.
    public void addMember(Member member) {
        allMembers.put(member.getMemberId(), member);
    }

    //Revokes a member's membership by their ID.
    public void revokeMembership(String memberId) {
        allMembers.remove(memberId);
    }

    //Returns the name of the member who has a book checked out, if any.
    public String whoHasBook(String bookId) {
        for (Member member : allMembers.values()) {
            for (Book book : member.getBorrowedBookList()) {
                if (book.getBookId().equals(bookId)) {
                    return member.getName();
                }
            }
        }
        return "Not checked out.";
    }

    //Returns a collection of all members in the system.
    public Collection<Member> getAllMembers() {
        return allMembers.values();
    }

    //Returns a collection of all books in the library.
    public Collection<Book> getAllBooks() {
        return allBooks.values();
    }

    //Finds a book by its name, ignoring case.
    public Book findBookByName(String name) {
        for (Book book : allBooks.values()) {
            if (book.getName().equalsIgnoreCase(name)) {
                return book;
            }
        }
        return null;
    }

    //Retrieves a book by its ID.
    public Book getBookById(String bookId) {
        return allBooks.get(bookId);
    }

    //Retrieves a member by their ID.
    public Member getMemberById(String memberId) {
        return allMembers.get(memberId);
    }

    //Checks out a book to a member if the book is available.
    public void checkoutBook(Member member, Book book) {
        if (book.isAvailable()) {
            member.addBorrowedBook(book);
            book.setAvailable(false);
        }
    }

    //Returns a book from a member and updates its availability.
    public void returnBook(Member member, Book book) {
        member.removeBorrowedBook(book.getBookId());
        book.setAvailable(true);
    }
}