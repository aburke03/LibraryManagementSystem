import java.util.*;

public class Member {
    private String name;
    private String email;
    private String memberId;
    private List<Book> borrowedBooks;

    public Member(String name, String email, String memberId) {
        if (name == null || email == null || memberId == null) {
            throw new IllegalArgumentException("null field!");
        }
        this.name = name;
        this.email = email;
        this.memberId = memberId;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getMemberInfo() {
        return String.format("ID: %s | Name: %s | Email: %s\n", memberId, name, email);
    }

    public List<Book> getBorrowedBookList() {
        return borrowedBooks;
    }

    public void addBorrowedBook(Book book) {
        if (!borrowedBooks.contains(book)) borrowedBooks.add(book);
    }

    public void removeBorrowedBook(String bookId) {
        if (bookId == null) throw new IllegalArgumentException("null field!");
        borrowedBooks.removeIf(book -> book.getBookId().equals(bookId));
    }

    public void updateMemberInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMemberId() {
        return memberId;
    }
}
