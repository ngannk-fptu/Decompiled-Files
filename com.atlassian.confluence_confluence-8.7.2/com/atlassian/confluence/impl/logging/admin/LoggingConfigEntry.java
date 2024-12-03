/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 */
package com.atlassian.confluence.impl.logging.admin;

import java.io.Serializable;
import org.apache.log4j.Level;

public class LoggingConfigEntry
implements Serializable {
    private static final String ROOT = "root";
    private String clazz;
    private Level level;

    public LoggingConfigEntry() {
    }

    public boolean isRoot() {
        return this.clazz.equals(ROOT);
    }

    public LoggingConfigEntry(String clazz, Level level) {
        this.clazz = clazz;
        this.level = level;
    }

    public LoggingConfigEntry(String clazz, String level) {
        this.clazz = clazz;
        this.level = Level.toLevel((String)level);
    }

    public String getClazz() {
        return this.clazz;
    }

    public String getLevel() {
        return this.level.toString();
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setLevel(String level) {
        this.level = Level.toLevel((String)level);
    }
}

