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
    // structural: fresh instance per test
    void setup() {
        libs = new Librarians();
    }

    @Test
    // specification: valid code authenticates
    void authenticate_valid_returnsTrue() {
        assertTrue(libs.authenticate(valid));
    }

    @Test
    // specification: invalid code rejected
    void authenticate_invalid_returnsFalse() {
        assertFalse(libs.authenticate(invalid));
    }

    @Test
    // specification: name lookup for valid code
    void getName_valid_returnsMike() {
        assertEquals("Mike", libs.getName(valid));
    }

    @Test
    // specification: name lookup throws on invalid code
    void getName_invalid_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.getName(invalid));
    }

    @Test
    // structural: exactly three auth codes
    void getAuthCodes_sizeIsThree() {
        assertEquals(3, libs.getAuthCodes().size());
    }

    @Test
    // structural: auth codes contain expected entries
    void getAuthCodes_containsAllKeys() {
        Set<String> codes = libs.getAuthCodes();
        assertTrue(codes.containsAll(Set.of("123456", "654321", "000000")));
    }

    @Test
    // structural: auth codes set is unmodifiable
    void getAuthCodes_unmodifiable_throws() {
        assertThrows(UnsupportedOperationException.class,
            () -> libs.getAuthCodes().add("111111"));
    }

    @Test
    // specification: initial salary total is zero
    void initialTotalSalary_isZero() {
        assertEquals(0.0, libs.getTotalSalaryWithdrawn(valid));
    }

    @Test
    // specification: recording salary updates total
    void recordSalary_valid_updatesTotal() {
        libs.recordSalaryWithdrawal(valid, 100.0);
        assertEquals(100.0, libs.getTotalSalaryWithdrawn(valid));
    }

    @Test
    // specification: negative salary throws
    void recordSalary_negative_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.recordSalaryWithdrawal(valid, -1.0));
    }

    @Test
    // specification: salary recording fails for bad code
    void recordSalary_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.recordSalaryWithdrawal(invalid, 10.0));
    }

    @Test
    // specification: successful book purchase logged
    void recordBookPurchase_valid_increasesList() {
        libs.recordBookPurchase(valid, 20.0);
        assertEquals(1, libs.getPurchasedBooks(valid).size());
    }

    @Test
    // specification: negative book cost throws
    void recordBookPurchase_negativeCost_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.recordBookPurchase(valid, -5.0));
    }

    @Test
    // specification: purchase fails for bad code
    void recordBookPurchase_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.recordBookPurchase(invalid, 10.0));
    }

    @Test
    // specification: retrieving purchases fails for bad code
    void getPurchasedBooks_invalidCode_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> libs.getPurchasedBooks(invalid));
    }

    @Test
    // structural: purchase history list is read-only
    void getPurchasedBooks_unmodifiable_throws() {
        libs.recordBookPurchase(valid, 30.0);
        assertThrows(UnsupportedOperationException.class,
            () -> libs.getPurchasedBooks(valid).add(5.0));
    }

    @Test
    // structural: private constructor rejects wrong format
    void privateConstructor_badAuthCode_throws() throws Exception {
        Constructor<?> ctor = Class.forName("Librarians$Librarian")
            .getDeclaredConstructor(String.class, String.class);
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(
            InvocationTargetException.class,
            () -> ctor.newInstance("Name", "abc123")
        );
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    // structural: private constructor rejects null code
    void privateConstructor_nullAuthCode_throws() throws Exception {
        Constructor<?> ctor = Class.forName("Librarians$Librarian")
            .getDeclaredConstructor(String.class, String.class);
        ctor.setAccessible(true);

        InvocationTargetException ex = assertThrows(
            InvocationTargetException.class,
            () -> ctor.newInstance("Name", null)
        );
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }
}

