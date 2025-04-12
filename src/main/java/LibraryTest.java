import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.constraints.IntRange;

import java.util.Collection;

/**
 * Test suite for the Library Management System.
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 */
public class LibraryTest {

    private Library library;
    private Book testBook;
    private Member testMember;

    @BeforeEach
    public void setUp() {
        library = new Library();
        // Create a test Book using the provided Book class.
        testBook = new Book("The Hobbit", "J.R.R. Tolkien", 1937, "978-0547928227", "B001", "Fantasy");
        // Create a test Member using the provided Member class.
        testMember = new Member("Alice", "alice@example.com", "M001");
        library.addBook(testBook);
        library.addMember(testMember);
    }

    // Specification-based tests

    @Test
    public void testAddAndGetBook() {
        // Retrieve by book ID
        Book retrievedById = library.getBookById("B001");
        assertNotNull(retrievedById, "Book should be retrievable by its ID.");
        // Retrieve by name (case-insensitive)
        Book retrievedByName = library.findBookByName("the hobbit");
        assertNotNull(retrievedByName, "Book should be retrievable by its name ignoring case.");
        assertEquals("B001", retrievedByName.getBookId());
    }

    @Test
    public void testRemoveBook() {
        // Remove the book and verify it cannot be retrieved.
        library.removeBook("B001");
        assertNull(library.getBookById("B001"), "Removed book should not be retrievable.");
    }

    @Test
    public void testAddAndRevokeMember() {
        // Retrieve the member then revoke membership.
        Member retrieved = library.getMemberById("M001");
        assertNotNull(retrieved, "Member should be retrievable before revocation.");
        library.revokeMembership("M001");
        assertNull(library.getMemberById("M001"), "Revoked member should not be retrievable.");
    }

    @Test
    public void testCheckoutBook() {
        // Checkout the test book to the test member.
        library.checkoutBook(testMember, testBook);
        assertFalse(testBook.isAvailable(), "After checkout, the book should not be available.");
        assertTrue(testMember.getBorrowedBookList().contains(testBook), "Member's borrowed list should include the checked-out book.");
        // Verify the owner via whoHasBook.
        String owner = library.whoHasBook("B001");
        assertEquals("Alice", owner, "whoHasBook should return the name of the member who has the book.");
    }

    @Test
    public void testReturnBook() {
        // Checkout then return the book.
        library.checkoutBook(testMember, testBook);
        library.returnBook(testMember, testBook);
        assertTrue(testBook.isAvailable(), "After return, the book should be available.");
        assertFalse(testMember.getBorrowedBookList().contains(testBook), "Member's borrowed list should not include the returned book.");
        assertEquals("Not checked out.", library.whoHasBook("B001"),
                "whoHasBook should indicate 'Not checked out.' when the book is returned.");
    }

    @Test
    public void testWhoHasBookNotCheckedOut() {
        // When the book has not been checked out, verify the default message.
        assertEquals("Not checked out.", library.whoHasBook("B001"),
                "whoHasBook should indicate 'Not checked out.' when no member has borrowed the book.");
    }

    @Test
    public void testGetAllBooksAndMembers() {
        Collection<Book> books = library.getAllBooks();
        Collection<Member> members = library.getAllMembers();
        assertTrue(books.contains(testBook), "Library's collection of books should contain the test book.");
        assertTrue(members.contains(testMember), "Library's collection of members should contain the test member.");
    }

    @Test
    public void testCheckoutBookWhenNotAvailable() {
        // Add a second member.
        Member secondMember = new Member("Bob", "bob@example.com", "M002");
        library.addMember(secondMember);
        // Checkout the book with the first member.
        library.checkoutBook(testMember, testBook);
        assertFalse(testBook.isAvailable(), "Book should not be available after checkout.");
        // Attempt to checkout the same book with the second member.
        library.checkoutBook(secondMember, testBook);
        // Verify that the first member still has the book.
        assertTrue(testMember.getBorrowedBookList().contains(testBook),
                "First member should still have the book.");
        assertFalse(secondMember.getBorrowedBookList().contains(testBook),
                "Second member should not have the book since it is already checked out.");
        // whoHasBook must return the first member's name.
        assertEquals("Alice", library.whoHasBook("B001"),
                "whoHasBook should return the name of the member who first checked out the book.");
    }

    // Property-based tests using jqwik

    @Property
    public void propertyCheckoutMakesBookUnavailable(
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String bookName,
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String author,
            @ForAll @IntRange(min = 1900, max = 2100) int year,
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String isbn,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String bookId,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String genre,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String memberId,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String memberName,
            @ForAll @AlphaChars @StringLength(min = 5, max = 10) String emailLocal
    ) {
        Library lib = new Library();
        Book book = new Book(bookName, author, year, isbn, bookId, genre);
        // Construct an email address for the member.
        String memberEmail = emailLocal + "@example.com";
        Member member = new Member(memberName, memberEmail, memberId);
        lib.addBook(book);
        lib.addMember(member);

        lib.checkoutBook(member, book);
        // After checkout, verify that the book is unavailable and present in the member's borrowed list.
        assertFalse(book.isAvailable(), "Property: Book should be unavailable after checkout.");
        assertTrue(member.getBorrowedBookList().contains(book),
                "Property: Member's borrowed list should contain the book after checkout.");
    }

    @Property
    public void propertyReturnMakesBookAvailable(
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String bookName,
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String author,
            @ForAll @IntRange(min = 1900, max = 2100) int year,
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String isbn,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String bookId,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String genre,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String memberId,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String memberName,
            @ForAll @AlphaChars @StringLength(min = 5, max = 10) String emailLocal
    ) {
        Library lib = new Library();
        Book book = new Book(bookName, author, year, isbn, bookId, genre);
        String memberEmail = emailLocal + "@example.com";
        Member member = new Member(memberName, memberEmail, memberId);
        lib.addBook(book);
        lib.addMember(member);

        lib.checkoutBook(member, book);
        lib.returnBook(member, book);
        // After return, verify that the book is available and removed from the member's borrowed list.
        assertTrue(book.isAvailable(), "Property: Book should be available after return.");
        assertFalse(member.getBorrowedBookList().contains(book),
                "Property: Member's borrowed list should not contain the book after return.");
        assertEquals("Not checked out.", lib.whoHasBook(bookId),
                "Property: whoHasBook should indicate 'Not checked out.' after the book is returned.");
    }
}
