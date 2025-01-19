package me.matthewe.devroomtrialbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import me.matthewe.devroomtrialbackend.LoginRequest;
import me.matthewe.devroomtrialbackend.data.Account;
import me.matthewe.devroomtrialbackend.data.Book;
import me.matthewe.devroomtrialbackend.service.AccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
@RestController
@RequestMapping("/auth")
public class AccountController {

    private static final Log log = LogFactory.getLog(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Map to store active sessions by username
    private final Map<String, List<HttpSession>> activeSessions = new ConcurrentHashMap<>();




    // Add this method to the AccountController
    @GetMapping("/active-sessions")
    public ResponseEntity<?> getActiveSessions(@RequestParam(required = false) String username,
                                               @RequestParam(required = false) String email) {
        if (username == null && email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Either username or email must be provided.");
        }

        // Find user by username or email
        Optional<Account> account = username != null
                ? accountService.getAccountByUsername(username)
                : accountService.getAccountByEmail(email);

        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // Get the user's active sessions
        List<HttpSession> sessions = activeSessions.get(account.get().getUsername());
        if (sessions != null && !sessions.isEmpty()) {
            List<Map<String, Object>> sessionDetails = new ArrayList<>();
            for (HttpSession session : sessions) {
                sessionDetails.add(Map.of(
                        "sessionId", session.getId(),
                        "creationTime", session.getCreationTime(),
                        "lastAccessedTime", session.getLastAccessedTime()
                ));
            }
            return ResponseEntity.ok(sessionDetails);
        }

        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(Map.of("loggedIn", true, "username", user.getUsername()));
        }
        return ResponseEntity.ok(Map.of("loggedIn", false));
    }

    // Register a new user
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
        activeSessions.computeIfAbsent(createdAccount.getUsername(), k -> new ArrayList<>()).add(session);
        log.info("Registered and logged in account " + account.getUsername());

        return ResponseEntity.ok(Map.of("message", "User registered and logged in successfully!", "username", account.getUsername()));
    }

    // Login a user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Optional<Account> account = accountService.getAccountByUsername(loginRequest.getUsername());
        if (account.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), account.get().getPassword())) {
            session.setAttribute("user", account.get());

            // Track the session for the user
            activeSessions.computeIfAbsent(account.get().getUsername(), k -> new ArrayList<>()).add(session);

            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    // Logout the current session
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            // Remove the session from active sessions
            List<HttpSession> sessions = activeSessions.get(user.getUsername());
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    activeSessions.remove(user.getUsername());
                }
            }

            log.info("User " + user.getUsername() + " logged out.");
            session.invalidate(); // Invalidate the session
            return ResponseEntity.ok("Logout successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user is logged in.");
    }
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        // If X-Forwarded-For contains multiple IPs, take the first one
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        return clientIp;
    }
    // View active sessions for the logged-in user
    @GetMapping("/sessions")
    public ResponseEntity<?> viewActiveSessions(HttpSession session, HttpServletRequest request) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            List<HttpSession> sessions = activeSessions.get(user.getUsername());
            if (sessions != null) {
                List<Map<String, Object>> sessionDetails = new ArrayList<>();
                for (HttpSession s : sessions) {
                    sessionDetails.add(Map.of(
                            "sessionId", s.getId(),
                            "creationTime", s.getCreationTime(),
                            "lastAccessedTime", s.getLastAccessedTime()
                    ));
                }
                return ResponseEntity.ok(sessionDetails);
            }
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
    }

    // Logout a specific session by sessionId
    @PostMapping("/logout-session")
    public ResponseEntity<?> logoutSpecificSession(@RequestParam String sessionId, HttpSession currentSession) {
        Account user = (Account) currentSession.getAttribute("user");
        if (user != null) {
            synchronized (activeSessions) {
                List<HttpSession> sessions = activeSessions.get(user.getUsername());
                if (sessions != null) {
                    HttpSession targetSession = null;
                    for (HttpSession s : sessions) {
                        if (s.getId().equals(sessionId)) {
                            targetSession = s;
                            break;
                        }
                    }
                    if (targetSession != null) {
                        sessions.remove(targetSession);
                        targetSession.invalidate();
                        log.info("User " + user.getUsername() + " logged out from session " + sessionId);
                        if (sessions.isEmpty()) {
                            activeSessions.remove(user.getUsername());
                        }
                        return ResponseEntity.ok("Logged out from session: " + sessionId);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
    }

}
