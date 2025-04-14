import java.util.*;

public class Member {
    private String name;
    private String email;
    private String memberId;
    private List<Book> borrowedBooks;

    public Member(String name, String email, String memberId) {
        this.name = name;
        this.email = email;
        this.memberId = memberId;
        this.borrowedBooks = new ArrayList<>();
    }

    public void printMemberInfo() {
        System.out.printf("ID: %s | Name: %s | Email: %s\n", memberId, name, email);
    }

    public List<Book> getBorrowedBookList() {
        return borrowedBooks;
    }

    public void addBorrowedBook(Book book) {
        borrowedBooks.add(book);
    }

    public void removeBorrowedBook(String bookId) {
        borrowedBooks.removeIf(book -> book.getBookId().equals(bookId));
    }

    public void updateMemberInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getMemberId() {
        return memberId;
    }
}
