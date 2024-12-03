/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 */
package com.atlassian.dragonfly.api;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import java.net.URI;

public interface ApplicationLinkConfigurator {
    public ApplicationLink configureApplicationLinks(URI var1, URI var2, String var3, String var4) throws JiraIntegrationConfigurationException;

    public void rollbackApplicationLinkConfiguration(ApplicationLink var1);
}

