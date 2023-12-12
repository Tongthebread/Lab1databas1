package kth.decitong.librarydb.model;

import java.sql.*;
import java.util.ArrayList;
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
    private Connection conn;
    public BooksDbImpl() {}

    @Override
    public void connect(String database) throws BooksDbException {
        String server = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
        String user = "root";
        String pwd = "psyke456SONG";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
        } catch (ClassNotFoundException e){
            throw new BooksDbException("MySQL JDBC driver not found");
        }catch (SQLException e){
            throw new BooksDbException("Error connection to database");
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn != null && !conn.isClosed()) {
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

                List<Author> authors = getAuthorsForBook(bookId);
                for (Author author : authors) {
                    book.addAuthors(author);
                }

                result.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by title", e);
        }
        return result;
    }

    @Override
    public ArrayList<Book> searchBooksByAuthor(String authorName) throws BooksDbException {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT Book.* FROM Book " +
                "INNER JOIN AuthorOfBook ON Book.bookID = AuthorOfBook.bookID " +
                "INNER JOIN Author ON AuthorOfBook.authorID = Author.authorID " +
                "WHERE LOWER(Author.firstName) LIKE ? OR LOWER(Author.lastName) LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + authorName.toLowerCase() + "%");
            pstmt.setString(2, "%" + authorName.toLowerCase() + "%");
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

                List<Author> authors = getAuthorsForBook(bookId);
                for (Author author : authors) {
                    book.addAuthors(author);
                }

                books.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by author", e);
        }
        return books;
    }


    @Override
    public ArrayList<Book> searchBooksByGenre(String genre) throws BooksDbException {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE LOWER(genre) = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, genre.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("bookID");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                Date published = rs.getDate("published");
                int rating = rs.getInt("rating");
                String genreStr = rs.getString("genre");
                Genre genreEnum = Genre.valueOf(genreStr.toUpperCase());

                Book book = new Book(bookId, isbn, title, published, rating, genreEnum);

                List<Author> authors = getAuthorsForBook(bookId);
                for (Author author : authors) {
                    book.addAuthors(author);
                }

                books.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by genre", e);
        } catch (IllegalArgumentException e) {
            throw new BooksDbException("Error with genre enum value", e);
        }
        return books;
    }

    @Override
    public ArrayList<Book> searchBooksByRating(int rating) throws BooksDbException {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE rating = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rating);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int bookId = rs.getInt("bookID");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                Date published = rs.getDate("published");
                rating = rs.getInt("rating");
                String genreStr = rs.getString("genre");
                Genre genre = Genre.valueOf(genreStr.toUpperCase());

                Book book = new Book(bookId, isbn, title, published, rating, genre);

                List<Author> authors = getAuthorsForBook(bookId);
                for (Author author : authors) {
                    book.addAuthors(author);
                }

                books.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by rating", e);
        }
        return books;
    }


    @Override
    public ArrayList<Book> searchBooksByISBN(String ISBN) throws BooksDbException {
        ArrayList<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE isbn = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ISBN);
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

                List<Author> authors = getAuthorsForBook(bookId);
                for (Author author : authors) {
                    book.addAuthors(author);
                }

                books.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching books by ISBN", e);
        }
        return books;
    }


    @Override
    public void deleteBook(int bookID) throws BooksDbException {
        try {
            String sqlDeleteAuthorOfBook = "DELETE FROM AuthorOfBook WHERE bookID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteAuthorOfBook)) {
                pstmt.setInt(1, bookID);
                pstmt.executeUpdate();
            }

            String sqlDeleteBook = "DELETE FROM Book WHERE bookID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteBook)) {
                pstmt.setInt(1, bookID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new BooksDbException("No book found with ID: " + bookID);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book from database", e);
        }
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        try {
            String sqlBook = "INSERT INTO Book (isbn, bookID, title, published, rating, genre) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtBook = conn.prepareStatement(sqlBook)) {
                pstmtBook.setString(1, book.getIsbn());
                pstmtBook.setInt(2, book.getBookId());
                pstmtBook.setString(3, book.getTitle());
                pstmtBook.setDate(4, new java.sql.Date(book.getPublished().getTime()));
                pstmtBook.setInt(5, book.getRating());
                pstmtBook.setString(6, String.valueOf(book.getGenre()));
                pstmtBook.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book to database", e);
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
            System.out.println("no author");
            throw new BooksDbException("Error linking author to book in database", e);
        }
    }

    @Override
    public List<Author> getAuthorsForBook(int bookID) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT Author.* FROM Author INNER JOIN AuthorOfBook ON Author.authorID = AuthorOfBook.authorID WHERE AuthorOfBook.bookID = ?";

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

    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM Author";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int authorID = rs.getInt("authorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                Date birthDate = rs.getDate("birthDate");
                authors.add(new Author(authorID, firstName, lastName, birthDate));
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error fetching authors", e);
        }
        return authors;
    }

}
