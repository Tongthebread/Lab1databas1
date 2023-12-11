package kth.decitong.librarydb.model;

import java.sql.Date;
import java.util.ArrayList;

public class Book implements Comparable<Book>{

    private final int bookId;
    private final String isbn; // should check format (10 eller 13 siffror)
    private final String title;
    private final Date published;
    private int rating;
    private ArrayList<Author> authors;
    private Genre genre;
    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"

    public Book(int bookId, String isbn, String title, Date published, int rating, Genre genre) {
        this.bookId = bookId;
        if(checkISBN(isbn)){
            this.isbn = isbn;
        }
        else throw new IllegalArgumentException("Invalid ISBN");

        this.title = title;
        this.published = published;
        if (checkRating(rating)){
            this.rating = rating;
        }
        else throw new IllegalArgumentException("Invalid rating");
        authors = new ArrayList<>();
        this.genre = genre;
    }

    public int getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }

    public int getRating() {
        return rating;
    }

    public ArrayList<Author> getAuthors() {
        return new ArrayList<>(authors);
    }

    public void addAuthors(Author authors) {
        if (!this.authors.equals(authors)){
            this.authors.add(authors);
        }
    }

    private boolean checkISBN(String isbn) {
        String regex = "\\d{10}|\\d{13}";
        return isbn.matches(regex);
    }


    private boolean checkRating(int rating){
        return rating >= 1 && rating <= 5;
    }

    public Genre getGenre() {
        return genre;
    }

    @Override
    public int compareTo(Book o) {
        int titleCompare = title.compareTo(o.title);
        if (titleCompare == 0) {
            return Integer.compare(bookId, o.bookId);
        }

        return titleCompare;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Book){
            return this.compareTo((Book) o) == 0;
        }
        return false;
    }
}