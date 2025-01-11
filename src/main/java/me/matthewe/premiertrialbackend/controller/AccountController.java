package me.matthewe.premiertrialbackend.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.matthewe.premiertrialbackend.LoginRequest;
import me.matthewe.premiertrialbackend.data.Account;
import me.matthewe.premiertrialbackend.service.AccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AccountController {


    private static final Log log = LogFactory.getLog(AccountController.class);
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the PasswordEncoder

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        // Hash the password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountService.createAccount(account);
        log.info("Registered account " + account.getUsername());
        return ResponseEntity.ok("User registered successfully!");
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<?> getAccountByUsername(@PathVariable String username) {
        Optional<Account> account = accountService.getAccountByUsername(username);
        if (account.isPresent()) {
            Account user = account.get();
            AccountDTO userDTO = new AccountDTO(user.getId(), user.getUsername(), user.getEmail());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            log.info("User " + user.getUsername() + " logged out.");
            session.invalidate();  // Invalidate the session
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user is logged in.");
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


}