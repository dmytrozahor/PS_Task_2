package com.dmytrozah.profitsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.dmytrozah.profitsoft.config",
        "com.dmytrozah.profitsoft.service"
})
/*
@Slf4j
*/
public class Task2App /*implements CommandLineRunner */{

    /*private Booksclient booksclient;
*/

    public static void main(String[] args) {
        SpringApplication.run(Task2App.class, args);
    }

/*    @Override
    public void run(String... args) throws Exception {
        BookSaveDto dto = BookSaveDto.builder()
                .title("Test")
                .build();

        int bookId = booksclient.createBook(dto);

        log.info("Created book with id {}", bookId);
    }*/
}
