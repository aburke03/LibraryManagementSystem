import java.util.*;

/**
 * Represents a book in the library system.
 * Each book has identifying metadata and availability status.
 */

public class Book {
    private String name;
    private String author;
    private int year;
    private String isbn;
    private String bookId;
    private boolean isAvailable;
    private String genre;

    //Constructs a new Book instance with the given details.
    public Book(String name, String author, int year, String isbn, String bookId, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.bookId = bookId;
        this.genre = genre;
        this.isAvailable = true;
    }


    //Updates the book's descriptive fields.
    public void updateBookInfo(String name, String author, int year, String isbn, String genre) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.genre = genre;
    }

    //Returns a formatted string with all book details.
    public String getBookInfo() {
        return String.format("ID: %s | Name: %s | Author: %s | Year: %d | ISBN: %s | Genre: %s | Available: %b",
                bookId, name, author, year, isbn, genre, isAvailable);
    }

    //Sets the availability status of the book.
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    //Returns the availability status of the book.
    public boolean isAvailable() {
        return isAvailable;
    }

    //Returns the unique ID of the book.
    public String getBookId() {
        return bookId;
    }

    //Returns the name/title of the book.
    public String getName() {
        return name;
    }
}
