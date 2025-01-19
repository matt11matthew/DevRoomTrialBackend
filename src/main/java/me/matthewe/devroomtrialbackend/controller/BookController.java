package me.matthewe.devroomtrialbackend.controller;

import me.matthewe.devroomtrialbackend.data.Book;
import me.matthewe.devroomtrialbackend.service.BookService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/book")
public class BookController {


    private static final Log log = LogFactory.getLog(BookController.class);
    @Autowired
    private BookService bookService;

    // Endpoint to get all books
    @GetMapping("/books")
    public ResponseEntity<Iterable<Book>> getAllBooks() {
        try {
            Iterable<Book> books = bookService.findAll();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error fetching all books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable UUID id, @RequestHeader("username") String username) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (!"checked_out".equals(book.getStatus()) || !username.equals(book.getCheckedOutBy())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot return this book.");
            }
            book.setStatus("Available");
            book.setCheckedOutBy(null);
            bookService.updateBook(book);
            return ResponseEntity.ok("Book returned successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<String> checkoutBook(@PathVariable UUID id, @RequestHeader("username") String username) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            if (!"Available".equals(book.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Book is already checked out.");
            }
            book.setStatus("checked_out");
            book.setCheckedOutBy(username);
            bookService.updateBook(book);
            return ResponseEntity.ok("Book checked out successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Map<String,Object> map) {
        try {
            // Ensure default values for required fields
            Book book = new Book((String) map.get("title"), (String) map.get("description"), (String) map.get("image_url"), "Available", null);


            Book createdBook = bookService.createBook(book);
            log.info("Created book " + createdBook.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (Exception e) {
            log.error("Error creating book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Endpoint to get a book by UUID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable UUID id) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            return ResponseEntity.ok(bookOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint to delete a book by UUID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable UUID id) {
        try {
            if (bookService.deleteBookById(id)) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Successfully deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
            }
        } catch (Exception e) {
            log.error("Error deleting book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the book.");
        }
    }


}