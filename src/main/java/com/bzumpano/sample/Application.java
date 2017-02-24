package com.bzumpano.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.bzumpano.sample","asset.pipeline.springboot"})
public class Application {

        public static void main(final String[] args) {
            SpringApplication.run(Application.class, args);
        }

}