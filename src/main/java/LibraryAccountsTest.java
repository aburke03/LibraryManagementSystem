import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.Assume;
import net.jqwik.api.constraints.DoubleRange;

import java.lang.reflect.Field;

/**
 * Comprehensive test suite for the LibraryAccounts class in the Library Management System.
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 */
public class LibraryAccountsTest {

    private LibraryAccounts accounts;
    private final String validAuthCode = "123456"; // One of the predefined codes
    private final String invalidAuthCode = "111111"; // Not a predefined code

    @BeforeEach
    public void setUp() {
        accounts = new LibraryAccounts();
    }

    // ========== CONSTRUCTOR AND INITIALIZATION TESTS ==========

    @Test
    public void testConstructorInitialization() {
        // Test that the constructor initializes all fields properly
        LibraryAccounts freshAccounts = new LibraryAccounts();
        assertEquals(39000.00, freshAccounts.getOperatingCashBalance(), 0.001,
                "Initial balance should be 39,000");
        assertNotNull(freshAccounts.getLibrarians(),
                "Librarians object should be initialized");
    }

    // ========== BALANCE AND DONATION TESTS ==========

    @Test
    public void testInitialBalance() {
        // Verify the initial balance is set to $39,000
        assertEquals(39000.00, accounts.getOperatingCashBalance(),
                "Initial operating cash balance should be $39,000");
    }

    @Test
    public void testAddValidDonation() {
        // Test adding a valid donation
        double initialBalance = accounts.getOperatingCashBalance();
        double donationAmount = 500.00;

        accounts.addDonation(donationAmount);

        assertEquals(initialBalance + donationAmount, accounts.getOperatingCashBalance(),
                "Balance should increase by the donated amount");
    }

    @Test
    public void testAddZeroDonation() {
        // Test adding a zero donation
        double initialBalance = accounts.getOperatingCashBalance();

        accounts.addDonation(0.0);

        assertEquals(initialBalance, accounts.getOperatingCashBalance(),
                "Zero donation should not change the balance");
    }

    @Test
    public void testAddNegativeDonation() {
        // Test adding a negative donation (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> accounts.addDonation(-100.00),
                "Negative donation should throw IllegalArgumentException");
    }

    // ========== SALARY WITHDRAWAL TESTS ==========

    @Test
    public void testWithdrawValidSalaryAmount() {
        // Test withdrawing a valid salary amount
        double initialBalance = accounts.getOperatingCashBalance();
        double salaryAmount = 1000.00;

        accounts.withdrawSalary(salaryAmount);

        assertEquals(initialBalance - salaryAmount, accounts.getOperatingCashBalance(),
                "Balance should decrease by the withdrawn salary amount");
    }

    @Test
    public void testWithdrawZeroSalary() {
        // Test withdrawing a zero salary amount
        double initialBalance = accounts.getOperatingCashBalance();

        accounts.withdrawSalary(0.0);

        assertEquals(initialBalance, accounts.getOperatingCashBalance(),
                "Zero salary withdrawal should not change the balance");
    }

    @Test
    public void testWithdrawNegativeSalary() {
        // Test withdrawing a negative salary (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> accounts.withdrawSalary(-100.00),
                "Negative salary withdrawal should throw IllegalArgumentException");
    }

    @Test
    public void testWithdrawExcessiveSalary() {
        // Test withdrawing more than the available balance
        double excessiveAmount = accounts.getOperatingCashBalance() + 1000.00;

        assertThrows(IllegalArgumentException.class, () -> accounts.withdrawSalary(excessiveAmount),
                "Withdrawing more than the balance should throw IllegalArgumentException");
    }

    @Test
    public void testWithdrawSalaryWithAuthCode() {
        // Test withdrawing salary with valid auth code
        double initialBalance = accounts.getOperatingCashBalance();
        double salaryAmount = 1000.00;

        accounts.withdrawSalary(validAuthCode, salaryAmount);

        assertEquals(initialBalance - salaryAmount, accounts.getOperatingCashBalance(),
                "Balance should decrease by the withdrawn salary amount");

        // Verify the withdrawal was recorded for the librarian
        assertEquals(salaryAmount, accounts.getLibrarians().getTotalSalaryWithdrawn(validAuthCode),
                "Salary withdrawal should be recorded for the librarian");
    }

    @Test
    public void testWithdrawZeroSalaryWithAuthCode() {
        // Test withdrawing zero salary with valid auth code
        double initialBalance = accounts.getOperatingCashBalance();

        accounts.withdrawSalary(validAuthCode, 0.0);

        assertEquals(initialBalance, accounts.getOperatingCashBalance(),
                "Zero salary withdrawal should not change the balance");
        assertEquals(0.0, accounts.getLibrarians().getTotalSalaryWithdrawn(validAuthCode),
                "Zero salary withdrawal should be recorded for the librarian");
    }

    @Test
    public void testWithdrawSalaryWithInvalidAuthCode() {
        // Test withdrawing salary with invalid auth code
        assertThrows(IllegalArgumentException.class,
                () -> accounts.withdrawSalary(invalidAuthCode, 1000.00),
                "Withdrawing salary with invalid auth code should throw IllegalArgumentException");
    }

    @Test
    public void testAllWithdrawSalaryOverloads() {
        // Test all overloaded versions of withdrawSalary
        double initialBalance = accounts.getOperatingCashBalance();
        double amount = 100.00;

        // Test single-parameter version
        accounts.withdrawSalary(amount);
        assertEquals(initialBalance - amount, accounts.getOperatingCashBalance(),
                "Balance should decrease by the amount after using single-parameter version");

        // Test two-parameter version with valid auth code
        initialBalance = accounts.getOperatingCashBalance();
        accounts.withdrawSalary(validAuthCode, amount);
        assertEquals(initialBalance - amount, accounts.getOperatingCashBalance(),
                "Balance should decrease by the amount after using two-parameter version");
    }

    // ========== BOOK ORDERING TESTS ==========

    @Test
    public void testOrderNewBook() {
        // Test ordering a new book (cost is random but should be between $10-$100)
        double initialBalance = accounts.getOperatingCashBalance();
        double cost = accounts.orderNewBook();

        // Cost should be between $10 and $100
        assertTrue(cost >= 10.0 && cost <= 100.0,
                "Book cost should be between $10 and $100");

        // Balance should decrease by the cost
        assertEquals(initialBalance - cost, accounts.getOperatingCashBalance(),
                "Balance should decrease by the book cost");
    }

    @Test
    public void testOrderNewBookMultipleTimes() {
        // Test ordering multiple books in succession
        double initialBalance = accounts.getOperatingCashBalance();

        // First order
        double cost1 = accounts.orderNewBook();
        double balanceAfterFirst = accounts.getOperatingCashBalance();

        // Second order
        double cost2 = accounts.orderNewBook();
        double finalBalance = accounts.getOperatingCashBalance();

        // Verify each step
        assertEquals(initialBalance - cost1, balanceAfterFirst,
                "Balance should decrease by the first book cost");
        assertEquals(balanceAfterFirst - cost2, finalBalance,
                "Balance should decrease by the second book cost");
        assertEquals(initialBalance - cost1 - cost2, finalBalance,
                "Final balance should reflect both purchases");
    }

    @Test
    public void testOrderNewBookWithMockedLowCost() {
        // Test orderNewBook with a mocked Purchasing object that always returns a low cost
        try {
            // Create a mock Purchasing that always returns 10.0
            Purchasing mockPurchasing = new Purchasing() {
                @Override
                public double generateBookCost() {
                    return 10.0;
                }
            };

            // Use reflection to replace the purchasing field
            Field purchasingField = LibraryAccounts.class.getDeclaredField("purchasing");
            purchasingField.setAccessible(true);
            purchasingField.set(accounts, mockPurchasing);

            // Now test orderNewBook with our mock
            double initialBalance = accounts.getOperatingCashBalance();
            double cost = accounts.orderNewBook();

            assertEquals(10.0, cost, "Cost should be our mocked value of 10.0");
            assertEquals(initialBalance - 10.0, accounts.getOperatingCashBalance(),
                    "Balance should decrease by exactly 10.0");
        } catch (Exception e) {
            fail("Test failed due to reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testOrderNewBookWithMockedHighCost() {
        // Test orderNewBook with a mocked Purchasing object that returns a cost higher than balance
        try {
            // Set a low initial balance
            Field balanceField = LibraryAccounts.class.getDeclaredField("operatingCashBalance");
            balanceField.setAccessible(true);
            balanceField.set(accounts, 50.0);

            // Create a mock Purchasing that returns a cost higher than the balance
            Purchasing mockPurchasing = new Purchasing() {
                @Override
                public double generateBookCost() {
                    return 100.0;
                }
            };

            // Use reflection to replace the purchasing field
            Field purchasingField = LibraryAccounts.class.getDeclaredField("purchasing");
            purchasingField.setAccessible(true);
            purchasingField.set(accounts, mockPurchasing);

            // Now test orderNewBook with our mock - should throw exception
            assertThrows(IllegalArgumentException.class,
                    () -> accounts.orderNewBook(),
                    "Should throw exception when cost exceeds balance");

            // Verify balance hasn't changed
            assertEquals(50.0, accounts.getOperatingCashBalance(),
                    "Balance should remain unchanged after failed purchase");
        } catch (Exception e) {
            fail("Test failed due to reflection error: " + e.getMessage());
        }
    }

    @Test
    public void testOrderBookSpecificCost() {
        // Test ordering a book with a specific cost
        double initialBalance = accounts.getOperatingCashBalance();
        double cost = 50.00;

        accounts.orderBook(cost);

        assertEquals(initialBalance - cost, accounts.getOperatingCashBalance(),
                "Balance should decrease by the specified book cost");
    }

    @Test
    public void testOrderBookZeroCost() {
        // Test ordering a book with zero cost
        double initialBalance = accounts.getOperatingCashBalance();

        accounts.orderBook(0.0);

        assertEquals(initialBalance, accounts.getOperatingCashBalance(),
                "Zero cost book should not change the balance");
    }

    @Test
    public void testOrderBookNegativeCost() {
        // Test ordering a book with negative cost (should throw exception)
        assertThrows(IllegalArgumentException.class, () -> accounts.orderBook(-50.00),
                "Negative book cost should throw IllegalArgumentException");
    }

    @Test
    public void testOrderBookExcessiveCost() {
        // Test ordering a book that costs more than the available balance
        double excessiveCost = accounts.getOperatingCashBalance() + 1000.00;

        assertThrows(IllegalArgumentException.class, () -> accounts.orderBook(excessiveCost),
                "Ordering a book that costs more than the balance should throw IllegalArgumentException");
    }

    @Test
    public void testEdgeCaseBalanceExactlyCost() {
        // Test the edge case where the cost is exactly equal to the balance
        try {
            // Set the balance to a known value
            Field balanceField = LibraryAccounts.class.getDeclaredField("operatingCashBalance");
            balanceField.setAccessible(true);
            balanceField.set(accounts, 100.0);

            // Order a book that costs exactly that amount
            accounts.orderBook(100.0);

            // Verify the balance is now zero
            assertEquals(0.0, accounts.getOperatingCashBalance(),
                    "Balance should be zero after ordering a book that costs exactly the balance amount");
        } catch (Exception e) {
            fail("Test failed due to reflection error: " + e.getMessage());
        }
    }

    // ========== MISC TESTS ==========

    @Test
    public void testGetLibrarians() {
        // Test that getLibrarians returns a valid Librarians object
        assertNotNull(accounts.getLibrarians(),
                "getLibrarians should return a non-null Librarians object");
    }

    // ========== PROPERTY-BASED TESTS ==========

    @Property
    public void propertyDonationIncreasesBalance(
            @ForAll @DoubleRange(min = 0.0, max = 10000.0) double donationAmount) {
        // Property-based test: Adding a non-negative donation increases balance by that amount
        // Initialize accounts for each property test since @BeforeEach doesn't work with jqwik
        LibraryAccounts accounts = new LibraryAccounts();
        double initialBalance = accounts.getOperatingCashBalance();

        accounts.addDonation(donationAmount);

        assertEquals(initialBalance + donationAmount, accounts.getOperatingCashBalance(),
                "Property: Balance should increase by the donated amount");
    }

    @Property
    public void propertySalaryDecreasesBalance(
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double salaryAmount) {
        // Property-based test: Withdrawing a valid salary decreases balance by that amount
        // Initialize accounts for each property test since @BeforeEach doesn't work with jqwik
        LibraryAccounts accounts = new LibraryAccounts();
        double initialBalance = accounts.getOperatingCashBalance();

        // Ensure salary amount is not more than the balance
        Assume.that(salaryAmount <= initialBalance);

        accounts.withdrawSalary(salaryAmount);

        assertEquals(initialBalance - salaryAmount, accounts.getOperatingCashBalance(),
                "Property: Balance should decrease by the withdrawn salary amount");
    }

    @Property
    public void propertyOrderBookDecreasesBalance(
            @ForAll @DoubleRange(min = 10.0, max = 100.0) double bookCost) {
        // Property-based test: Ordering a book decreases balance by the book's cost
        // Initialize accounts for each property test since @BeforeEach doesn't work with jqwik
        LibraryAccounts accounts = new LibraryAccounts();
        double initialBalance = accounts.getOperatingCashBalance();

        // Ensure book cost is not more than the balance
        Assume.that(bookCost <= initialBalance);

        accounts.orderBook(bookCost);

        assertEquals(initialBalance - bookCost, accounts.getOperatingCashBalance(),
                "Property: Balance should decrease by the book cost");
    }

    @Property
    public void propertyAuthCodeWithSalary(
            @ForAll @DoubleRange(min = 10.0, max = 1000.0) double salaryAmount) {
        // Property-based test: Withdrawing salary with valid auth code records it for the librarian
        // Initialize accounts for each property test since @BeforeEach doesn't work with jqwik
        LibraryAccounts accounts = new LibraryAccounts();

        // Ensure salary amount is not more than the balance
        Assume.that(salaryAmount <= accounts.getOperatingCashBalance());

        accounts.withdrawSalary(validAuthCode, salaryAmount);

        // Get the total salary withdrawn for this librarian
        double totalSalary = accounts.getLibrarians().getTotalSalaryWithdrawn(validAuthCode);

        // The total should be at least the amount we just withdrew
        assertTrue(totalSalary >= salaryAmount,
                "Property: Total salary withdrawn should include the current withdrawal");
    }

    @Property
    public void propertyOperationsAreCumulative(
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double donation1,
            @ForAll @DoubleRange(min = 0.0, max = 500.0) double salary1,
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double donation2,
            @ForAll @DoubleRange(min = 0.0, max = 500.0) double salary2) {
        // Property-based test: Multiple operations should affect the balance cumulatively
        // Initialize accounts for each property test since @BeforeEach doesn't work with jqwik
        LibraryAccounts accounts = new LibraryAccounts();
        double initialBalance = accounts.getOperatingCashBalance();

        // First round of operations
        accounts.addDonation(donation1);

        // Ensure salary1 is not more than the current balance
        double currentBalance = accounts.getOperatingCashBalance();
        Assume.that(salary1 <= currentBalance);

        accounts.withdrawSalary(salary1);

        // Second round of operations
        accounts.addDonation(donation2);

        // Ensure salary2 is not more than the current balance
        currentBalance = accounts.getOperatingCashBalance();
        Assume.that(salary2 <= currentBalance);

        accounts.withdrawSalary(salary2);

        // Final balance should be: initial + donation1 - salary1 + donation2 - salary2
        double expectedBalance = initialBalance + donation1 - salary1 + donation2 - salary2;

        assertEquals(expectedBalance, accounts.getOperatingCashBalance(),
                "Property: Multiple operations should affect the balance cumulatively");
    }
}