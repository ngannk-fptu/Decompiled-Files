/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.event.ContextClosedEvent
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.setup.AbstractBootstrapApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class BootstrapApplicationShutdownListener
extends AbstractBootstrapApplicationListener<ContextClosedEvent> {
    public void onApplicationEvent(ContextClosedEvent event) {
        this.cleanupOnShutdown();
    }

    private void cleanupOnShutdown() {
        BootstrapConfigurer.getBootstrapConfigurer().cleanupOnShutdown();
    }
}

