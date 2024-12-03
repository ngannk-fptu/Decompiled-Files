/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.spring.container.ContainerContextLoadedEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.AbstractBootstrapApplicationListener;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.spring.container.ContainerContextLoadedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootstrapApplicationStartupListener
extends AbstractBootstrapApplicationListener<ContainerContextLoadedEvent> {
    private static final Logger log = LoggerFactory.getLogger(BootstrapApplicationStartupListener.class);

    public void onApplicationEvent(ContainerContextLoadedEvent event) {
        this.checkConfigurationOnStartup();
    }

    private void checkConfigurationOnStartup() {
        try {
            BootstrapConfigurer.getBootstrapConfigurer().checkConfigurationOnStartup();
        }
        catch (BootstrapException e) {
            String msg = e.getLocalizedMessage();
            log.error(msg);
            JohnsonUtils.raiseJohnsonEventRequiringTranslation(JohnsonEventType.STARTUP, "startup.config.check.failed", msg, JohnsonEventLevel.FATAL);
        }
    }
}

