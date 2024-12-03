/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate$Type
 *  com.atlassian.security.auth.trustedapps.TrustedApplication
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrustedAppsOrphanedTrustDetector
implements InternalOrphanedTrustDetector {
    private final ApplicationLinkService applicationLinkService;
    private final TrustedApplicationsConfigurationManager trustedApplicationsConfigurationManager;
    private final TrustConfigurator trustConfigurator;
    private static final Logger log = LoggerFactory.getLogger(TrustedAppsOrphanedTrustDetector.class);

    @Autowired
    public TrustedAppsOrphanedTrustDetector(ApplicationLinkService applicationLinkService, TrustedApplicationsConfigurationManager trustedApplicationsConfigurationManager, TrustConfigurator trustConfigurator) {
        this.applicationLinkService = applicationLinkService;
        this.trustedApplicationsConfigurationManager = trustedApplicationsConfigurationManager;
        this.trustConfigurator = trustConfigurator;
    }

    public List<OrphanedTrustCertificate> findOrphanedTrustCertificates() {
        ArrayList<OrphanedTrustCertificate> orphanedTrustCertificates = new ArrayList<OrphanedTrustCertificate>();
        HashSet<String> recognisedIds = new HashSet<String>();
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks()) {
            String id = (String)link.getProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID);
            if (id == null) continue;
            recognisedIds.add(id);
        }
        for (TrustedApplication trustedApp : this.trustedApplicationsConfigurationManager.getTrustedApplications()) {
            if (recognisedIds.contains(trustedApp.getID())) continue;
            orphanedTrustCertificates.add(new OrphanedTrustCertificate(trustedApp.getID(), trustedApp.getName(), OrphanedTrustCertificate.Type.TRUSTED_APPS));
        }
        return orphanedTrustCertificates;
    }

    public void deleteTrustCertificate(String id, OrphanedTrustCertificate.Type type) {
        this.checkCertificateType(type);
        this.trustedApplicationsConfigurationManager.deleteApplication(id);
    }

    private void checkCertificateType(OrphanedTrustCertificate.Type type) {
        if (!this.canHandleCertificateType(OrphanedTrustCertificate.Type.TRUSTED_APPS)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public boolean canHandleCertificateType(OrphanedTrustCertificate.Type type) {
        return type == OrphanedTrustCertificate.Type.TRUSTED_APPS;
    }

    public void addOrphanedTrustToApplicationLink(String id, OrphanedTrustCertificate.Type type, ApplicationId applicationId) {
        this.checkCertificateType(type);
        try {
            ApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
            this.trustConfigurator.issueInboundTrust(applicationLink);
            log.debug("Associated Trusted Apps configuration for Application Link id='" + applicationLink.getId() + "' and name='" + applicationLink.getName() + "'");
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException("An application of the type " + e.getType() + " is not installed!", e);
        }
        catch (TrustConfigurator.ConfigurationException e) {
            throw new RuntimeException("Failed to add Trusted Apps configuration for Application Link with id '" + applicationId + '\"', e);
        }
    }
}

