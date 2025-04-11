import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Specification-based test class for Book
 * Tests the functionality of the Book class according to its requirements
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
    void setUp() {
        // Create a new book instance before each test
        book = new Book(name, author, year, isbn, bookId, genre);
    }

    @Test
    void testBookCreation() {
        // Specification-based test: Test that book is created with correct parameters
        assertEquals(name, book.getName());
        assertEquals(bookId, book.getBookId());
        assertTrue(book.isAvailable());
    }

    @Test
    void testUpdateBookInfo() {
        // Specification-based test: Test that book info can be updated
        String newName = "The Lord of the Rings";
        String newAuthor = "J.R.R. Tolkien";
        int newYear = 1954;
        String newIsbn = "978-0618640157";
        String newGenre = "Epic Fantasy";

        book.updateBookInfo(newName, newAuthor, newYear, newIsbn, newGenre);
        String bookInfo = book.getBookInfo();

        assertTrue(bookInfo.contains(newName));
        assertTrue(bookInfo.contains(newAuthor));
        assertTrue(bookInfo.contains(String.valueOf(newYear)));
        assertTrue(bookInfo.contains(newIsbn));
        assertTrue(bookInfo.contains(newGenre));
    }

    @Test
    void testBookAvailability() {
        // Specification-based test: Test that book availability can be toggled
        assertTrue(book.isAvailable());
        
        book.setAvailable(false);
        assertFalse(book.isAvailable());
        
        book.setAvailable(true);
        assertTrue(book.isAvailable());
    }

    @Test
    void testGetBookInfo() {
        // Specification-based test: Test that book info contains all expected data
        String bookInfo = book.getBookInfo();
        
        assertTrue(bookInfo.contains(name));
        assertTrue(bookInfo.contains(author));
        assertTrue(bookInfo.contains(String.valueOf(year)));
        assertTrue(bookInfo.contains(isbn));
        assertTrue(bookInfo.contains(bookId));
        assertTrue(bookInfo.contains(genre));
        assertTrue(bookInfo.contains("true")); // availability
    }
}