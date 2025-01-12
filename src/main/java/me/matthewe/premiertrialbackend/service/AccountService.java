package me.matthewe.premiertrialbackend.service;

import me.matthewe.premiertrialbackend.data.Account;
import me.matthewe.premiertrialbackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }


    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
}
