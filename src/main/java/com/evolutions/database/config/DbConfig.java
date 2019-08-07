package com.evolutions.database.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DbConfig {

    @Value("${play.evolutions.enabled:true}")
    private Boolean enable;

    @Value("${play.evolutions.schema:}")
    private String schema;

    @Value("${play.evolutions.autocommit:true}")
    private Boolean autocommit;

    @Value("${play.evolutions.useLocks:false}")
    private Boolean useLocks;

    @Value("${play.evolutions.autoApply:false}")
    private Boolean autoApply;

    @Value("${play.evolutions.autoApplyDowns:false}")
    private Boolean autoApplyDowns;

    @Value("${play.evolutions.skipApplyDownsOnly:false}")
    private Boolean skipApplyDownsOnly;

    @Value("${spring.datasource.url:}")
    private String url;

    @Value("${spring.datasource.driverClassName:}")
    private String driverClassName;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

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
}
