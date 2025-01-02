package me.matthewe.premiertrialbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "me.matthewe.premiertrialbackend")
@EnableJpaRepositories(basePackages = "me.matthewe.premiertrialbackend.repository")
@EntityScan(basePackages = "me.matthewe.premiertrialbackend.data")
public class PremierTrialBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PremierTrialBackendApplication.class, args);
    }
}
