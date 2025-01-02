package me.matthewe.premiertrialbackend.controller;

import me.matthewe.premiertrialbackend.data.Account;
import me.matthewe.premiertrialbackend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping("/{username}")
    public Account getAccountByUsername(@PathVariable String username) {
        return accountService.getAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}