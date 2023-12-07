package kth.decitong.librarydb.view;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import kth.decitong.librarydb.model.Author;
import kth.decitong.librarydb.model.Book;
import kth.decitong.librarydb.model.BooksDbInterface;
import kth.decitong.librarydb.model.SearchMode;
import java.util.ArrayList;
import java.util.List;
import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private static BooksPane booksView; // view
    private static BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = new ArrayList<>();
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        // Implementera sökning efter ISBN
                        break;
                    case Author:
                        // Implementera sökning efter författare
                        break;
                    default:
                        break;
                }
                // Hämta och associera författare med varje bok i result
                for (Book book : result) {
                    List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                    book.getAuthors().clear();
                    book.getAuthors().addAll(authors);
                }

                if (result.isEmpty()) {
                    booksView.showAlertAndWait("No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait("Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.", ERROR);
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

    public static void fetchAllAuthors(TableView<Author> authorTable) {
        try {
            List<Author> authors = booksDb.getAllAuthors(); // Implement this method in BooksDbImpl
            authorTable.setItems(FXCollections.observableArrayList(authors));
        } catch (Exception e) {
            booksView.showAlertAndWait("Error fetching authors from database", ERROR);
        }
    }

    public static List<Author> fetchAuthorsForBook(int bookId) {
        try {
            return booksDb.getAuthorsForBook(bookId);
        } catch (Exception e) {
            booksView.showAlertAndWait("Error fetching authors for book", ERROR);
            return new ArrayList<>();
        }
    }

    public static void fetchAllBooks() {
        try {
            List<Book> allBooks = booksDb.getAllBooks();
            if (allBooks.isEmpty()) {
                booksView.showAlertAndWait("No books found in the database.", Alert.AlertType.INFORMATION);
            } else {
                // Hämta och associera författare med varje bok
                for (Book book : allBooks) {
                    List<Author> authors = booksDb.getAuthorsForBook(book.getBookId());
                    book.getAuthors().clear();
                    book.getAuthors().addAll(authors);
                }
                booksView.displayBooks(allBooks);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Error fetching all books from database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


}

