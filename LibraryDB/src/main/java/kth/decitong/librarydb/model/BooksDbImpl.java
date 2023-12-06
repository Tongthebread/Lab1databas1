/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kth.decitong.librarydb.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 * <p>
 * Your implementation must access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDbImpl implements BooksDbInterface {

    private final List<Book> books;
    private Connection conn;

    public BooksDbImpl() {
        books = Arrays.asList();
    }

    @Override
    public boolean connect(String database) throws BooksDbException {
        String server = "jdbc:mysql://myplace.se:3306/" + database + "?UseClientEnc=UTF8";
        String user = "root";
        String pwd = "psyke456SONG";
        System.out.println("connect...");
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connection succeeded");
            return true;
        } catch (ClassNotFoundException e){
            throw new BooksDbException("MySQL JDBC driver not found");
        }catch (SQLException e){
            System.out.println("Couldnt connect");
            throw new BooksDbException("Error connection to database");
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Disconnect succeeded");
                conn.close();
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error disconnecting from database");
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE LOWER(title) LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTitle.toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("bookID");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                Date published = rs.getDate("published");
                int rating = rs.getInt("rating");
                String genreStr = rs.getString("genre");
                Genre genre = Genre.valueOf(genreStr.toUpperCase());

                Book book = new Book(bookId, isbn, title, published, rating, genre);
                result.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by title", e);
        }
        return result;
    }


    @Override
    public ArrayList<Book> searchBooksByAuthor(String authorName) throws BooksDbException {
        return null;
    }

    @Override
    public ArrayList<Book> searchBooksByGenre(String genre) throws BooksDbException {
        return null;
    }

    @Override
    public ArrayList<Book> searchBooksByRating(int rating) throws BooksDbException {
        return null;
    }

    @Override
    public ArrayList<Book> searchBooksByISBN(String ISBN) throws BooksDbException {
        return null;
    }

    @Override
    public void deleteBook(int bookID) throws BooksDbException {
        String sql = "DELETE FROM Book WHERE bookID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookID);
            int rowsFound = pstmt.executeUpdate();
            if (rowsFound == 0) {
                throw new BooksDbException("No book found with ID: " + bookID);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book from database", e);
        }
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        String sql = "INSERT INTO Book (isbn, bookID, title, published, rating, genre) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setInt(2, book.getBookId());
            pstmt.setString(3, book.getTitle());
            pstmt.setDate(4, book.getPublished());
            pstmt.setInt(5, book.getRating());
            pstmt.setString(6, String.valueOf(book.getGenre()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book to database", e);
        }

        for (Author author : book.getAuthors()) {
            addAuthorToBook(author, book);
        }
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException {
        String sql = "INSERT INTO Author (authorID, firstName, lastName, birthDate) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, author.getAuthorID());
            pstmt.setString(2, author.getFirstName());
            pstmt.setString(3, author.getLastName());
            pstmt.setDate(4, new java.sql.Date(author.getBirthDate().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding author to database", e);
        }
    }

    @Override
    public void addAuthorToBook(Author author, Book book) throws BooksDbException {
        String sql = "INSERT INTO AuthorOfBook (authorID, bookID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, author.getAuthorID());
            pstmt.setInt(2, book.getBookId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error linking author to book in database", e);
        }
    }

    @Override
    public List<Author> getAuthorsForBook(int bookID) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT Author.authorID, Author.firstName, Author.lastName, Author.birthDate FROM Author INNER JOIN AuthorOfBook ON Author.authorID = AuthorOfBook.authorID WHERE AuthorOfBook.bookID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Date birthDate = rs.getDate("birthDate");

                Author author = new Author(authorID, firstName, lastName, birthDate);
                authors.add(author);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching authors for book", e);
        }
        return authors;
    }
}
