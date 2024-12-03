/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrappedContextLoaderListener
 *  com.atlassian.util.profiling.Ticker
 *  com.mchange.v2.resourcepool.CannotAcquireResourceException
 *  javax.servlet.ServletContextEvent
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.BeanCreationException
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.bootstrap.BootstrappedContextLoaderListener;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.util.profiling.Ticker;
import com.mchange.v2.resourcepool.CannotAcquireResourceException;
import javax.servlet.ServletContextEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;

public class ConfluenceBootstrappedContextLoaderListener
extends BootstrappedContextLoaderListener {
    private static final Logger log = LoggerFactory.getLogger(BootstrappedContextLoaderListener.class);

    public boolean canInitialiseContainer() {
        return super.canInitialiseContainer() && GeneralUtil.isSetupComplete();
    }

    public void contextInitialized(ServletContextEvent event) {
        try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.spring-context.main");){
            super.contextInitialized(event);
        }
        catch (BeanCreationException e) {
            log.error("Failed to initialize the main Confluence context", (Throwable)e);
            if (ExceptionUtils.getRootCause((Throwable)e) instanceof CannotAcquireResourceException) {
                JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.STARTUP, "startup.resource.acquire.failed", ExceptionUtils.getRootCauseMessage((Throwable)e), JohnsonEventLevel.FATAL);
                return;
            }
            JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.STARTUP, "startup.resource.context.init.failed", ExceptionUtils.getRootCauseMessage((Throwable)e), JohnsonEventLevel.FATAL);
        }
    }
}

