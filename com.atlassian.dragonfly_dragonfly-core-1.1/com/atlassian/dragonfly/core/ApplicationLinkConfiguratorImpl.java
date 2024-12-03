/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.dragonfly.api.ApplicationLinkConfigurator
 *  com.atlassian.dragonfly.api.JiraIntegrationConfigurationException
 *  com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dragonfly.core;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.dragonfly.api.ApplicationLinkConfigurator;
import com.atlassian.dragonfly.api.JiraIntegrationConfigurationException;
import com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkConfiguratorImpl
implements ApplicationLinkConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLinkConfiguratorImpl.class);
    private final MutatingApplicationLinkService applicationLinkService;
    private final TypeAccessor typeAccessor;
    private final JiraIntegrationSetupHelper jiraIntegrationSetupHelper;

    public ApplicationLinkConfiguratorImpl(MutatingApplicationLinkService applicationLinkService, TypeAccessor typeAccessor, JiraIntegrationSetupHelper jiraIntegrationSetupHelper) {
        this.applicationLinkService = applicationLinkService;
        this.typeAccessor = typeAccessor;
        this.jiraIntegrationSetupHelper = jiraIntegrationSetupHelper;
    }

    public ApplicationLink configureApplicationLinks(URI jiraUrl, URI localUrl, String username, String password) throws JiraIntegrationConfigurationException {
        ApplicationLink applicationLink = null;
        try {
            applicationLink = this.createReciprocatedApplicationLink(jiraUrl, localUrl, username, password);
            this.authenticateApplicationLink(applicationLink, username, password, localUrl);
        }
        catch (JiraIntegrationConfigurationException jice) {
            this.rollbackApplicationLinkConfiguration(applicationLink);
            throw jice;
        }
        return applicationLink;
    }

    private ApplicationLink createReciprocatedApplicationLink(URI remoteRpcUrl, URI localRpcUrl, String username, String password) throws JiraIntegrationConfigurationException {
        ApplicationLink applicationLink;
        try {
            this.applicationLinkService.createReciprocalLink(remoteRpcUrl, localRpcUrl, username, password);
        }
        catch (ReciprocalActionException e) {
            throw new JiraIntegrationConfigurationException(String.format("Failed to create application link from JIRA server at %s to this %s server at %s?. Please read the troubleshooting guide.", remoteRpcUrl, this.jiraIntegrationSetupHelper.getApplicationType().getDisplayName(), localRpcUrl), (Throwable)e);
        }
        JiraApplicationType jiraType = (JiraApplicationType)this.typeAccessor.getApplicationType(JiraApplicationType.class);
        if (jiraType == null) {
            throw new JiraIntegrationConfigurationException("Failed to load the application type: " + JiraApplicationType.class + ". " + "Have you disabled some modules of the Application Links plugin?");
        }
        try {
            ApplicationLinkDetails linkDetails = ApplicationLinkDetails.builder().rpcUrl(remoteRpcUrl).displayUrl(remoteRpcUrl).isPrimary(true).name(ApplicationLinkConfiguratorImpl.generateLinkName(remoteRpcUrl)).build();
            applicationLink = this.applicationLinkService.createApplicationLink((ApplicationType)jiraType, linkDetails);
        }
        catch (ManifestNotFoundException e) {
            throw new JiraIntegrationConfigurationException("Failed to retrieve manifest from the remote JIRA server. Is your JIRA server running and accessible from the server that FishEye is installed on?", (Throwable)e);
        }
        return applicationLink;
    }

    protected void authenticateApplicationLink(ApplicationLink applicationLink, String username, String password, URI localRpcUrl) throws JiraIntegrationConfigurationException {
        AuthenticationScenario authenticationScenario = new AuthenticationScenario(){

            public boolean isCommonUserBase() {
                return true;
            }

            public boolean isTrusted() {
                return true;
            }
        };
        try {
            this.applicationLinkService.configureAuthenticationForApplicationLink(applicationLink, authenticationScenario, username, password);
        }
        catch (AuthenticationConfigurationException e) {
            throw new JiraIntegrationConfigurationException(String.format("Failed to authenticate application link between JIRA server at %s to this %s server at %s?. Please read the troubleshooting guide.", applicationLink.getRpcUrl(), this.jiraIntegrationSetupHelper.getApplicationType().getDisplayName(), localRpcUrl), (Throwable)e);
        }
    }

    public void rollbackApplicationLinkConfiguration(ApplicationLink applicationLink) {
        try {
            if (applicationLink != null) {
                this.applicationLinkService.deleteApplicationLink(applicationLink);
            }
            LOG.info("Rolled back 2-way application link to JIRA.");
        }
        catch (Exception rollbackException) {
            LOG.error("Failed to rollback local UAL/Crowd configuration", (Throwable)rollbackException);
        }
    }

    private static String generateLinkName(URI remoteRpcUrl) {
        String name = "JIRA";
        if (remoteRpcUrl.getHost() != null) {
            name = remoteRpcUrl.getHost() + " " + name;
        }
        return name;
    }
}

