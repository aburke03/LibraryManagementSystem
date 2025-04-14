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
 * Covers full CLI interaction flow using specification-based,
 * structural, and property-based tests.
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
        String input = "1\nBookTitle\nAuthorName\n2000\nISBN123456\nBOOK123\nFiction\n2\nBOOK123\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Add and remove book should not throw.");
    }

    @Test
    public void testAddRemoveAndAttemptCheckoutWithMissingBook() {
        String input =
                "3\nBob\nbob@example.com\nMEMBER1\n" +  // Add member
                        "5\nMEMBER1\nBOOKDOESNOTEXIST\n" +      // Try checkout (book missing)
                        "4\nMEMBER1\n" +                        // Remove member
                        "9\n";                                  // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Member ops and invalid checkout should not throw.");
    }


    @Test
    public void testCheckoutAndReturnValidBook() {
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
        String input = "5\nBADMEMBER\nBADBOOK\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid checkout should not crash.");
    }

    @Test
    public void testReturnInvalidIds() {
        String input = "6\nX\nY\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid return should not crash.");
    }

    @Test
    public void testViewBooks() {
        String input = "1\nBook\nAuth\n2001\nisbn\nbk1\nMystery\n7\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing books should not crash.");
    }

    @Test
    public void testViewMembers() {
        String input = "3\nJane\njane@ex.com\nMID1\n8\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing members should not crash.");
    }

    @Test
    public void testInvalidMenuChoice() {
        String input = "42\n9\n\n";  // Extra newline to prevent Scanner crash
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid option should print warning and continue.");
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
        String input = String.format("1\n%s\n%s\n2005\n%s\n%s\n%s\n9\n",
                title, author, isbn, bookId, genre);
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start());
    }
}
