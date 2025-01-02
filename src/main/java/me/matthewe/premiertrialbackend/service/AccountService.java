package me.matthewe.premiertrialbackend.service;

import me.matthewe.premiertrialbackend.data.Account;
import me.matthewe.premiertrialbackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        // Add any validation logic here if needed
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
}