/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.server;

import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.MutableApplicationStatusService;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.base.Preconditions;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultApplicationStatusService
implements MutableApplicationStatusService {
    private static final Logger log = LoggerFactory.getLogger(DefaultApplicationStatusService.class);
    private volatile ApplicationState state = !GeneralUtil.isSetupComplete() ? ApplicationState.FIRST_RUN : ApplicationState.STARTING;
    private volatile boolean servletsLoaded = false;
    private volatile boolean applicationStarted = false;

    @Override
    public @NonNull ApplicationState getState() {
        ApplicationState current = this.state;
        if (current == ApplicationState.STOPPING) {
            return current;
        }
        return DefaultApplicationStatusService.isError() ? ApplicationState.ERROR : current;
    }

    public static boolean isError() {
        return JohnsonUtils.findHighestEventLevel().filter(highestLevel -> highestLevel.isAtLeast(JohnsonEventLevel.ERROR)).isPresent();
    }

    @Override
    public void setState(@NonNull ApplicationState state) {
        Preconditions.checkNotNull((Object)((Object)state));
        this.state = state;
    }

    @Override
    public void notifyServletsLoaded() {
        log.info("Servlets loaded.");
        this.servletsLoaded = true;
        this.refreshState();
    }

    @Override
    public void notifyApplicationStarted() {
        log.info("Application started.");
        this.applicationStarted = true;
        this.refreshState();
    }

    private void refreshState() {
        if ((this.state == ApplicationState.STARTING || this.state == ApplicationState.FIRST_RUN) && this.applicationStarted && this.servletsLoaded) {
            log.info("Confluence ApplicationStatus is now RUNNING.");
            this.state = ApplicationState.RUNNING;
        }
    }

    @PreDestroy
    public void shutDown() {
        this.state = ApplicationState.STOPPING;
    }
}

