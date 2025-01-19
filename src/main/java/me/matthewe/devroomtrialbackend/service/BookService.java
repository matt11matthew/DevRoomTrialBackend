package me.matthewe.devroomtrialbackend.service;

import me.matthewe.devroomtrialbackend.data.Account;
import me.matthewe.devroomtrialbackend.data.Book;
import me.matthewe.devroomtrialbackend.repository.AccountRepository;
import me.matthewe.devroomtrialbackend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;


    public Book createBook(Book account) {
        return bookRepository.save(account);
    }



    public Optional<Book> findById(UUID id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public boolean deleteBookById(UUID id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            bookRepository.delete(bookOptional.get());
            return true;
        }
        return false;
    }
}
