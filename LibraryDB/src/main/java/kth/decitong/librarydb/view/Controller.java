package kth.decitong.librarydb.view;

import javafx.scene.control.Alert;
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
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        // ...
                        break;
                    case Author:
                        // ...
                        break;
                    default:
                        result= new ArrayList<>();
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.",ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
    public static void addBook(Book book) {
        try {
            booksDb.addBook(book);
            for (Author author : book.getAuthors()) {
                booksDb.addAuthorToBook(author, book);
            }
            booksView.showAlertAndWait("Book and authors added successfully", INFORMATION);
        } catch (Exception e){
            booksView.showAlertAndWait("Error adding book and authors to database", ERROR);
        }
    }

    public static void deleteBook(Book book){
        try{
            booksDb.deleteBook(book.getBookId());
            booksView.showAlertAndWait("Book removed succesfully.", INFORMATION);
        } catch (Exception e){
            booksView.showAlertAndWait("Error removing book from database", ERROR);
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
}

