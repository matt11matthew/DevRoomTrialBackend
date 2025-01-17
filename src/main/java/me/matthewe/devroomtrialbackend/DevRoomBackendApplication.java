package me.matthewe.devroomtrialbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "me.matthewe.devroomtrialbackend")
@EnableJpaRepositories(basePackages = "me.matthewe.devroomtrialbackend.repository")
@EntityScan(basePackages = "me.matthewe.devroomtrialbackend.data")
public class DevRoomBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevRoomBackendApplication.class, args);
    }


}
