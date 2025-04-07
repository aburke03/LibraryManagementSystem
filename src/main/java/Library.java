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

    public boolean bookAvailability(String bookId) {
        Book book = allBooks.get(bookId);
        return book != null && book.checkAvailability();
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

    public Book findBookByName(String name) {
        for (Book book : allBooks.values()) {
            if (book.getName().equalsIgnoreCase(name)) {
                return book;
            }
        }
        return null;
    }

    public void checkoutBook(String memberId, String bookId) {
        Member member = allMembers.get(memberId);
        Book book = allBooks.get(bookId);
        if (member != null && book != null && book.checkAvailability()) {
            member.addBorrowedBook(book);
            book.setAvailable(false);
        }
    }

    public void returnBook(String memberId, String bookId) {
        Member member = allMembers.get(memberId);
        Book book = allBooks.get(bookId);
        if (member != null && book != null) {
            member.removeBorrowedBook(bookId);
            book.setAvailable(true);
        }
    }

    public Map<String, Book> getAllBooks() {
        return allBooks;
    }
}
