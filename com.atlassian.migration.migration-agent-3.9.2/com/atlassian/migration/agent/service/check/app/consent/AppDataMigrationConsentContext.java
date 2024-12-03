/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check.app.consent;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.List;

public class AppDataMigrationConsentContext
implements CheckContext {
    public final List<String> appKeys;

    public AppDataMigrationConsentContext(List<String> appKeys) {
        this.appKeys = appKeys;
    }
}

