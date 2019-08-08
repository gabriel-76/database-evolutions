package com.evolutions.database.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Database Evolutions.
 *
 * @author Gabriel Oliveira
 */
@ConfigurationProperties(prefix = "play.evolutions")
public class DatabaseEvolutionsProperties {

    private boolean enable = true;

    private String schema = "";

    private boolean autocommit = true;

    private boolean useLocks = false;

    private boolean autoApply = false;

    private boolean autoApplyDowns = false;

    private boolean skipApplyDownsOnly = false;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isAutocommit() {
        return autocommit;
    }

    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    public boolean isUseLocks() {
        return useLocks;
    }

    public void setUseLocks(boolean useLocks) {
        this.useLocks = useLocks;
    }

    public boolean isAutoApply() {
        return autoApply;
    }

    public void setAutoApply(boolean autoApply) {
        this.autoApply = autoApply;
    }

    public boolean isAutoApplyDowns() {
        return autoApplyDowns;
    }

    public void setAutoApplyDowns(boolean autoApplyDowns) {
        this.autoApplyDowns = autoApplyDowns;
    }

    public boolean isSkipApplyDownsOnly() {
        return skipApplyDownsOnly;
    }

    public void setSkipApplyDownsOnly(boolean skipApplyDownsOnly) {
        this.skipApplyDownsOnly = skipApplyDownsOnly;
    }
}
