import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

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
    public void testStartupBannerAndExit() {
        String input = "\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Startup banner and exit should not throw.");
    }

    @Test
    public void testAddAndRemoveBook() {
        String input =
                "\n" + // volunteer
                        "1\nBookTitle\nAuthorName\n2000\nISBN123456\nBOOK123\nFiction\n" +
                        "2\nBOOK123\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Add and remove book should not throw.");
    }

    @Test
    public void testAddRemoveAndAttemptCheckoutWithMissingBook() {
        String input =
                "\n" +
                        "3\nBob\nbob@example.com\nMEMBER1\n" +
                        "5\nMEMBER1\nBOOKDOESNOTEXIST\n" +
                        "4\nMEMBER1\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Member ops and invalid checkout should not throw.");
    }

    @Test
    public void testNonIntegerMenuChoice() {
        String input = "\nabc\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Non-integer input should be handled as invalid.");
    }

    @Test
    public void testCheckoutAndReturnValidBook() {
        String input =
                "\n" +
                        "1\nTitle\nAuthor\n2020\nISBN9\nB1\nSci-Fi\n" +
                        "3\nAlice\nalice@example.com\nM1\n" +
                        "5\nM1\nB1\n" +
                        "6\nM1\nB1\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Checkout and return should succeed.");
    }

    @Test
    public void testCheckoutInvalidIds() {
        String input = "\n5\nBADMEMBER\nBADBOOK\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid checkout should not crash.");
    }

    @Test
    public void testReturnInvalidIds() {
        String input = "\n6\nX\nY\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid return should not crash.");
    }

    @Test
    public void testViewBooks() {
        String input = "\n1\nBook\nAuth\n2001\nisbn\nbk1\nMystery\n7\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing books should not crash.");
    }

    @Test
    public void testViewMembers() {
        String input = "\n3\nJane\njane@ex.com\nMID1\n8\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Viewing members should not crash.");
    }

    @Test
    public void testInvalidMenuChoice() {
        String input = "\n42\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid menu input should print a warning and continue.");
    }

    @Test
    public void testReturnWithValidMemberOnly() {
        String input =
                "\n" +
                        "3\nJohn\njohn@example.com\nMEM1\n" +
                        "6\nMEM1\nINVALIDBOOK\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Return with valid member and invalid book should not crash.");
    }

    @Test
    public void testReturnWithValidBookOnly() {
        String input =
                "\n" +
                        "1\nSomeBook\nAuthor\n2020\nISBN001\nBOOK1\nFiction\n" +
                        "6\nINVALIDMEMBER\nBOOK1\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Return with valid book and invalid member should not crash.");
    }

    @Test
    public void testRemoveMemberAsVolunteer() {
        String input = "\n4\nMEM1\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Volunteer should not revoke membership and not crash.");
    }

    @Test
    public void testRemoveMemberAsFullTime() {
        String input = "123456\n4\nMID\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Full-time should revoke membership and not crash.");
    }

    @Test
    public void testAddDonationAsVolunteer() {
        String input = "\n9\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Volunteer should not add donation and not crash.");
    }

    @Test
    public void testAddDonationAsFullTimeValid() {
        String input = "123456\n9\n100\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Full-time valid donation should succeed.");
    }

    @Test
    public void testAddDonationInvalidAmountNonNumeric() {
        String input = "123456\n9\nabc\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Non-numeric donation amount should be handled.");
    }

    @Test
    public void testAddDonationNegativeAmount() {
        String input = "123456\n9\n-50\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Negative donation amount should be handled.");
    }

    @Test
    public void testWithdrawSalaryAsVolunteer() {
        String input = "\n10\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Volunteer should not withdraw salary and not crash.");
    }

    @Test
    public void testWithdrawSalaryAsFullTimeValid() {
        String input = "123456\n10\n200\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Full-time valid salary withdrawal should succeed.");
    }

    @Test
    public void testWithdrawSalaryInvalidNonNumeric() {
        String input = "123456\n10\nxyz\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Non-numeric salary withdrawal amount should be handled.");
    }

    @Test
    public void testWithdrawSalaryNegativeAmount() {
        String input = "123456\n10\n-100\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Negative salary withdrawal should be handled.");
    }

    @Test
    public void testCheckoutPurchaseCancelled() {
        String input =
                "123456\n" +
                        "3\nBob\nbob@example.com\nMEM1\n" +
                        "5\nMEM1\nBADBOOK\nn\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Purchase cancelled should not crash.");
    }

    @Test
    public void testCheckoutPurchaseSuccess() {
        String input =
                "123456\n" +
                        "3\nBob\nbob@example.com\nMEM1\n" +
                        "5\nMEM1\nBADBOOK\ny\n" +
                        "Title\nAuth\n2021\nISBNX\nB1\nGenre\n" +
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Purchase and checkout should not crash.");
    }

    @Test
    public void testWithdrawSalaryInsufficientFunds() {
        String input = "123456\n10\n50000\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start(),
                "Insufficient-funds withdrawal should be caught and not crash.");
    }

    @Test
    public void testCheckoutPurchaseFailsInsufficientFunds() {
        // First empty the library's funds with a massive salary withdrawal
        // Then try to purchase a book, which should fail with insufficient funds
        String input =
                "123456\n" +                          // authenticate as full-time
                        "10\n38999\n" +                       // withdraw almost all money (leave $1)
                        "3\nBob\nbob@x.com\nMEM99\n" +        // add member
                        "5\nMEM99\nNOBOOK\ny\n" +             // try to checkout non-existent book, agree to purchase
                        "11\n";                               // exit after error

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start(),
                "Purchase failure should be handled gracefully.");
    }

    @Test
    public void testRemoveMemberFullTimeInvalidId() {
        String input = "123456\n4\nNONEXISTENT\n11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start(),
                "Removing a non-existent member (full-time) should not crash.");
    }

    // Additional tests to increase line coverage

    @Test
    public void testPromptBookDetailsInvalidYear() {
        // Test the branch where year input is not a valid integer
        String input =
                "\n" +  // volunteer
                        "1\nBook Title\nAuthor\nabc\nISBN123\nB123\nFiction\n" +  // invalid year input
                        "11\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid year in book details should be handled.");
    }

    @Test
    public void testAuthenticateUserValidCode() {
        // Test successful authentication with valid code
        String input = "123456\n11\n";  // Valid auth code followed by exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Valid auth code authentication should succeed.");
    }

    @Test
    public void testAuthenticateUserInvalidCode() {
        // Test failed authentication with invalid code
        String input = "999999\n11\n";  // Invalid auth code followed by exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Invalid auth code should be handled gracefully.");
    }

    @Test
    public void testAddDonationWithIllegalArgumentException() {
        // Test the catch block for IllegalArgumentException in addDonation
        String input = "123456\n9\n-100\n11\n";  // Negative donation amount triggers exception
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Negative donation should be handled gracefully.");
    }

    @Test
    public void testWithdrawSalaryWithIllegalArgumentException() {
        // Test the catch block for IllegalArgumentException in withdrawSalary
        String input = "123456\n10\n-100\n11\n";  // Negative salary amount triggers exception
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> cli.start(), "Negative salary withdrawal should be handled gracefully.");
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
        String input = String.format(
                "\n1\n%s\n%s\n2005\n%s\n%s\n%s\n11\n",
                title, author, isbn, bookId, genre
        );
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        assertDoesNotThrow(() -> new Interface().start(), "Property-based book add should not throw.");
    }
}