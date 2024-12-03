/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.app.PluginManager;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamCalendarHelper {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TeamCalendarHelper.class);
    private final PluginManager pluginManager;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final JdbcConfluenceStore jdbcConfluenceStore;

    public TeamCalendarHelper(PluginManager pluginManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager, JdbcConfluenceStore jdbcConfluenceStore) {
        this.pluginManager = pluginManager;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.jdbcConfluenceStore = jdbcConfluenceStore;
    }

    public boolean includeTeamCalendar() {
        boolean isTCTableExists;
        try {
            isTCTableExists = this.jdbcConfluenceStore.checkIfTableExists("AO_950DC3_TC_SUBCALS_IN_SPACE");
        }
        catch (Exception e) {
            isTCTableExists = false;
        }
        return !this.migrationDarkFeaturesManager.isTeamCalendarsMigrationDisabled() && this.pluginManager.isPluginInstalled("com.atlassian.confluence.extra.team-calendars") != false && isTCTableExists;
    }
}

