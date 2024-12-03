/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.plugin.webresource.ConfluenceResourceBatchingConfiguration;
import com.atlassian.confluence.plugin.webresource.Counter;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperBatchInvalidatorListener
implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(SuperBatchInvalidatorListener.class);
    private ConfluenceResourceBatchingConfiguration resourceBatchingConfiguration;
    private Counter superbatchResourceCounter;
    private TenantRegistry tenantRegistry;

    public void handleEvent(Event event) {
        if (this.shouldInvalidateSuperBatch(event)) {
            this.superbatchResourceCounter.updateCounter();
        }
    }

    private boolean shouldInvalidateSuperBatch(Event event) {
        if (this.tenantRegistry.isRegistryVacant()) {
            return false;
        }
        if (event instanceof PluginFrameworkStartedEvent) {
            return true;
        }
        PluginEvent pluginEvent = (PluginEvent)event;
        List<String> keys = this.resourceBatchingConfiguration.getSuperBatchModuleCompleteKeys();
        for (String key : keys) {
            if (!key.startsWith(pluginEvent.getPluginKey() + ":")) continue;
            log.debug("Invalidate super batch resource counter by {}", (Object)key);
            return true;
        }
        return false;
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{PluginEvent.class, PluginFrameworkStartedEvent.class};
    }

    public void setResourceBatchingConfiguration(ConfluenceResourceBatchingConfiguration resourceBatchingConfiguration) {
        this.resourceBatchingConfiguration = resourceBatchingConfiguration;
    }

    public void setSuperbatchResourceCounter(Counter superbatchResourceCounter) {
        this.superbatchResourceCounter = superbatchResourceCounter;
    }

    public void setTenantRegistry(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }
}

