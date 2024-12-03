/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.accessors;

import com.atlassian.troubleshooting.healthcheck.model.DbType;

public interface DbPlatform {
    public DbType getDbType();

    public String getDbVersion();

    public boolean versionEquals(String var1);
}

