import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.constraints.IntRange;

/**
 * Test suite for the Book class in the Library Management System.
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 */
public class BookTest {

    private Book book;
    private final String name = "The Hobbit";
    private final String author = "J.R.R. Tolkien";
    private final int year = 1937;
    private final String isbn = "978-0547928227";
    private final String bookId = "B001";
    private final String genre = "Fantasy";

    @BeforeEach
    public void setUp() {
        // Create a new book instance before each test
        book = new Book(name, author, year, isbn, bookId, genre);
    }

    // SPECIFICATION-BASED TESTS
    // These tests verify that the Book class meets its functional requirements

    @Test
    public void testBookCreation() {
        // Specification-based test: Test that book is created with correct parameters
        assertEquals(name, book.getName(), "Book name should match the constructor parameter");
        assertEquals(bookId, book.getBookId(), "Book ID should match the constructor parameter");
        assertTrue(book.isAvailable(), "A newly created book should be available by default");
    }

    @Test
    public void testUpdateBookInfo() {
        // Specification-based test: Test that book info can be updated
        String newName = "The Lord of the Rings";
        String newAuthor = "J.R.R. Tolkien";
        int newYear = 1954;
        String newIsbn = "978-0618640157";
        String newGenre = "Epic Fantasy";

        book.updateBookInfo(newName, newAuthor, newYear, newIsbn, newGenre);
        String bookInfo = book.getBookInfo();

        assertTrue(bookInfo.contains(newName), "Book info should contain the updated name");
        assertTrue(bookInfo.contains(newAuthor), "Book info should contain the updated author");
        assertTrue(bookInfo.contains(String.valueOf(newYear)), "Book info should contain the updated year");
        assertTrue(bookInfo.contains(newIsbn), "Book info should contain the updated ISBN");
        assertTrue(bookInfo.contains(newGenre), "Book info should contain the updated genre");
        assertTrue(bookInfo.contains(bookId), "Book ID should remain unchanged after update");
    }

    @Test
    public void testBookAvailability() {
        // Specification-based test: Test that book availability can be toggled
        assertTrue(book.isAvailable(), "A newly created book should be available by default");

        book.setAvailable(false);
        assertFalse(book.isAvailable(), "Book should be unavailable after setting available to false");

        book.setAvailable(true);
        assertTrue(book.isAvailable(), "Book should be available after setting available back to true");
    }

    // STRUCTURAL TESTS
    // These tests are designed to achieve high code coverage with JaCoCo

    @Test
    public void testGetBookInfo() {
        // Structural test: Test that getBookInfo method returns a properly formatted string
        // with all the expected book properties
        String bookInfo = book.getBookInfo();

        // Verify the bookInfo string contains all book fields
        assertTrue(bookInfo.contains("ID: " + bookId), "Book info should contain the ID field");
        assertTrue(bookInfo.contains("Name: " + name), "Book info should contain the name field");
        assertTrue(bookInfo.contains("Author: " + author), "Book info should contain the author field");
        assertTrue(bookInfo.contains("Year: " + year), "Book info should contain the year field");
        assertTrue(bookInfo.contains("ISBN: " + isbn), "Book info should contain the ISBN field");
        assertTrue(bookInfo.contains("Genre: " + genre), "Book info should contain the genre field");
        assertTrue(bookInfo.contains("Available: true"), "Book info should contain the availability status");
    }

    @Test
    public void testGetBookInfoAfterAvailabilityChange() {
        // Structural test: Test that availability status changes are reflected in getBookInfo
        book.setAvailable(false);
        String bookInfo = book.getBookInfo();

        assertTrue(bookInfo.contains("Available: false"),
                "Book info should show false availability after setting available to false");
    }

    @Test
    public void testGetName() {
        // Structural test: Test the getName getter
        assertEquals(name, book.getName(), "getName should return the book's name");
    }

    @Test
    public void testGetBookId() {
        // Structural test: Test the getBookId getter
        assertEquals(bookId, book.getBookId(), "getBookId should return the book's ID");
    }

    // PROPERTY-BASED TESTS
    // These tests use jqwik to verify properties that should hold for any valid inputs

    @Property
    public void NewBookIsAlwaysAvailable(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookAuthor,
            @ForAll @IntRange(min = 1000, max = 2023) int bookYear,
            @ForAll @StringLength(min = 10, max = 17) String bookIsbn,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String bookGenre
    ) {
        // Property-based test: A newly created book should always be available
        Book testBook = new Book(bookName, bookAuthor, bookYear, bookIsbn, id, bookGenre);
        assertTrue(testBook.isAvailable(), "Property: A newly created book should always be available");
    }

    @Property
    public void propertyBookInfoContainsAllFields(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookAuthor,
            @ForAll @IntRange(min = 1000, max = 2023) int bookYear,
            @ForAll @StringLength(min = 10, max = 17) String bookIsbn,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String bookGenre
    ) {
        // Property-based test: Book info should contain all book properties
        Book testBook = new Book(bookName, bookAuthor, bookYear, bookIsbn, id, bookGenre);
        String bookInfo = testBook.getBookInfo();

        assertTrue(bookInfo.contains(bookName), "Property: Book info should contain the book name");
        assertTrue(bookInfo.contains(bookAuthor), "Property: Book info should contain the author name");
        assertTrue(bookInfo.contains(String.valueOf(bookYear)), "Property: Book info should contain the year");
        assertTrue(bookInfo.contains(bookIsbn), "Property: Book info should contain the ISBN");
        assertTrue(bookInfo.contains(id), "Property: Book info should contain the book ID");
        assertTrue(bookInfo.contains(bookGenre), "Property: Book info should contain the genre");
    }

    @Property
    public void propertyAvailabilityCanBeToggled(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String bookAuthor,
            @ForAll @IntRange(min = 1000, max = 2023) int bookYear,
            @ForAll @StringLength(min = 10, max = 17) String bookIsbn,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String bookGenre
    ) {
        // Property-based test: Book availability can be toggled regardless of other properties
        Book testBook = new Book(bookName, bookAuthor, bookYear, bookIsbn, id, bookGenre);

        // Set to false and verify
        testBook.setAvailable(false);
        assertFalse(testBook.isAvailable(), "Property: Book should be unavailable after setting available to false");

        // Set to true and verify
        testBook.setAvailable(true);
        assertTrue(testBook.isAvailable(), "Property: Book should be available after setting available to true");
    }

    @Property
    public void propertyUpdateReflectedInBookInfo(
            // Original book properties
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String origName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String origAuthor,
            @ForAll @IntRange(min = 1000, max = 2000) int origYear,
            @ForAll @StringLength(min = 10, max = 17) String origIsbn,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String origGenre,

            // Updated book properties
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String newName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String newAuthor,
            @ForAll @IntRange(min = 1000, max = 2023) int newYear,
            @ForAll @StringLength(min = 10, max = 17) String newIsbn,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String newGenre
    ) {
        // Property-based test: Updates to book information should be reflected in getBookInfo
        Book testBook = new Book(origName, origAuthor, origYear, origIsbn, id, origGenre);

        // Update the book info
        testBook.updateBookInfo(newName, newAuthor, newYear, newIsbn, newGenre);
        String bookInfo = testBook.getBookInfo();

        // Check that new values are reflected in the book info
        assertTrue(bookInfo.contains(newName), "Property: Updated name should be reflected in book info");
        assertTrue(bookInfo.contains(newAuthor), "Property: Updated author should be reflected in book info");
        assertTrue(bookInfo.contains(String.valueOf(newYear)), "Property: Updated year should be reflected in book info");
        assertTrue(bookInfo.contains(newIsbn), "Property: Updated ISBN should be reflected in book info");
        assertTrue(bookInfo.contains(newGenre), "Property: Updated genre should be reflected in book info");

        // ID should remain unchanged
        assertTrue(bookInfo.contains(id), "Property: Book ID should remain unchanged after update");
    }
}