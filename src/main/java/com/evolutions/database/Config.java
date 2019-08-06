package com.evolutions.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Config {

    @Value("${play.evolutions.enabled}")
    private Boolean enable;

    @Value("${play.evolutions.schema}")
    private String schema;

    @Value("${play.evolutions.autocommit}")
    private Boolean autocommit;

    @Value("${play.evolutions.useLocks}")
    private Boolean autocouseLocks;

    @Value("${play.evolutions.autoApply}")
    private Boolean autoApply;

    @Value("${play.evolutions.autoApplyDowns}")
    private Boolean autoApplyDowns;

    @Value("${play.evolutions.skipApplyDownsOnly}")
    private Boolean skipApplyDownsOnly;

    @Value("${play.evolutions.mode:dev}")
    private String mode;

    @Value("${play.evolutions.location:evolutions}")
    private String path;

    public Boolean getEnable() {
        return enable;
    }

    public String getSchema() {
        return schema;
    }

    public Boolean getAutocommit() {
        return autocommit;
    }

    public Boolean getAutocouseLocks() {
        return autocouseLocks;
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

    public String getMode() {
        return mode.toLowerCase();
    }

    public String getPath() {
        return path;
    }
}
