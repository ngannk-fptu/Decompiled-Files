/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 */
package com.atlassian.confluence.util.db;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import java.util.Map;
import java.util.Optional;

public class DatabaseUtils {
    private static final Map<String, String> EVALUATION_DIALECTS_TO_NAME = Map.of("com.atlassian.confluence.impl.hibernate.dialect.HSQL2Dialect", "HSQL", "org.hibernate.dialect.H2Dialect", "H2");

    private DatabaseUtils() {
    }

    public static Optional<String> evaluationDatabaseName() {
        return DatabaseUtils.evaluationDatabaseName(DatabaseUtils.getBootstrapManager());
    }

    public static Optional<String> evaluationDatabaseName(BootstrapManager bootstrapManager) {
        String dialect = (String)bootstrapManager.getProperty("hibernate.dialect");
        return Optional.ofNullable(EVALUATION_DIALECTS_TO_NAME.get(dialect));
    }

    private static BootstrapManager getBootstrapManager() {
        return (BootstrapManager)BootstrapUtils.getBootstrapManager();
    }
}

