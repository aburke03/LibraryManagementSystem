import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.Property;
import net.jqwik.api.ForAll;
import net.jqwik.api.constraints.DoubleRange;

import java.lang.reflect.Field;


/**
 * Test suite for the LibraryAccounts class in the Library Management System.
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 *
 * i get 100% method and line coverage for the actual file, but for whatever reason my test file gives me issues.
 * i have tried multiple things like try catch statments which gave me 100% method and good lines, but
 * it didn't make much since to do that
 */
public class LibraryAccountsTest {

    private LibraryAccounts accounts;
    private final String validAuthCode = "123456";
    private final String invalidAuthCode = "111111";

    @BeforeEach
    public void setUp() {
        // Create fresh accounts object before each test to avoid state bleeding between tests
        accounts = new LibraryAccounts();
    }

    // Specification tests 

    @Test
    public void testInitialBalance() {
        // Make sure a new account starts with the expected $39K balance
        assertEquals(39000.00, accounts.getOperatingCashBalance());
    }

    @Test
    public void testGetLibrarians() {
        // Check that we can access the librarians object. Should not be null
        assertNotNull(accounts.getLibrarians());
    }

    @Test
    public void testAddValidDonation() {
        // Adding money should increase the balance by exactly that amount
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.addDonation(500.00);
        assertEquals(initialBalance + 500.00, accounts.getOperatingCashBalance());
    }

    @Test
    public void testAddZeroDonation() {
        // Adding zero dollars shouldn't change the balance
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.addDonation(0.0);
        assertEquals(initialBalance, accounts.getOperatingCashBalance());
    }

    @Test
    public void testAddNegativeDonation() {
        // Can't donate negative money, so it will throw a exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accounts.addDonation(-100.00));
        assertEquals("Donation amount must be non-negative", exception.getMessage());
    }

    @Test
    public void testWithdrawValidSalary() {
        // Taking out salary should reduce the balance by that amount
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.withdrawSalary(1000.00);
        assertEquals(initialBalance - 1000.00, accounts.getOperatingCashBalance());
    }

    @Test
    public void testWithdrawZeroSalary() {
        // Withdrawing zero dollars shouldn't change anything
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.withdrawSalary(0.0);
        assertEquals(initialBalance, accounts.getOperatingCashBalance());
    }

    @Test
    public void testWithdrawNegativeSalary() {
        // Can't take out negative salary, which it should complain about
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accounts.withdrawSalary(-100.00));
        assertEquals("Salary withdrawal amount must be non-negative", exception.getMessage());
    }

    @Test
    public void testWithdrawExcessiveSalary() {
        // Can't withdraw more money than we have. Should get a insufficient funds error
        double excessiveAmount = accounts.getOperatingCashBalance() + 1000.00;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accounts.withdrawSalary(excessiveAmount));
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    public void testWithdrawSalaryWithAuthCode() {
        // When a librarian withdraws salary, their record should be updated
        double initialBalance = accounts.getOperatingCashBalance();
        double salaryAmount = 1000.00;

        accounts.withdrawSalary(validAuthCode, salaryAmount);

        // Check both overall balance and librarian's record
        assertEquals(initialBalance - salaryAmount, accounts.getOperatingCashBalance());
        assertEquals(salaryAmount, accounts.getLibrarians().getTotalSalaryWithdrawn(validAuthCode));
    }

    @Test
    public void testWithdrawSalaryWithInvalidAuthCode() {
        // Can't withdraw salary with a bogus librarian code
        assertThrows(IllegalArgumentException.class, () -> accounts.withdrawSalary(invalidAuthCode, 1000.00));
    }

    @Test
    public void testOrderNewBook() {
        // Ordering a new book should generate a random cost and update the balance
        double initialBalance = accounts.getOperatingCashBalance();
        double cost = accounts.orderNewBook();

        // Book costs should be between $10-$100
        assertTrue(cost >= 10.0);
        assertTrue(cost <= 100.0);

        // Balance should be reduced by the cost
        assertEquals(initialBalance - cost, accounts.getOperatingCashBalance());
    }

    @Test
    public void testOrderBookSpecificCost() {
        // When ordering a book with known cost, balance should decrease by that amount
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.orderBook(50.00);
        assertEquals(initialBalance - 50.00, accounts.getOperatingCashBalance());
    }

    @Test
    public void testOrderBookZeroCost() {
        // Free books shouldn't change the balance
        double initialBalance = accounts.getOperatingCashBalance();
        accounts.orderBook(0.0);
        assertEquals(initialBalance, accounts.getOperatingCashBalance());
    }

    @Test
    public void testOrderBookNegativeCost() {
        // Can't have books with negative costs
        assertThrows(IllegalArgumentException.class, () -> accounts.orderBook(-50.00));
    }

    @Test
    public void testOrderBookExcessiveCost() {
        // Can't order books that cost more than our available funds
        double excessiveCost = accounts.getOperatingCashBalance() + 1000.00;
        assertThrows(IllegalArgumentException.class, () -> accounts.orderBook(excessiveCost));
    }

    @Test
    public void testMockedPurchasing() throws Exception {
        // Let's hack in a fixed-cost purchasing system to test predictably
        Purchasing mockPurchasing = new CustomPurchasing(10.0);
        setPrivateField("purchasing", mockPurchasing);

        double initialBalance = accounts.getOperatingCashBalance();
        double cost = accounts.orderNewBook();

        // With our rigged purchasing, we know exactly what the cost should be
        assertEquals(10.0, cost);
        assertEquals(initialBalance - 10.0, accounts.getOperatingCashBalance());
    }

    @Test
    public void testInsufficientFundsForOrderNewBook() throws Exception {
        // Set up so we only have $5 but books cost $50
        setPrivateField("operatingCashBalance", 5.0);
        setPrivateField("purchasing", new CustomPurchasing(50.0));

        // Should fail due to insufficient funds
        assertThrows(IllegalArgumentException.class, () -> accounts.orderNewBook());

        // Balance shouldn't change since purchase failed
        assertEquals(5.0, accounts.getOperatingCashBalance());
    }

    @Test
    public void testEdgeCaseExactBalance() throws Exception {
        // Edge case: what if we spend our very last dollar?
        setPrivateField("operatingCashBalance", 100.0);
        accounts.orderBook(100.0);
        assertEquals(0.0, accounts.getOperatingCashBalance());
    }

    // Custom purchasing class for testing with fixed costs
    private static class CustomPurchasing extends Purchasing {
        private final double fixedCost;

        public CustomPurchasing(double fixedCost) {
            this.fixedCost = fixedCost;
        }

        @Override
        public double generateBookCost() {
            return fixedCost;
        }
    }

    // Helper to hack private fields for testing
    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = LibraryAccounts.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(accounts, value);
    }

    // PROPERTY-BASED TESTS - These run with many random inputs to find edge cases

    @Property
    public void propertyDonationIncreasesBalance(
            @ForAll @DoubleRange(min = 0.0, max = 10000.0) double donationAmount) {
        // For any donation amount (0-10K), balance should increase by exactly that amount
        LibraryAccounts testAccounts = new LibraryAccounts();
        double initialBalance = testAccounts.getOperatingCashBalance();

        testAccounts.addDonation(donationAmount);

        assertEquals(initialBalance + donationAmount, testAccounts.getOperatingCashBalance());
    }

    @Property
    public void propertySalaryDecreasesBalance(
            @ForAll @DoubleRange(min = 0.0, max = 500.0) double salaryAmount) {
        // For any reasonable salary amount, balance should decrease accordingly
        LibraryAccounts testAccounts = new LibraryAccounts();
        double initialBalance = testAccounts.getOperatingCashBalance();

        testAccounts.withdrawSalary(salaryAmount);

        assertEquals(initialBalance - salaryAmount, testAccounts.getOperatingCashBalance());
    }

    @Property
    public void propertyOrderBookDecreasesBalance(
            @ForAll @DoubleRange(min = 10.0, max = 100.0) double bookCost) {
        // For any book cost in the valid range, balance should decrease properly
        LibraryAccounts testAccounts = new LibraryAccounts();
        double initialBalance = testAccounts.getOperatingCashBalance();

        testAccounts.orderBook(bookCost);

        assertEquals(initialBalance - bookCost, testAccounts.getOperatingCashBalance());
    }

    @Property
    public void propertyAuthCodeWithSalary(
            @ForAll @DoubleRange(min = 10.0, max = 500.0) double salaryAmount) {
        // When librarians withdraw salary, it should be tracked in their records
        LibraryAccounts testAccounts = new LibraryAccounts();

        testAccounts.withdrawSalary(validAuthCode, salaryAmount);

        double totalSalary = testAccounts.getLibrarians().getTotalSalaryWithdrawn(validAuthCode);
        assertTrue(totalSalary >= salaryAmount);
    }

    @Property
    public void propertyOperationsAreCumulative(
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double donation1,
            @ForAll @DoubleRange(min = 0.0, max = 200.0) double salary1,
            @ForAll @DoubleRange(min = 0.0, max = 1000.0) double donation2,
            @ForAll @DoubleRange(min = 0.0, max = 200.0) double salary2) {
        // Multiple operations should all affect the balance correctly
        LibraryAccounts testAccounts = new LibraryAccounts();
        double initialBalance = testAccounts.getOperatingCashBalance();

        // Do a series of operations
        testAccounts.addDonation(donation1);
        testAccounts.withdrawSalary(salary1);
        testAccounts.addDonation(donation2);
        testAccounts.withdrawSalary(salary2);

        // Final balance should be precisely calculable
        double expectedBalance = initialBalance + donation1 - salary1 + donation2 - salary2;
        assertEquals(expectedBalance, testAccounts.getOperatingCashBalance());
    }
}