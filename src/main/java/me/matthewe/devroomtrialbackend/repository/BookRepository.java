package me.matthewe.devroomtrialbackend.repository;

import me.matthewe.devroomtrialbackend.data.Account;
import me.matthewe.devroomtrialbackend.data.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findById(UUID id);

}