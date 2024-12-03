/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.util.ChainingClassLoader
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.ObjectUtils
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.core.manifest;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.core.rest.ManifestResource;
import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.rest.model.ManifestEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.util.Holder;
import com.atlassian.applinks.internal.common.event.ManifestDownloadFailedEvent;
import com.atlassian.applinks.internal.common.event.ManifestDownloadedEvent;
import com.atlassian.applinks.internal.common.net.ResponseContentException;
import com.atlassian.applinks.internal.common.net.ResponsePreconditions;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.util.ChainingClassLoader;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AppLinksManifestDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(AppLinksManifestDownloader.class);
    private static final String CACHE_KEY = "ManifestRequestCache";
    private static final int CONNECTION_TIMEOUT = 10000;
    private final RequestFactory requestFactory;
    private final EventPublisher eventPublisher;
    private final TypeAccessor typeAccessor;
    private final RestUrlBuilder restUrlBuilder;

    @Autowired
    public AppLinksManifestDownloader(RequestFactory requestFactory, EventPublisher eventPublisher, TypeAccessor typeAccessor, RestUrlBuilder restUrlBuilder) {
        this.requestFactory = requestFactory;
        this.eventPublisher = eventPublisher;
        this.typeAccessor = typeAccessor;
        this.restUrlBuilder = restUrlBuilder;
    }

    @Nonnull
    public Manifest download(URI url) throws ManifestNotFoundException {
        return this.downloadInternal(url, true);
    }

    @Nonnull
    @Internal
    public Manifest downloadNoEvent(URI url) throws ManifestNotFoundException {
        return this.downloadInternal(url, false);
    }

    private Manifest downloadInternal(URI url, boolean raiseEvents) throws ManifestNotFoundException {
        LoadingCache<URI, DownloadResult> resultMap = this.getDownloadCache(raiseEvents);
        return resultMap != null ? ((DownloadResult)resultMap.getUnchecked((Object)url)).get() : this.doDownload(url, raiseEvents);
    }

    private LoadingCache<URI, DownloadResult> getDownloadCache(final boolean raiseEvents) {
        HttpServletRequest request = CurrentContext.getHttpServletRequest();
        if (request != null) {
            LoadingCache cache = (LoadingCache)request.getAttribute(CACHE_KEY);
            if (cache == null && (cache = (LoadingCache)request.getAttribute(CACHE_KEY)) == null) {
                cache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<URI, DownloadResult>(){

                    public DownloadResult load(final @Nonnull URI uri) throws Exception {
                        return new DownloadResult(){
                            Manifest manifest = null;
                            ManifestNotFoundException exception = null;
                            {
                                try {
                                    this.manifest = AppLinksManifestDownloader.this.doDownload(uri, raiseEvents);
                                }
                                catch (ManifestNotFoundException e) {
                                    this.exception = e;
                                }
                            }

                            @Override
                            public Manifest get() throws ManifestNotFoundException {
                                if (this.manifest == null) {
                                    LOG.debug("Throwing cached ManifestNotFoundException for: " + uri.toString());
                                    throw this.exception;
                                }
                                LOG.debug("Returning cached manifest for: " + uri.toString());
                                return this.manifest;
                            }
                        };
                    }
                });
                request.setAttribute(CACHE_KEY, (Object)cache);
            }
            return cache;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Manifest doDownload(URI url, boolean raiseEvent) throws ManifestNotFoundException {
        final Holder manifestHolder = new Holder();
        final Holder<Throwable> exception = new Holder<Throwable>();
        ClassLoader currentContextClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader[] classLoaders = (ClassLoader[])Stream.of(ManifestEntity.class.getClassLoader(), ClassLoaderUtils.class.getClassLoader(), ClassLoader.getSystemClassLoader()).filter(Objects::nonNull).toArray(ClassLoader[]::new);
        ChainingClassLoader chainingClassLoader = new ChainingClassLoader(classLoaders);
        try {
            Thread.currentThread().setContextClassLoader((ClassLoader)chainingClassLoader);
            this.requestFactory.createRequest(Request.MethodType.GET, this.appLinksManifestUrl(url)).setConnectionTimeout(10000).setSoTimeout(10000).setFollowRedirects(false).execute((ResponseHandler)new ResponseHandler<Response>(){

                public void handle(Response response) throws ResponseException {
                    if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
                        String location = response.getHeader("Location");
                        if (location == null) {
                            throw new ResponseException("manifest not found");
                        }
                        LOG.info("Manifest request got redirected to '" + location + "'.");
                        exception.set(new ManifestGotRedirectedException("manifest got redirected", location));
                    } else {
                        ResponsePreconditions.checkStatusOk(response);
                        try {
                            manifestHolder.set(AppLinksManifestDownloader.this.asManifest((ManifestEntity)response.getEntity(ManifestEntity.class)));
                        }
                        catch (Exception ex) {
                            exception.set(new ResponseContentException(response, ex));
                        }
                    }
                }
            });
        }
        catch (ResponseException re) {
            exception.set((Throwable)ObjectUtils.defaultIfNull((Object)re.getCause(), (Object)((Object)re)));
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentContextClassloader);
        }
        if (manifestHolder.get() == null) {
            if (raiseEvent) {
                this.eventPublisher.publish((Object)new ManifestDownloadFailedEvent(url, (Throwable)exception.get()));
            }
            if (exception.get() == null) {
                throw new ManifestNotFoundException(url.toString());
            }
            throw new ManifestNotFoundException(url.toString(), (Throwable)exception.get());
        }
        if (raiseEvent) {
            this.eventPublisher.publish((Object)new ManifestDownloadedEvent((Manifest)manifestHolder.get()));
        }
        return (Manifest)manifestHolder.get();
    }

    @VisibleForTesting
    protected String appLinksManifestUrl(URI baseUri) {
        return ((ManifestResource)this.restUrlBuilder.getUrlFor(RestUtil.getBaseRestUri(baseUri), ManifestResource.class)).getManifest().toString();
    }

    private Manifest asManifest(final ManifestEntity manifest) {
        return new Manifest(){

            public ApplicationId getId() {
                return manifest.getId();
            }

            public String getName() {
                return manifest.getName();
            }

            public TypeId getTypeId() {
                return manifest.getTypeId();
            }

            public Long getBuildNumber() {
                return manifest.getBuildNumber();
            }

            public String getVersion() {
                return manifest.getVersion();
            }

            public URI getUrl() {
                return manifest.getUrl();
            }

            public URI getIconUrl() {
                return manifest.getIconUrl();
            }

            public URI getIconUri() {
                return manifest.getIconUri();
            }

            public Version getAppLinksVersion() {
                return manifest.getApplinksVersion();
            }

            public Boolean hasPublicSignup() {
                return manifest.hasPublicSignup();
            }

            public Set<Class<? extends AuthenticationProvider>> getInboundAuthenticationTypes() {
                return this.loadTypes(manifest.getInboundAuthenticationTypes());
            }

            public Set<Class<? extends AuthenticationProvider>> getOutboundAuthenticationTypes() {
                return this.loadTypes(manifest.getOutboundAuthenticationTypes());
            }

            private Set<Class<? extends AuthenticationProvider>> loadTypes(Set<String> classNames) {
                HashSet<Class<? extends AuthenticationProvider>> types = new HashSet<Class<? extends AuthenticationProvider>>();
                if (classNames == null) {
                    return types;
                }
                for (String name : classNames) {
                    Class c = AppLinksManifestDownloader.this.typeAccessor.getAuthenticationProviderClass(name);
                    if (c != null) {
                        types.add(c);
                        continue;
                    }
                    LOG.info(String.format("Authenticator %s specified by remote application %s is not installed locally, and will not be used.", name, this.getId()));
                }
                return types;
            }
        };
    }

    public static class ManifestGotRedirectedException
    extends Exception {
        private String newLocation;

        public ManifestGotRedirectedException(String message, String newLocation) {
            super(message);
            this.newLocation = newLocation;
        }

        public String getNewLocation() {
            return this.newLocation;
        }

        public String newLocationBaseUrl() {
            if (this.newLocation.indexOf("rest/applinks/") > 0) {
                return this.newLocation.substring(0, this.newLocation.indexOf("rest/applinks/"));
            }
            return this.newLocation;
        }
    }

    private static interface DownloadResult {
        public Manifest get() throws ManifestNotFoundException;
    }
}

