/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.config.db.HibernateConfig;

public class DbEscapeHelper {
    private static final String ESCAPE_CHAR_LEFT = System.getProperty("confluence.db.escape.identifier.char.left");
    private static final String ESCAPE_CHAR_RIGHT = System.getProperty("confluence.db.escape.identifier.char.right");
    private final HibernateConfig hibernateConfig;

    public DbEscapeHelper(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    public String escapeIdentifier(String identifier) {
        return this.escapeCharLeft() + identifier + this.escapeCharRight();
    }

    private String escapeCharLeft() {
        if (ESCAPE_CHAR_LEFT != null) {
            return ESCAPE_CHAR_LEFT;
        }
        if (this.hibernateConfig.isSqlServer()) {
            return "[";
        }
        if (this.hibernateConfig.isPostgreSql()) {
            return "\"";
        }
        if (this.hibernateConfig.isMySql()) {
            return "`";
        }
        if (this.hibernateConfig.isOracle()) {
            return "\"";
        }
        if (this.hibernateConfig.isH2()) {
            return "\"";
        }
        return "\"";
    }

    private String escapeCharRight() {
        if (ESCAPE_CHAR_RIGHT != null) {
            return ESCAPE_CHAR_RIGHT;
        }
        if (this.hibernateConfig.isSqlServer()) {
            return "]";
        }
        if (this.hibernateConfig.isPostgreSql()) {
            return "\"";
        }
        if (this.hibernateConfig.isMySql()) {
            return "`";
        }
        if (this.hibernateConfig.isOracle()) {
            return "\"";
        }
        if (this.hibernateConfig.isH2()) {
            return "\"";
        }
        return "\"";
    }
}

