/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.h2.DatabaseCreatingClientConfig
 *  com.atlassian.h2.DatabaseCreatingServerLifecycle
 *  com.atlassian.h2.H2QueryFailedException
 *  com.atlassian.h2.OpenServerConfig
 *  com.atlassian.h2.ServerLifecycle
 *  com.atlassian.h2.ServerView
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.impl.hibernate.EmbeddedDatabaseManager;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.h2.DatabaseCreatingClientConfig;
import com.atlassian.h2.DatabaseCreatingServerLifecycle;
import com.atlassian.h2.H2QueryFailedException;
import com.atlassian.h2.OpenServerConfig;
import com.atlassian.h2.ServerLifecycle;
import com.atlassian.h2.ServerView;
import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class H2DatabaseManager
implements EmbeddedDatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(H2DatabaseManager.class);
    private static final String DATABASE_NAME = "h2db";
    private static final boolean IS_MVCC = false;
    private final ServerLifecycle serverLifecycle;
    private final Function<ServerView, String> clientConfig;

    public H2DatabaseManager(Supplier<File> databaseHomeDirectory) {
        this.serverLifecycle = new DatabaseCreatingServerLifecycle((Supplier)new OpenServerConfig(), databaseHomeDirectory, DATABASE_NAME);
        this.clientConfig = new DatabaseCreatingClientConfig(databaseHomeDirectory, DATABASE_NAME, false);
    }

    @PreDestroy
    void shutdown() {
        this.serverLifecycle.stop();
    }

    @Override
    public String ensureDatabaseStarted() {
        String clientUri;
        boolean wasRunning = this.serverLifecycle.view().isRunning();
        try {
            clientUri = this.clientConfig.apply(this.serverLifecycle.start());
        }
        catch (H2QueryFailedException e) {
            JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.BOOTSTRAP, "johnson.message.h2.startup.error", ExceptionUtils.getRootCauseMessage((Throwable)e), JohnsonEventLevel.FATAL);
            throw e;
        }
        if (!wasRunning) {
            log.info("STARTED H2 database server at URL {}", (Object)clientUri);
        }
        return clientUri;
    }
}

