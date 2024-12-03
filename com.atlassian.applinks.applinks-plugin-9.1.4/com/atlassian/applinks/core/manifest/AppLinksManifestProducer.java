/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.manifest.ApplicationStatus
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestProducer
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.manifest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestProducer;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;
import java.util.Set;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppLinksManifestProducer
implements ManifestProducer {
    private static final int CONNECTION_TIMEOUT = 10000;
    private final AppLinksManifestDownloader downloader;
    private final RequestFactory<Request<Request<?, Response>, Response>> requestFactory;
    protected final WebResourceManager webResourceManager;
    protected final AppLinkPluginUtil appLinkPluginUtil;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected AppLinksManifestProducer(RequestFactory<Request<Request<?, Response>, Response>> requestFactory, AppLinksManifestDownloader downloader, WebResourceManager webResourceManager, AppLinkPluginUtil AppLinkPluginUtil2) {
        this.downloader = downloader;
        this.requestFactory = requestFactory;
        this.webResourceManager = webResourceManager;
        this.appLinkPluginUtil = AppLinkPluginUtil2;
    }

    public Manifest getManifest(URI url) throws ManifestNotFoundException {
        try {
            Manifest downloadedManifest = this.downloader.download(url);
            if (downloadedManifest != null && this.getApplicationTypeId().equals((Object)downloadedManifest.getTypeId())) {
                return downloadedManifest;
            }
        }
        catch (ManifestNotFoundException e) {
            this.LOG.debug("Failed to obtain an AppLinks manifest from the peer. Treating the peer as a non-AppLinks capable host instead.");
        }
        return this.createManifest(url);
    }

    private Manifest createManifest(final URI url) {
        return new Manifest(){

            public ApplicationId getId() {
                return ApplicationIdUtil.generate((URI)url);
            }

            public String getName() {
                return AppLinksManifestProducer.this.getApplicationName();
            }

            public TypeId getTypeId() {
                return AppLinksManifestProducer.this.getApplicationTypeId();
            }

            public String getVersion() {
                return AppLinksManifestProducer.this.getApplicationVersion();
            }

            public Long getBuildNumber() {
                return AppLinksManifestProducer.this.getApplicationBuildNumber();
            }

            public URI getUrl() {
                return URIUtil.copyOf(url);
            }

            public URI getIconUrl() {
                return AppLinksManifestProducer.this.getApplicationIconUrl();
            }

            public URI getIconUri() {
                return AppLinksManifestProducer.this.getApplicationIconUri();
            }

            public Version getAppLinksVersion() {
                return AppLinksManifestProducer.this.getApplicationAppLinksVersion();
            }

            public Boolean hasPublicSignup() {
                return null;
            }

            public Set<Class<? extends AuthenticationProvider>> getInboundAuthenticationTypes() {
                return AppLinksManifestProducer.this.getSupportedInboundAuthenticationTypes();
            }

            public Set<Class<? extends AuthenticationProvider>> getOutboundAuthenticationTypes() {
                return AppLinksManifestProducer.this.getSupportedOutboundAuthenticationTypes();
            }
        };
    }

    protected Long getApplicationBuildNumber() {
        return 0L;
    }

    protected String getApplicationVersion() {
        return null;
    }

    protected Version getApplicationAppLinksVersion() {
        return null;
    }

    protected URI getApplicationIconUrl() {
        return null;
    }

    protected URI getApplicationIconUri() {
        return null;
    }

    protected abstract TypeId getApplicationTypeId();

    protected abstract String getApplicationName();

    protected abstract Set<Class<? extends AuthenticationProvider>> getSupportedInboundAuthenticationTypes();

    protected abstract Set<Class<? extends AuthenticationProvider>> getSupportedOutboundAuthenticationTypes();

    public ApplicationStatus getStatus(URI url) {
        try {
            this.LOG.debug("Querying " + url + " for its online status.");
            Request request = this.requestFactory.createRequest(Request.MethodType.GET, url.toString());
            request.setConnectionTimeout(10000).setSoTimeout(10000);
            return (ApplicationStatus)request.executeAndReturn(response -> response.isSuccessful() ? ApplicationStatus.AVAILABLE : ApplicationStatus.UNAVAILABLE);
        }
        catch (ResponseException re) {
            return ApplicationStatus.UNAVAILABLE;
        }
    }
}

