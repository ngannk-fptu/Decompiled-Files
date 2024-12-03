/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dragonfly.api;

import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import java.net.URI;

public interface JiraIntegrationConfigurator {
    public void integrateWithJira(URI var1, URI var2, String var3, String var4) throws JiraIntegrationConfigurationException;
}

