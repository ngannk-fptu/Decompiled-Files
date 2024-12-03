/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.security.auth.trustedapps.Application
 *  com.atlassian.security.auth.trustedapps.ApplicationRetriever$RetrievalException
 *  com.atlassian.security.auth.trustedapps.RequestConditions
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.google.common.collect.ImmutableMap
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.applinks.trusted.auth.Action;
import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrustConfigurator {
    public static final long DEFAULT_CERTIFICATE_TIMEOUT = 10000L;
    protected final TrustedApplicationsConfigurationManager trustedAppsManager;
    protected final AuthenticationConfigurationManager configurationManager;

    @Autowired
    public TrustConfigurator(AuthenticationConfigurationManager configurationManager, TrustedApplicationsConfigurationManager trustedAppsManager) {
        this.configurationManager = configurationManager;
        this.trustedAppsManager = trustedAppsManager;
    }

    public void updateInboundTrust(ApplicationLink appLink, RequestConditions requestConditions) throws ConfigurationException {
        Application application = this.getApplicationCertificate(appLink);
        this.trustedAppsManager.addTrustedApplication(application, requestConditions);
        appLink.putProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID, (Object)application.getID());
    }

    private Application getApplicationCertificate(ApplicationLink appLink) throws ConfigurationException {
        Objects.requireNonNull(appLink);
        try {
            return this.trustedAppsManager.getApplicationCertificate(appLink.getRpcUrl().toString());
        }
        catch (ApplicationRetriever.RetrievalException re) {
            throw new ConfigurationException("Unable to retrieve the application's certificate: " + re.getMessage(), re);
        }
    }

    public void issueInboundTrust(ApplicationLink appLink) throws ConfigurationException {
        Application application = this.getApplicationCertificate(appLink);
        Objects.requireNonNull(appLink);
        if (StreamSupport.stream(this.trustedAppsManager.getTrustedApplications().spliterator(), false).noneMatch(input -> input.getID().equals(application.getID()))) {
            this.trustedAppsManager.addTrustedApplication(application, RequestConditions.builder().setCertificateTimeout(10000L).build());
        }
        appLink.putProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID, (Object)application.getID());
    }

    public boolean inboundTrustEnabled(ApplicationLink applicationLink) {
        return applicationLink.getProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID) != null;
    }

    public void revokeInboundTrust(ApplicationLink appLink) {
        Object value = appLink.getProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID);
        if (value != null) {
            this.trustedAppsManager.deleteApplication(value.toString());
        }
        appLink.removeProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID);
    }

    public void configureOutboundTrust(ApplicationLink link, Action action) {
        if (Action.ENABLE == Objects.requireNonNull(action)) {
            this.issueOutboundTrust(link);
        } else {
            this.revokeOutboundTrust(link);
        }
    }

    public void issueOutboundTrust(ApplicationLink link) {
        Objects.requireNonNull(link);
        this.configurationManager.registerProvider(link.getId(), TrustedAppsAuthenticationProvider.class, (Map)ImmutableMap.of());
    }

    public void revokeOutboundTrust(ApplicationLink link) {
        Objects.requireNonNull(link);
        this.configurationManager.unregisterProvider(link.getId(), TrustedAppsAuthenticationProvider.class);
    }

    public static class ConfigurationException
    extends Exception {
        public ConfigurationException(String message) {
            super(message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

