package net.caiena.survey;

import org.springframework.boot.SpringApplication;

/**
 * @author bzumpano
 * @since 3/23/16
 */
public abstract class ApplicationBase {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
