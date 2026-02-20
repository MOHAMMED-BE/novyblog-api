package org.example.blogapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BlogapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogapiApplication.class, args);
    }

}
