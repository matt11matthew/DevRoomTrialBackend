package me.matthewe.premiertrialbackend.controller;

import jakarta.servlet.http.HttpSession;
import me.matthewe.premiertrialbackend.LoginRequest;
import me.matthewe.premiertrialbackend.data.Account;
import me.matthewe.premiertrialbackend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the PasswordEncoder

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        // Hash the password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountService.createAccount(account);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<Account> account = accountService.getAccountByUsername(loginRequest.getUsername());
        if (account.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), account.get().getPassword())) {
            session.setAttribute("user", account.get());
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }
}