import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.constraints.AlphaChars;

/**
 * Test suite for the Interface class (CLI).
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 */
public class InterfaceTest {

    private Interface cli;

    @BeforeEach
    public void setUp() {
        cli = new Interface();
    }

    // SPECIFICATION-BASED & STRUCTURAL TESTS

    @Test
    public void testAddAndRemoveBook() {
        // Add a book and then remove it using CLI simulation
        String input = "1\nBookTitle\nAuthorName\n2000\nISBN123456\nBOOK123\nFiction\n2\nBOOK123\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Add and remove book should not throw.");
    }

    @Test
    public void testAddRemoveAndAttemptCheckoutWithMissingBook() {
        // Add member, attempt checkout with invalid book, then remove member
        String input =
                "3\nBob\nbob@example.com\nMEMBER1\n" +
                        "5\nMEMBER1\nBOOKDOESNOTEXIST\n" +
                        "4\nMEMBER1\n" +
                        "9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Member ops and invalid checkout should not throw.");
    }

    @Test
    public void testNonIntegerMenuChoice() {
        // Simulate inputting a non-numeric menu option
        String input = "abc\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Non-integer input should be handled as invalid.");
    }

    @Test
    public void testCheckoutAndReturnValidBook() {
        // Add book + member, checkout and return the book, then exit
        String input =
                "1\nTitle\nAuthor\n2020\nISBN9\nB1\nSci-Fi\n" +
                        "3\nAlice\nalice@example.com\nM1\n" +
                        "5\nM1\nB1\n" +
                        "6\nM1\nB1\n" +
                        "9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Checkout and return should succeed.");
    }

    @Test
    public void testCheckoutInvalidIds() {
        // Attempt to checkout a book with invalid member and book IDs
        String input = "5\nBADMEMBER\nBADBOOK\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid checkout should not crash.");
    }

    @Test
    public void testReturnInvalidIds() {
        // Attempt to return a book with invalid member and book IDs
        String input = "6\nX\nY\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid return should not crash.");
    }

    @Test
    public void testViewBooks() {
        // Add a book and then view the list of books
        String input = "1\nBook\nAuth\n2001\nisbn\nbk1\nMystery\n7\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing books should not crash.");
    }

    @Test
    public void testViewMembers() {
        // Add a member and then view the list of members
        String input = "3\nJane\njane@ex.com\nMID1\n8\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing members should not crash.");
    }

    @Test
    public void testInvalidMenuChoice() {
        // Input an unsupported menu number followed by a valid exit
        String input = "42\n9\n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid menu input should print a warning and continue.");
    }

    @Test
    public void testReturnWithValidMemberOnly() {
        // Valid member, invalid book, triggers null check in returnBook
        String input =
                "3\nJohn\njohn@example.com\nMEM1\n" +
                        "6\nMEM1\nINVALIDBOOK\n" +
                        "9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Return with valid member and invalid book should not crash.");
    }

    @Test
    public void testReturnWithValidBookOnly() {
        // Valid book, invalid member, triggers null check in returnBook
        String input =
                "1\nSomeBook\nAuthor\n2020\nISBN001\nBOOK1\nFiction\n" +
                        "6\nINVALIDMEMBER\nBOOK1\n" +
                        "9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Return with valid book and invalid member should not crash.");
    }

    // PROPERTY-BASED TESTS

    @Property
    public void propertyBasedBookAdd(
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String title,
            @ForAll @AlphaChars @StringLength(min = 3, max = 20) String author,
            @ForAll @AlphaChars @StringLength(min = 10, max = 17) String isbn,
            @ForAll @AlphaChars @StringLength(min = 3, max = 10) String bookId,
            @ForAll @AlphaChars @StringLength(min = 3, max = 15) String genre
    ) {
        // Property-based test: Add book with random valid fields
        String input = String.format("1\n%s\n%s\n2005\n%s\n%s\n%s\n9\n",
                title, author, isbn, bookId, genre);
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start(), "Adding book via property-based input should not throw.");
    }
}

