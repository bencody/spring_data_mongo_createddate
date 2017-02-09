package de.bencody.createdate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Created by bco on 09.02.2017.
 */
@SpringBootApplication
@EnableMongoAuditing
public class CreateDateMain {

    public static void main(String[] args) {
        SpringApplication.run(CreateDateMain.class);
    }
}
