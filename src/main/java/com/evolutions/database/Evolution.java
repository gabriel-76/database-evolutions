package com.evolutions.database;

import java.util.Objects;

/**
 * An evolution.
 */
public final class Evolution {

    private final int revision;
    private final String sqlUp;
    private final String sqlDown;

    /**
     * Create the evolution.
     *
     * @param revision The revision of the evolution to create.
     * @param sqlUp    The SQL script for bringing the evolution up.
     * @param sqlDown  The SQL script for tearing the evolution down.
     */
    public Evolution(int revision, String sqlUp, String sqlDown) {
        this.revision = revision;
        this.sqlUp = sqlUp;
        this.sqlDown = sqlDown;
    }

    /**
     * Get the revision of the evolution.
     *
     * @return The revision of the evolution to create.
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Get the SQL script for bringing the evolution up.
     *
     * @return the sql script.
     */
    public String getSqlUp() {
        return sqlUp;
    }

    /**
     * Get the SQL script for tearing the evolution down.
     *
     * @return the sql script.
     */
    public String getSqlDown() {
        return sqlDown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Evolution evolution = (Evolution) o;

        if (revision != evolution.revision) return false;

        if (!Objects.equals(sqlDown, evolution.sqlDown)) return false;

        return Objects.equals(sqlUp, evolution.sqlUp);
    }

    @Override
    public int hashCode() {
        int result = revision;
        result = 31 * result + (sqlUp != null ? sqlUp.hashCode() : 0);
        result = 31 * result + (sqlDown != null ? sqlDown.hashCode() : 0);
        return result;
    }
}
