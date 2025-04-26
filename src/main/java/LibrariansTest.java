import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class LibrariansTest {
    private Librarians libs;
    private final String valid   = "123456";
    private final String invalid = "999999";

    @BeforeEach
        // make a fresh Librarians instance before each test
    void setup() {
        libs = new Librarians();
    }

    @Test
        // should return true when we authenticate with a known valid code
    void authenticate_valid_returnsTrue() {
        assertTrue(libs.authenticate(valid));
    }

    @Test
        // should return false for a code that isn't in the system
    void authenticate_invalid_returnsFalse() {
        assertFalse(libs.authenticate(invalid));
    }

    @Test
        // asking for the name of a valid code should give us "Mike"
    void getName_valid_returnsMike() {
        assertEquals("Mike", libs.getName(valid));
    }

    @Test
        // getName with a bad code ought to throw an IllegalArgumentException
    void getName_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> libs.getName(invalid));
    }

    @Test
        // there should be exactly three auth codes available
    void getAuthCodes_sizeIsThree() {
        assertEquals(3, libs.getAuthCodes().size());
    }

    @Test
        // check that the auth-code set contains all the expected strings
    void getAuthCodes_containsAllKeys() {
        Set<String> keys = libs.getAuthCodes();
        assertTrue(keys.containsAll(Set.of("123456", "654321", "000000")));
    }

    @Test
        // the returned set should be unmodifiable—adding to it throws UnsupportedOperationException
    void getAuthCodes_unmodifiable_throws() {
        assertThrows(UnsupportedOperationException.class,
                () -> libs.getAuthCodes().add("111111"));
    }

    @Test
        // before any withdrawals, total salary withdrawn should start at zero
    void initialTotalSalary_isZero() {
        assertEquals(0.0, libs.getTotalSalaryWithdrawn(valid));
    }

    @Test
        // after recording a withdrawal, the total salary should update accordingly
    void recordSalary_valid_updatesTotal() {
        libs.recordSalaryWithdrawal(valid, 100.0);
        assertEquals(100.0, libs.getTotalSalaryWithdrawn(valid));
    }

    @Test
        // recording a negative salary amount is invalid and should throw
    void recordSalary_negative_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.recordSalaryWithdrawal(valid, -1.0));
    }

    @Test
        // using an invalid auth code when recording salary should throw
    void recordSalary_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.recordSalaryWithdrawal(invalid, 10.0));
    }

    @Test
        // after buying a book, that purchase should appear in the list
    void recordBookPurchase_valid_increasesList() {
        libs.recordBookPurchase(valid, 20.0);
        assertEquals(1, libs.getPurchasedBooks(valid).size());
    }

    @Test
        // purchasing a book with a negative price is invalid
    void recordBookPurchase_negativeCost_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.recordBookPurchase(valid, -5.0));
    }

    @Test
        // invalid auth code during book purchase should throw
    void recordBookPurchase_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.recordBookPurchase(invalid, 10.0));
    }

    @Test
        // requesting purchase history with a bad code should throw
    void getPurchasedBooks_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.getPurchasedBooks(invalid));
    }

    @Test
        // the purchase list is unmodifiable—trying to add to it throws
    void getPurchasedBooks_unmodifiable_throws() {
        libs.recordBookPurchase(valid, 30.0);
        assertThrows(UnsupportedOperationException.class,
                () -> libs.getPurchasedBooks(valid).add(5.0));
    }

    // covers the private-constructor authCode format check
    @Test
    // instantiating the private Librarian with a bad code should wrap IllegalArgumentException
    void privateConstructor_badAuthCode_throws() throws Exception {
        Constructor<?> ctor = Class
                .forName("Librarians$Librarian")
                .getDeclaredConstructor(String.class, String.class);
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(
                InvocationTargetException.class,
                () -> ctor.newInstance("Name", "abc123")
        );
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
        // checking total salary for an invalid code should throw IllegalArgumentException
    void getTotalSalaryWithdrawn_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> libs.getTotalSalaryWithdrawn(invalid));
    }
}

