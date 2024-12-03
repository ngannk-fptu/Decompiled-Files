/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.upm.pac;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.core.pac.AbstractMarketplaceClientManager;
import com.atlassian.upm.core.pac.ClientContextFactory;
import org.osgi.framework.BundleContext;

public class UpmMarketplaceClientManager
extends AbstractMarketplaceClientManager {
    public static final String USER_AGENT_PREFIX = "Atlassian-UniversalPluginManager/";
    private final String upmVersion;

    public UpmMarketplaceClientManager(ApplicationProperties applicationProperties, ClientContextFactory clientContextFactory, BundleContext bundleContext, EventPublisher eventPublisher, UpmInformation upm) {
        super(applicationProperties, clientContextFactory, bundleContext, eventPublisher);
        this.upmVersion = upm.getVersionString();
    }

    @Override
    public String getUserAgentPrefix() {
        return USER_AGENT_PREFIX;
    }

    @Override
    public String getVersion() {
        return this.upmVersion;
    }
}

