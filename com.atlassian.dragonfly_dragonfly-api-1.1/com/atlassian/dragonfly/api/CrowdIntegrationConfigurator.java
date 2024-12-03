/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dragonfly.api;

import com.atlassian.dragonfly.api.CrowdApplicationEntity;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import java.net.URI;

public interface CrowdIntegrationConfigurator {
    public CrowdApplicationEntity configureCrowdAuthentication(URI var1, String var2, String var3) throws JiraIntegrationConfigurationException;

    public void rollbackCrowdAuthenticationConfiguration();
}

