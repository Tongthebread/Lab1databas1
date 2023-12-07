package kth.decitong.librarydb.view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import kth.decitong.librarydb.model.*;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private MenuBar menuBar;

    public BooksPane(BooksDbImpl booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false);
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, Integer> bookIDCol = new TableColumn<>("BookID");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        TableColumn<Book, Integer> ratingCol = new TableColumn<>("Rating");
        TableColumn<Book, Genre> genreCol = new TableColumn<>("Genre");

        // Set cell value factories
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        // Custom cell value factory for author column
        authorCol.setCellValueFactory(cellData -> {
            List<Author> authors = cellData.getValue().getAuthors();
            if (authors != null && !authors.isEmpty()) {
                String authorNames = authors.stream()
                        .map(author -> author.getFirstName() + " " + author.getLastName())
                        .collect(Collectors.joining(", "));
                return new ReadOnlyStringWrapper(authorNames);
            }
            return new ReadOnlyStringWrapper("");
        });

        // Add columns to table
        booksTable.getColumns().addAll(titleCol, bookIDCol, isbnCol, publishedCol, authorCol, ratingCol, genreCol);

        // Set items in the table
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        connectItem.setOnAction(e -> Controller.connect());
        MenuItem disconnectItem = new MenuItem("Disconnect");
        disconnectItem.setOnAction(e -> Controller.disconnect());
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        addItem.setOnAction(e -> showAddBookDialog());
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(e -> showRemoveBookDialog());
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, manageMenu);
    }

    private void showRemoveBookDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Book");
        dialog.setHeaderText("Enter Book ID to Remove");
        dialog.setContentText("Book ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(bookIdString -> {
            try {
                int bookId = Integer.parseInt(bookIdString);
                Controller.deleteBook(bookId);
                booksInTable.removeIf(book -> book.getBookId() == bookId);
            } catch (NumberFormatException e) {
                showAlertAndWait("Invalid Book ID: " + bookIdString, Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlertAndWait("Error removing book from database", Alert.AlertType.ERROR);
            }
        });
    }



    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter Book Details");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        List<Author> authors = new ArrayList<>();

        Button addAuthorButton = new Button("Add Author");
        addAuthorButton.setOnAction(e -> {
            Author author = showAddAuthorDialog();
            if (author != null) {
                authors.add(author);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bookIDField = new TextField();
        bookIDField.setPromptText("Book ID");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        DatePicker publishedDateField = new DatePicker();
        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (1-5)");
        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(bookIDField, 1, 0);
        grid.add(new Label("ISBN:"), 0, 1);
        grid.add(isbnField, 1, 1);
        grid.add(new Label("Title:"), 0, 2);
        grid.add(titleField, 1, 2);
        grid.add(new Label("Published Date:"), 0, 3);
        grid.add(publishedDateField, 1, 3);
        grid.add(new Label("Rating:"), 0, 4);
        grid.add(ratingField, 1, 4);
        grid.add(new Label("Genre:"), 0, 5);
        grid.add(genreField, 1, 5);
        grid.add(addAuthorButton, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    int bookId = Integer.parseInt(bookIDField.getText());
                    String isbn = isbnField.getText();
                    String title = titleField.getText();
                    Date publishedDate = Date.valueOf(publishedDateField.getValue());
                    int rating = Integer.parseInt(ratingField.getText());
                    Genre genre = Genre.valueOf(genreField.getText().toUpperCase()); // Anta att Genre är en enum

                    Book book = new Book(bookId, isbn, title, publishedDate, rating, genre);

                    for (Author author : authors) {
                        book.addAuthors(author); // Using the addAuthor method of Book class
                    }
                    return book;
                } catch (Exception e) {
                    showAlertAndWait("Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            Controller.addBook(book);
            booksInTable.add(book);
        });
    }

    private Author showAddAuthorDialog() {
        Dialog<Author> dialog = new Dialog<>();
        dialog.setTitle("Add New Author");
        dialog.setHeaderText("Enter Author Details");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField authorIDField = new TextField();
        authorIDField.setPromptText("Author ID");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        DatePicker birthDateField = new DatePicker();

        grid.add(new Label("Author ID:"), 0, 0);
        grid.add(authorIDField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Birth Date:"), 0, 3);
        grid.add(birthDateField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    int authorId = Integer.parseInt(authorIDField.getText());
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    Date birthDate = Date.valueOf(birthDateField.getValue());

                    return new Author(authorId, firstName, lastName, birthDate);
                } catch (Exception e) {
                    showAlertAndWait("Invalid input: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Author> result = dialog.showAndWait();
        result.ifPresent(author -> {
            Controller.addAuthor(author);
        });
        return result.orElse(null);
    }

}