import java.util.Random;

/**
 * Handles book purchase requests by generating a random cost between $10 and $100.
 */
public class Purchasing {
    private static final int MIN_COST = 10;
    private static final int MAX_COST = 100;
    private Random random;

    // Initialize a new Random instance for cost generation
    public Purchasing() {
        this.random = new Random();
    }

    // Generate and return a random cost between MIN_COST and MAX_COST (inclusive)
    public double generateBookCost() {
        int cost = MIN_COST + random.nextInt(MAX_COST - MIN_COST + 1);
        return (double) cost;
    }
}

