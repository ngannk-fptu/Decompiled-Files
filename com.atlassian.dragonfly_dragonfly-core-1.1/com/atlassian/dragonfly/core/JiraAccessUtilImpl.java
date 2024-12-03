/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.dragonfly.api.JiraAccessUtil
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dragonfly.core;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.dragonfly.api.JiraAccessUtil;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraAccessUtilImpl
implements JiraAccessUtil {
    private static final Logger LOG = LoggerFactory.getLogger(JiraAccessUtilImpl.class);
    private final ManifestRetriever manifestRetriever;
    private final TypeAccessor typeAccessor;
    private final MutatingApplicationLinkService applicationLinkService;

    public JiraAccessUtilImpl(ManifestRetriever manifestRetriever, TypeAccessor typeAccessor, MutatingApplicationLinkService applicationLinkService) {
        this.manifestRetriever = manifestRetriever;
        this.typeAccessor = typeAccessor;
        this.applicationLinkService = applicationLinkService;
    }

    public boolean checkTargetIsSupportedJira(URI remoteUrl) {
        Manifest manifest;
        try {
            manifest = this.manifestRetriever.getManifest(remoteUrl);
        }
        catch (ManifestNotFoundException e) {
            LOG.info("Failed to retrieve manifest from " + remoteUrl, (Throwable)e);
            return false;
        }
        if (!manifest.getTypeId().equals((Object)TypeId.getTypeId((ApplicationType)this.typeAccessor.getApplicationType(JiraApplicationType.class)))) {
            LOG.info(remoteUrl + " is not Jira");
            return false;
        }
        return true;
    }

    public boolean checkAdminCredential(URI remoteUrl, String username, String password) {
        try {
            return this.applicationLinkService.isAdminUserInRemoteApplication(remoteUrl, username, password);
        }
        catch (ResponseException e) {
            return false;
        }
    }
}

