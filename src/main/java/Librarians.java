import java.util.*;

/**
 * Manages library staff information, tracking authentication codes, salary withdrawals,
 * and book purchases per librarian.
 */
public class Librarians {
    private Map<String, Librarian> librarians;

    // Constructor to set up three predefined librarians with 6digit codes
    public Librarians() {
        librarians = new HashMap<>();
        librarians.put("123456", new Librarian("Mike", "123456"));
        librarians.put("654321", new Librarian("Ekim", "654321"));
        librarians.put("000000", new Librarian("Ghost", "000000"));
    }

    // Check if given authCode belongs to a fulltime librarian
    public boolean authenticate(String authCode) {
        return librarians.containsKey(authCode);
    }

    // Get the name associated with an authCode or throw if invalid
    public String getName(String authCode) {
        Librarian lib = librarians.get(authCode);
        if (lib == null) {
            throw new IllegalArgumentException("Invalid librarian code");
        }
        return lib.name;
    }

    // Return unmodifiable set of all librarian auth codes
    public Set<String> getAuthCodes() {
        return Collections.unmodifiableSet(librarians.keySet());
    }

    // Log a salary withdrawal for the given librarian code
    public void recordSalaryWithdrawal(String authCode, double amount) {
        Librarian lib = librarians.get(authCode);
        if (lib == null) {
            throw new IllegalArgumentException("Invalid librarian code");
        }
        lib.addSalary(amount);
    }

    // Log a book purchase cost for the given librarian code
    public void recordBookPurchase(String authCode, double cost) {
        Librarian lib = librarians.get(authCode);
        if (lib == null) {
            throw new IllegalArgumentException("Invalid librarian code");
        }
        lib.addPurchasedBook(cost);
    }

    // Get total salary withdrawn by a specific librarian
    public double getTotalSalaryWithdrawn(String authCode) {
        Librarian lib = librarians.get(authCode);
        if (lib == null) {
            throw new IllegalArgumentException("Invalid librarian code");
        }
        return lib.getTotalSalary();
    }

    // Get list of book purchase costs for a specific librarian
    public List<Double> getPurchasedBooks(String authCode) {
        Librarian lib = librarians.get(authCode);
        if (lib == null) {
            throw new IllegalArgumentException("Invalid librarian code");
        }
        return lib.getPurchasedBooks();
    }

    // Internal class representing an individual librarian record
    private static class Librarian {
        private final String name;
        private final String authCode;
        private double totalSalary;
        private List<Double> purchasedBooks;

        // Constructor to validate authCode format and initialize fields
        private Librarian(String name, String authCode) {
            this.name = Objects.requireNonNull(name);
            if (authCode == null || !authCode.matches("\\d{6}")) {
                throw new IllegalArgumentException("Auth code must be exactly 6 digits");
            }
            this.authCode = authCode;
            this.totalSalary = 0.0;
            this.purchasedBooks = new ArrayList<>();
        }

        // Add non-negative salary amount to this librarian's total
        private void addSalary(double amount) {
            if (amount < 0) {
                throw new IllegalArgumentException("Salary amount must be non-negative");
            }
            totalSalary += amount;
        }

        // Add non-negative purchase cost to this librarian's record
        private void addPurchasedBook(double cost) {
            if (cost < 0) {
                throw new IllegalArgumentException("Book cost must be non-negative");
            }
            purchasedBooks.add(cost);
        }

        // Return the total salary withdrawn
        private double getTotalSalary() {
            return totalSalary;
        }

        // Return unmodifiable list of purchase costs
        private List<Double> getPurchasedBooks() {
            return Collections.unmodifiableList(purchasedBooks);
        }
    }
}

