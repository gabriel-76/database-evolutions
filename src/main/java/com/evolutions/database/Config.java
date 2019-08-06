package com.evolutions.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Config {

    @Value("${play.evolutions.mode:dev}")
    private String mode;

    @Value("${play.evolutions.location:evolutions}")
    private String path;

    public String getMode() {
        return mode.toLowerCase();
    }

    public String getPath() {
        return path;
    }
}
