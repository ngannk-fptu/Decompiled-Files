/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.dragonfly.api.ApplicationLinkConfigurator
 *  com.atlassian.dragonfly.api.CrowdIntegrationConfigurator
 *  com.atlassian.dragonfly.api.JiraIntegrationConfigurationException
 *  com.atlassian.dragonfly.api.JiraIntegrationConfigurator
 */
package com.atlassian.dragonfly.core;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.dragonfly.api.ApplicationLinkConfigurator;
import com.atlassian.dragonfly.api.CrowdIntegrationConfigurator;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurator;
import java.net.URI;

public class JiraIntegrationConfiguratorImpl
implements JiraIntegrationConfigurator {
    final ApplicationLinkConfigurator applicationLinkConfigurator;
    final CrowdIntegrationConfigurator crowdIntegrationConfigurator;

    public JiraIntegrationConfiguratorImpl(ApplicationLinkConfigurator applicationLinkConfigurator, CrowdIntegrationConfigurator crowdIntegrationConfigurator) {
        this.applicationLinkConfigurator = applicationLinkConfigurator;
        this.crowdIntegrationConfigurator = crowdIntegrationConfigurator;
    }

    public void integrateWithJira(URI remoteJiraUrl, URI localUrl, String username, String password) throws JiraIntegrationConfigurationException {
        ApplicationLink applicationLink = this.applicationLinkConfigurator.configureApplicationLinks(remoteJiraUrl, localUrl, username, password);
        try {
            this.crowdIntegrationConfigurator.configureCrowdAuthentication(remoteJiraUrl, username, password);
        }
        catch (JiraIntegrationConfigurationException jice) {
            this.applicationLinkConfigurator.rollbackApplicationLinkConfiguration(applicationLink);
            throw jice;
        }
    }
}

