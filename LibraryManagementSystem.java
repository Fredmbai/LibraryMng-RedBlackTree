import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Red-Black Tree Node
class RedBlackNode {
    String title;
    RedBlackNode left, right, parent;
    int color;  // 0 = Black, 1 = Red

    public RedBlackNode(String title) {
        this.title = title;
        this.left = this.right = this.parent = null;
        this.color = 1;  // New nodes are red by default
    }
}

// Red-Black Tree Implementation
class RedBlackTree {
    private RedBlackNode root;
    private final RedBlackNode TNULL;

    public RedBlackTree() {
        TNULL = new RedBlackNode("");
        TNULL.color = 0;
        root = TNULL;
    }

    // Insert method with fix-up
    public void insert(String title) {
        RedBlackNode node = new RedBlackNode(title);
        RedBlackNode y = null;
        RedBlackNode x = root;

        while (x != TNULL) {
            y = x;
            if (node.title.compareTo(x.title) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y == null) {
            root = node;
        } else if (node.title.compareTo(y.title) < 0) {
            y.left = node;
        } else {
            y.right = node;
        }

        node.left = TNULL;
        node.right = TNULL;
        node.color = 1;
        fixInsert(node);
    }

    // Fixes the Red-Black Tree after insertion
    private void fixInsert(RedBlackNode k) {
        RedBlackNode u;
        while (k.parent != null && k.parent.color == 1) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right;

                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = 0;
    }

    // Helper function to perform left rotation
    private void leftRotate(RedBlackNode x) {
        RedBlackNode y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    // Helper function to perform right rotation
    private void rightRotate(RedBlackNode x) {
        RedBlackNode y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // In-order traversal for book listing
    public ObservableList<String> inorder() {
        ObservableList<String> bookList = FXCollections.observableArrayList();
        inorderHelper(this.root, bookList);
        return bookList;
    }

    private void inorderHelper(RedBlackNode node, ObservableList<String> bookList) {
        if (node != TNULL) {
            inorderHelper(node.left, bookList);
            if (!node.title.isEmpty()) {
                bookList.add(node.title);
            }
            inorderHelper(node.right, bookList);
        }
    }
}

// Main JavaFX Application
public class LibraryManagementSystem extends Application {

    private final RedBlackTree libraryTree = new RedBlackTree();
    private final ObservableList<String> books = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");

        // UI Elements
        TextField bookTitleInput = new TextField();
        bookTitleInput.setPromptText("Enter Book Title");

        Button addBookButton = new Button("Add Book");
        Button borrowBookButton = new Button("Borrow Book");

        ListView<String> bookListView = new ListView<>(books);
        bookListView.setPrefHeight(200);

        addBookButton.setOnAction(e -> {
            String title = bookTitleInput.getText().trim();
            if (!title.isEmpty()) {
                libraryTree.insert(title);
                books.setAll(libraryTree.inorder());
                bookTitleInput.clear();
                showAlert("Success", "Book added: " + title);
            } else {
                showAlert("Error", "Book title cannot be empty.");
            }
        });

        borrowBookButton.setOnAction(e -> {
            String selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                books.remove(selectedBook);
                showAlert("Success", "You borrowed: " + selectedBook);
            } else {
                showAlert("Error", "Please select a book to borrow.");
            }
        });

        VBox layout = new VBox(10, bookTitleInput, addBookButton, bookListView, borrowBookButton);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
