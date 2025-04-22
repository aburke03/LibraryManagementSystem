import java.util.Objects;

/**
 * Manages the library's operating cash balance, including donations, salary withdrawals,
 * and ordering new books through Purchasing and provides access to the Librarians' list.
 */
public class LibraryAccounts {
    private double operatingCashBalance;
    private Purchasing purchasing;
    private Librarians librarians;

    // Initialize balance to $39000 and set up Purchasing and Librarians
    public LibraryAccounts() {
        this.operatingCashBalance = 39_000.00;
        this.purchasing = new Purchasing();
        this.librarians = new Librarians();
    }

    // Return the current operating cash balance
    public double getOperatingCashBalance() {
        return operatingCashBalance;
    }

    // Provide access to the librarian records
    public Librarians getLibrarians() {
        return librarians;
    }

    // Add a non-negative donation to the cash balance
    public void addDonation(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Donation amount must be non-negative");
        }
        operatingCashBalance += amount;
    }

    // Withdraw a salary amount from balance (non-negative, must have enough funds)
    public void withdrawSalary(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Salary withdrawal amount must be non-negative");
        }
        if (amount > operatingCashBalance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        operatingCashBalance -= amount;
    }

    // Withdraw salary for a specific librarian and record it
    public void withdrawSalary(String authCode, double amount) {
        withdrawSalary(amount);
        librarians.recordSalaryWithdrawal(authCode, amount);
    }

    // Order a new book by generating a cost, deducting from balance, and returning cost
    public double orderNewBook() {
        double cost = purchasing.generateBookCost();
        if (cost > operatingCashBalance) {
            throw new IllegalArgumentException("Insufficient funds to order book");
        }
        operatingCashBalance -= cost;
        return cost;
    }

    // Directly order a book at a given cost, deducting it from balance
    public void orderBook(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Book cost must be non-negative");
        }
        if (cost > operatingCashBalance) {
            throw new IllegalArgumentException("Insufficient funds to order book");
        }
        operatingCashBalance -= cost;
    }
}

