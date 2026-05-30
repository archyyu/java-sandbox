package com.script;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ScriptApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScriptApplication.class, args);
    }
}
