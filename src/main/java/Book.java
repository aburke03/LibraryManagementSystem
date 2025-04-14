import java.util.*;

public class Book {
    private String name;
    private String author;
    private int year;
    private String isbn;
    private String bookId;
    private boolean isAvailable;
    private String genre;

    public Book(String name, String author, int year, String isbn, String bookId, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.bookId = bookId;
        this.genre = genre;
        this.isAvailable = true;
    }

    public void updateBookInfo(String name, String author, int year, String isbn, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.genre = genre;
    }

    public String getBookInfo() {
        return String.format("ID: %s | Name: %s | Author: %s | Year: %d | ISBN: %s | Genre: %s | Available: %b",
                bookId, name, author, year, isbn, genre, isAvailable);
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }
}
