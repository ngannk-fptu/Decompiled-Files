/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.logging.interfaces;

import lombok.Generated;

public class MigrationLogConstants {
    public static final String PATTERN_LAYOUT_WITH_CONTEXT_CLASS = "com.atlassian.confluence.util.PatternLayoutWithContext";
    public static final String MIGRATION_AGENT_PACKAGE = "com.atlassian.migration.agent";
    public static final String MIGRATION_LOG_APPENDER_NAME = "migrationslog";
    public static final String MIGRATION_LOG_FILE_NAME = "atlassian-confluence-migrations.log";
    public static final String MIGRATION_LOG_FILE_SIZE = "20480KB";
    public static final int MAX_MIGRATION_LOG_FILES = 10;
    public static final String MIGRATION_LOG_PATTERN = "%d %p [%t] [%c{4}] %M %m%n";
    public static final String LOG_DIRECTORY = "logs";
    public static final String ROLLING_FILE_APPENDER_CLASS = "org.apache.log4j.RollingFileAppender";

    @Generated
    private MigrationLogConstants() {
    }
}

