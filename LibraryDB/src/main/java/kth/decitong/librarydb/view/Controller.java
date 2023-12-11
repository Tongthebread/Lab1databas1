package kth.decitong.librarydb.view;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import kth.decitong.librarydb.model.*;
import java.util.List;
import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private static BooksPane booksView;
    private static BooksDbInterface booksDb;

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        Controller.booksDb = booksDb;
        Controller.booksView = booksView;
    }

    public static void connect() {
        try {
            booksDb.connect("DB_LIBRARY"); // Use your actual database name
            booksView.showAlertAndWait("Connected to database successfully", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Failed to connect to database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void disconnect() {
        try {
            booksDb.disconnect();
            booksView.showAlertAndWait("Disconnected from database.", INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error disconnecting from database: " + e.getMessage(), ERROR);
        }
    }

    public static void addBook(Book book) {
        try {
            booksDb.addBook(book);
            for (Author author : book.getAuthors()) {
                booksDb.addAuthorToBook(author, book);
            }
            List<Author> fetchedAuthors = booksDb.getAuthorsForBook(book.getBookId());
            book.getAuthors().clear();
            book.getAuthors().addAll(fetchedAuthors);

            booksView.showAlertAndWait("Book and authors added successfully", INFORMATION);
        } catch (Exception e){
            booksView.showAlertAndWait("Error adding book and authors to database", ERROR);
        }
    }

    public static void addAuthor(Author author){
        try{
            booksDb.addAuthor(author);
            booksView.showAlertAndWait("Author added successfully.", INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error adding author to database", ERROR);
        }
    }

    public static void deleteBook(int bookId) {
        try {
            booksDb.deleteBook(bookId);
            booksView.showAlertAndWait("Book removed successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error removing book from database", Alert.AlertType.ERROR);
        }
    }

    public static void fetchAllAuthors(TableView<Author> authorTable) {
        try {
            List<Author> authors = booksDb.getAllAuthors();
            authorTable.setItems(FXCollections.observableArrayList(authors));
        } catch (Exception e) {
            booksView.showAlertAndWait("Error fetching authors from database", ERROR);
        }
    }

    protected void searchBooksByISBN(String isbn) {
        try {
            List<Book> result = booksDb.searchBooksByISBN(isbn);
            for (Book book : result) {
                List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                book.getAuthors().clear();
                book.getAuthors().addAll(authors);
            }
            if (result.isEmpty()) {
                booksView.showAlertAndWait("No books found with the given ISBN.", INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error searching books by ISBN: " + e.getMessage(), ERROR);
        }
    }
    protected void searchBooksByAuthor(String authorName) {
        try {
            List<Book> result = booksDb.searchBooksByAuthor(authorName);
            for (Book book : result) {
                List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                book.getAuthors().clear();
                book.getAuthors().addAll(authors);
            }
            if (result.isEmpty()) {
                booksView.showAlertAndWait("No books found for the author: " + authorName, INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error searching books by author: " + e.getMessage(), ERROR);
        }
    }
    protected void searchBooksByTitle(String title) {
        try {
            List<Book> result = booksDb.searchBooksByTitle(title);
            for (Book book : result) {
                List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                book.getAuthors().clear();
                book.getAuthors().addAll(authors);
            }
            if (result.isEmpty()) {
                booksView.showAlertAndWait("No books found for the title: " + title, INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error searching books by title: " + e.getMessage(), ERROR);
        }
    }
    protected void searchBooksByRating(int rating) {
        try {
            List<Book> result = booksDb.searchBooksByRating(rating);
            for (Book book : result) {
                List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                book.getAuthors().clear();
                book.getAuthors().addAll(authors);
            }
            if (result.isEmpty()) {
                booksView.showAlertAndWait("No books found for the rating: " + rating, INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error searching books by rating: " + e.getMessage(), ERROR);
        }
    }

    protected void searchBooksByGenre(String genre) {
        try {
            List<Book> result = booksDb.searchBooksByGenre(String.valueOf(genre));
            for (Book book : result) {
                List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                book.getAuthors().clear();
                book.getAuthors().addAll(authors);
            }
            if (result.isEmpty()) {
                booksView.showAlertAndWait("No books found for the genre: " + genre, INFORMATION);
            } else {
                booksView.displayBooks(result);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error searching books by genre: " + e.getMessage(), ERROR);
        }
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && !searchFor.trim().isEmpty()) {
                switch (mode) {
                    case Title:
                        searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        searchBooksByISBN(searchFor);
                        break;
                    case Author:
                        searchBooksByAuthor(searchFor);
                        break;
                    case Rating:
                        try {
                            int rating = Integer.parseInt(searchFor);
                            searchBooksByRating(rating);
                        } catch (NumberFormatException e) {
                            booksView.showAlertAndWait("Invalid rating format. Please enter a numeric value.", Alert.AlertType.ERROR);
                        }
                    case Genre:
                        searchBooksByGenre(searchFor);
                        break;
                }
            } else {
                booksView.showAlertAndWait("Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Search error: " + e.getMessage(), ERROR);
        }
    }

}

