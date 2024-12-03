/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;

public class RemigrationAdminTaskCriteria
implements AdminConfigurationCriteria {
    private final MacroMigrationService remigrationService;

    public RemigrationAdminTaskCriteria(MacroMigrationService remigrationService) {
        this.remigrationService = remigrationService;
    }

    @Override
    public boolean isMet() {
        return !this.remigrationService.isMigrationRequired();
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getValue() {
        if (this.isMet()) {
            return "Not required.";
        }
        return "Required.";
    }

    @Override
    public boolean hasLiveValue() {
        return true;
    }

    @Override
    public boolean getIgnored() {
        return this.remigrationService.isAdminTaskIgnored();
    }

    @Override
    public void setIgnored(boolean ignored) {
        this.remigrationService.setAdminTaskIgnored(ignored);
    }
}

