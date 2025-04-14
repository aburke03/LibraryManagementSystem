import java.util.*;

public class Library {
    private Map<String, Book> allBooks = new HashMap<>();
    private Map<String, Member> allMembers = new HashMap<>();

    public void addBook(Book book) {
        allBooks.put(book.getBookId(), book);
    }

    public void removeBook(String bookId) {
        allBooks.remove(bookId);
    }

    public void addMember(Member member) {
        allMembers.put(member.getMemberId(), member);
    }

    public void revokeMembership(String memberId) {
        allMembers.remove(memberId);
    }

    public String whoHasBook(String bookId) {
        for (Member member : allMembers.values()) {
            for (Book book : member.getBorrowedBookList()) {
                if (book.getBookId().equals(bookId)) {
                    return member.getName();
                }
            }
        }
        return "Not checked out.";
    }

    public Collection<Member> getAllMembers() {
        return allMembers.values();
    }

    public Collection<Book> getAllBooks() {
        return allBooks.values();
    }

    public Book findBookByName(String name) {
        for (Book book : allBooks.values()) {
            if (book.getName().equalsIgnoreCase(name)) {
                return book;
            }
        }
        return null;
    }

    public Book getBookById(String bookId) {
        return allBooks.get(bookId);
    }

    public Member getMemberById(String memberId) {
        return allMembers.get(memberId);
    }

    public void checkoutBook(Member member, Book book) {
        if (book.isAvailable()) {
            member.addBorrowedBook(book);
            book.setAvailable(false);
        }
    }

    public void returnBook(Member member, Book book) {
        member.removeBorrowedBook(book.getBookId());
        book.setAvailable(true);
    }
}
