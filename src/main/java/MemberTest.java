import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.api.constraints.IntRange;

import java.util.*;

/**
 * Test suite for the Book class in the Library Management System.
 * This suite combines specification-based testing, structural testing (for JaCoCo code coverage),
 * and property-based testing using jqwik.
 */
public class MemberTest {

    private Member member;
    private final String memberName = "Cornelious Booklover";
    private String memberEmail = "ilovebooks@gmail.com";
    private String memberId = "2253056939";

    @BeforeEach
    public void setUp() {
        member = new Member(memberName, memberEmail, memberId);
    }

    // Specification-based tests
    // null initialization
    @Test
    public void nullMemberCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Member(null, null, null));
    }

    // null book removal
    @Test
    public void nullRemoveBook() {
        assertThrows(IllegalArgumentException.class, () -> member.removeBorrowedBook(null));
    }

    // cannot borrow duplicate books
    @Test
    public void duplicateBorrowing() {
        Book book = new Book("I'm a Book: The Story of Being Novel", "john bookingham", 1503, "ISBN305", "book1", "Nonfiction");
        member.addBorrowedBook(book);
        member.addBorrowedBook(book);
        assertEquals(member.getBorrowedBookList().size(), 1);
    }

    // Property-based tests using jqwik

    // Member is properly intialized
    @Property
    public void memberInitialization(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String name,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String email,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String id
    ) {
        email = email + "@gmail.com";
        // Property-based test: A newly created book should always be available
        member = new Member(name, email, id);
        assertEquals(name, member.getName(), "Member name should match the constructor parameter");
        assertEquals(email, member.getEmail(), "Member email should match the constructor parameter");
        assertEquals(id, member.getMemberId(), "Member ID should match the constructor parameter");
        assertEquals(member.getBorrowedBookList(), Collections.emptyList(), "Member Books should match the constructor parameter");
    }

    // generating a valid book object
    @Provide
    Arbitrary<Book> validBooks() {
        Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> authors = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<Integer> years = Arbitraries.integers().between(1000, 2023);
        Arbitrary<String> isbns = Arbitraries.strings().ofMinLength(10).ofMaxLength(17);
        Arbitrary<String> ids = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
        Arbitrary<String> genres = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
        return Combinators.combine(names, authors, years, isbns, ids, genres)
                .as((name, author, year, isbn, id, genre) ->
                        new Book(name, author, year, isbn, id, genre));
    }

    // generating a valid member object
    @Provide
    Arbitrary<Member> validMembers() {
        Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> emails = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> ids = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
        return Combinators.combine(names, emails, ids)
                .as((name, email, id) ->
                        new Member(name, email, id));
    }

    // tests if a member can borrow any book
    @Property
    public void memberBorrowsBooks(
            @ForAll @From("validMembers") Member member,
            @ForAll List<@From("validBooks") Book> books
    ) {
        // Property-based test: Books should be able to be added
        for (Book book : books) {
            member.addBorrowedBook(book);
        }

        assertEquals(member.getBorrowedBookList().size(), books.size());
        for (Book book : books) {
            assertEquals(true, member.getBorrowedBookList().contains(book));
        }
    }

    // tests if a member can remove any borrowed book
    @Property
    public void memberRemovesBooks(
            @ForAll @From("validMembers") Member member,
            @ForAll List<@From("validBooks") Book> initialBooks
    ) {
        // Property-based test: Books should be able to be added
        for (Book book : initialBooks) {
            member.addBorrowedBook(book);
        }

        for (Book book : initialBooks) {
            member.removeBorrowedBook(book.getBookId());
        }

        assertEquals(member.getBorrowedBookList().size(), 0);
    }

    // tests member update functionality
    @Property
    public void memberUpdateProperty(
            @ForAll @From("validMembers") Member member,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String newName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String newEmail
    ) {
        member.updateMemberInfo(newName, newEmail);
        assertEquals(newName, member.getName(), "Member Name should be updated");
        assertEquals(newEmail, member.getEmail(), "Member Email should be updated");
    }

    @Property
    public void memberGetInfo(
            @ForAll @From("validMembers") Member member
    ) {
        assertEquals(String.format("ID: %s | Name: %s | Email: %s\n", member.getMemberId(), member.getName(), member.getEmail()), member.getMemberInfo());
    }
}