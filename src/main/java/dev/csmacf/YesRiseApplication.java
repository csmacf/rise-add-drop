package dev.csmacf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("dev.csmacf")  
public class YesRiseApplication {
    public static void main(String[] args) {
      System.setProperty("spring.profiles.active", "prod");
      // System.out.println("Active profile: " + System.getProperty("spring.profiles.active"));
        SpringApplication.run(YesRiseApplication.class, args);
    }
}