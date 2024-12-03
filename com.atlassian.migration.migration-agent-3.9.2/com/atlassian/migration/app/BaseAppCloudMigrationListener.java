/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AccessScope;
import java.util.Set;

public interface BaseAppCloudMigrationListener {
    public String getCloudAppKey();

    public String getServerAppKey();

    public Set<AccessScope> getDataAccessScopes();
}

