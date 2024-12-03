/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.MigrationPath
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.dto.MigrationPath;

@Deprecated
public enum AppCloudCapability {
    yes,
    no,
    unknown,
    upgrade,
    install_only,
    manual;


    public static AppCloudCapability fromMigrationPath(MigrationPath migrationPath, boolean requiresUpgrade) {
        if (migrationPath == null) {
            return unknown;
        }
        switch (migrationPath) {
            case DISCARDED: {
                return no;
            }
            case AUTOMATED: {
                if (requiresUpgrade) {
                    return upgrade;
                }
                return yes;
            }
            case INSTALL_ONLY: {
                return install_only;
            }
            case MANUAL: {
                return manual;
            }
        }
        return unknown;
    }
}

