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

import java.util.Map;
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
    public ResponseEntity<?> register(@RequestBody Account account, HttpSession session) {
        // Check if username is already taken
        if (accountService.getAccountByUsername(account.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username is already taken."));
        }

        // Check if email is already used
        if (accountService.getAccountByEmail(account.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email is already in use."));
        }

        // Hash the password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account createdAccount = accountService.createAccount(account);

        // Automatically log in the user after registration
        session.setAttribute("user", createdAccount);
        log.info("Registered and logged in account " + account.getUsername());

        return ResponseEntity.ok(Map.of("message", "User registered and logged in successfully!", "username", account.getUsername()));
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
    @GetMapping("/account/{email}")
    public ResponseEntity<?> getAccountByEmail(@PathVariable String email) {
        Optional<Account> account = accountService.getAccountByEmail(email);
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

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Map.of("loggedIn", true, "username", user.getUsername()));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
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