/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;
import com.atlassian.confluence.content.render.xhtml.migration.macro.MigrationRequiredListener;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemigrationLifecycleItem
implements LifecycleItem {
    protected static final String STARTUP_REMIGRATION_DISABLED = "confluence.startup.remigration.disable";
    private static final Logger log = LoggerFactory.getLogger(RemigrationLifecycleItem.class);
    private MigrationRequiredListener migrationRequiredListener;
    private MacroMigrationService remigrationService;
    private EventPublisher eventPublisher;

    public void startup(LifecycleContext context) throws Exception {
        this.eventPublisher.register((Object)this.migrationRequiredListener);
        if (!Boolean.getBoolean(STARTUP_REMIGRATION_DISABLED) && this.remigrationService.isMigrationRequired()) {
            log.info("Remigration is required");
            this.remigrationService.migrateAll();
        }
    }

    public void shutdown(LifecycleContext context) throws Exception {
    }

    public void setMigrationRequiredListener(MigrationRequiredListener listener) {
        this.migrationRequiredListener = listener;
    }

    public void setMacroMigrationService(MacroMigrationService migrationService) {
        this.remigrationService = migrationService;
    }

    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }
}

