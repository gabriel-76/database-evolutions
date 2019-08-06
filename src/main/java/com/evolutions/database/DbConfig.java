package com.evolutions.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DbConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public String getUrl() {
        return url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Value("${play.evolutions.enabled}")
    private Boolean enable;

    @Value("${play.evolutions.schema}")
    private String schema;

    @Value("${play.evolutions.autocommit}")
    private Boolean autocommit;

    @Value("${play.evolutions.useLocks}")
    private Boolean useLocks;

    @Value("${play.evolutions.autoApply}")
    private Boolean autoApply;

    @Value("${play.evolutions.autoApplyDowns}")
    private Boolean autoApplyDowns;

    @Value("${play.evolutions.skipApplyDownsOnly}")
    private Boolean skipApplyDownsOnly;

    public Boolean getEnable() {
        return enable;
    }

    public String getSchema() {
        return schema;
    }

    public Boolean getAutocommit() {
        return autocommit;
    }

    public Boolean getUseLocks() {
        return useLocks;
    }

    public Boolean getAutoApply() {
        return autoApply;
    }

    public Boolean getAutoApplyDowns() {
        return autoApplyDowns;
    }

    public Boolean getSkipApplyDownsOnly() {
        return skipApplyDownsOnly;
    }
}
