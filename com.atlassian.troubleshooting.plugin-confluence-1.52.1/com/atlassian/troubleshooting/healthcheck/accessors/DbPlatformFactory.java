/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.accessors;

import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import com.atlassian.troubleshooting.healthcheck.model.DbType;

public interface DbPlatformFactory {
    public DbPlatform create(DbType var1, String var2);
}

