/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.application.generic.GenericApplicationType
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.internal.capabilities;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.application.generic.GenericApplicationType;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.application.BuiltinApplinksType;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.capabilities.DefaultRemoteCapabilities;
import com.atlassian.applinks.internal.capabilities.RemoteCapabilitiesError;
import com.atlassian.applinks.internal.common.applink.ApplicationLinks;
import com.atlassian.applinks.internal.common.cache.ApplinksRequestCache;
import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.event.ManifestDownloadFailedEvent;
import com.atlassian.applinks.internal.common.event.ManifestDownloadedEvent;
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.atlassian.applinks.internal.common.exception.InvalidValueException;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.lang.ApplinksEnums;
import com.atlassian.applinks.internal.common.lang.ApplinksStreams;
import com.atlassian.applinks.internal.common.net.ResponsePreconditions;
import com.atlassian.applinks.internal.permission.PermissionValidationService;
import com.atlassian.applinks.internal.rest.capabilities.ApplinksCapabilitiesResource;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.RestRequestBuilder;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.NetworkErrorTranslator;
import com.atlassian.applinks.internal.status.error.SimpleApplinkError;
import com.atlassian.applinks.internal.status.error.UnexpectedResponseError;
import com.atlassian.applinks.internal.util.remote.AnonymousApplinksResponseHandler;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultRemoteCapabilitiesService
implements RemoteCapabilitiesService {
    @VisibleForTesting
    static final String REQUEST_CACHE_KEY = DefaultRemoteCapabilitiesService.class.getSimpleName();
    private static final Logger log = LoggerFactory.getLogger(DefaultRemoteCapabilitiesService.class);
    private static final int MAX_CACHE_SIZE = 100;
    private final AppLinksManifestDownloader manifestDownloader;
    private final ApplicationLinkService applicationLinkService;
    private final ApplinkHelper applinkHelper;
    private final ApplinksRequestCache applinksRequestCache;
    private final EventPublisher eventPublisher;
    private final PermissionValidationService permissionValidationService;
    private final ServiceExceptionFactory serviceExceptionFactory;
    private final Cache<ApplicationId, CachedCapabilities> capabilitiesCache;

    @Autowired
    public DefaultRemoteCapabilitiesService(ApplicationLinkService applicationLinkService, ApplinkHelper applinkHelper, ApplinksRequestCache applinksRequestCache, EventPublisher eventPublisher, AppLinksManifestDownloader manifestDownloader, PermissionValidationService permissionValidationService, ServiceExceptionFactory serviceExceptionFactory, CacheFactory cacheFactory) {
        this.applicationLinkService = applicationLinkService;
        this.applinkHelper = applinkHelper;
        this.applinksRequestCache = applinksRequestCache;
        this.eventPublisher = eventPublisher;
        this.manifestDownloader = manifestDownloader;
        this.permissionValidationService = permissionValidationService;
        this.serviceExceptionFactory = serviceExceptionFactory;
        this.capabilitiesCache = cacheFactory.getCache("applinks.capabilities", null, new CacheSettingsBuilder().local().maxEntries(100).build());
    }

    @PostConstruct
    public void register() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregister() {
        this.eventPublisher.unregister((Object)this);
    }

    @PreDestroy
    public void purgeCache() {
        this.capabilitiesCache.removeAll();
    }

    @Override
    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink applink) throws NoAccessException {
        this.permissionValidationService.validateAdmin();
        return this.getCapabilitiesUnrestricted(applink);
    }

    @Override
    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationId id) throws NoSuchApplinkException, NoAccessException {
        return this.getCapabilities(this.applinkHelper.getApplicationLink(id));
    }

    @Override
    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationLink applink, long maxAge, @Nonnull TimeUnit units) throws InvalidArgumentException, NoAccessException {
        this.permissionValidationService.validateAdmin();
        this.validateMaxAge(maxAge);
        Objects.requireNonNull(units, "units");
        ApplinkErrorType linkValidationError = DefaultRemoteCapabilitiesService.validateAtlassianApplink(applink);
        if (linkValidationError != null) {
            return new RemoteCapabilitiesError(new SimpleApplinkError(linkValidationError));
        }
        ApplinksRequestCache.Cache<ApplicationId, CachedCapabilities> requestCache = this.getRequestCache();
        CachedCapabilities cachedCapabilities = requestCache.get(applink.getId());
        if (cachedCapabilities != null && cachedCapabilities.isUpToDate(maxAge, units)) {
            return cachedCapabilities.capabilities;
        }
        cachedCapabilities = (CachedCapabilities)this.capabilitiesCache.get((Object)applink.getId());
        if (cachedCapabilities != null && cachedCapabilities.isUpToDate(maxAge, units)) {
            requestCache.put(applink.getId(), cachedCapabilities);
            return cachedCapabilities.capabilities;
        }
        return this.updateAndGet(applink, cachedCapabilities);
    }

    @Override
    @Nonnull
    public RemoteApplicationCapabilities getCapabilities(@Nonnull ApplicationId id, long maxAge, @Nonnull TimeUnit units) throws InvalidArgumentException, NoSuchApplinkException, NoAccessException {
        return this.getCapabilities(this.applinkHelper.getApplicationLink(id), maxAge, units);
    }

    @EventListener
    public void onManifestDownloaded(@Nonnull ManifestDownloadedEvent manifestDownloaded) {
        Manifest manifest = manifestDownloaded.getManifest();
        ApplicationLink applink = this.getApplinkSafe(manifest);
        if (applink != null) {
            CachedCapabilities cachedCapabilities = (CachedCapabilities)this.capabilitiesCache.get((Object)manifest.getId());
            log.debug("Updating cache for applink ID {}", (Object)applink.getId());
            this.updateAndGet(applink, manifest, cachedCapabilities);
        }
    }

    @EventListener
    public void onManifestDownloadFailed(@Nonnull ManifestDownloadFailedEvent manifestDownloadFailed) {
        ApplicationLink applink = this.findApplinkByUrl(manifestDownloadFailed.getUri());
        if (applink != null) {
            CachedCapabilities cachedCapabilities = (CachedCapabilities)this.capabilitiesCache.get((Object)applink.getId());
            ApplinkError error = manifestDownloadFailed.getCause() != null ? NetworkErrorTranslator.toApplinkError(manifestDownloadFailed.getCause(), "Failed to download manifest") : new SimpleApplinkError(ApplinkErrorType.UNKNOWN);
            log.debug("Updating cache for applink ID {} with error {}", (Object)applink.getId(), (Object)error.getType());
            this.updateAndGet(applink.getId(), cachedCapabilities, DefaultRemoteCapabilitiesService.withError(cachedCapabilities, error));
        }
    }

    @EventListener
    public void onApplinkDeleted(ApplicationLinkDeletedEvent applinkDeleted) {
        ApplicationId id = applinkDeleted.getApplicationId();
        log.debug("Removing cache entry for deleted applink ID {}", (Object)id);
        this.capabilitiesCache.remove((Object)id);
    }

    @EventListener
    public void onApplinkCreated(ApplicationLinkAddedEvent applinkCreated) {
        this.getCapabilitiesUnrestricted(applinkCreated.getApplicationLink());
    }

    @VisibleForTesting
    Cache<ApplicationId, CachedCapabilities> getCapabilitiesCache() {
        return this.capabilitiesCache;
    }

    private RemoteApplicationCapabilities getCapabilitiesUnrestricted(@Nonnull ApplicationLink applink) {
        ApplinkErrorType linkValidationError = DefaultRemoteCapabilitiesService.validateAtlassianApplink(applink);
        if (linkValidationError != null) {
            return new RemoteCapabilitiesError(new SimpleApplinkError(linkValidationError));
        }
        ApplinksRequestCache.Cache<ApplicationId, CachedCapabilities> requestCache = this.getRequestCache();
        CachedCapabilities cachedCapabilities = requestCache.get(applink.getId());
        if (cachedCapabilities != null) {
            return cachedCapabilities.capabilities;
        }
        cachedCapabilities = (CachedCapabilities)this.capabilitiesCache.get((Object)applink.getId());
        if (cachedCapabilities != null) {
            requestCache.put(applink.getId(), cachedCapabilities);
            return cachedCapabilities.capabilities;
        }
        return this.updateAndGet(applink, null);
    }

    private ApplinksRequestCache.Cache<ApplicationId, CachedCapabilities> getRequestCache() {
        return this.applinksRequestCache.getCache(REQUEST_CACHE_KEY, ApplicationId.class, CachedCapabilities.class);
    }

    @Nullable
    private ApplicationLink findApplinkByUrl(@Nonnull URI uri) {
        return ApplinksStreams.toStream(this.applicationLinkService.getApplicationLinks()).filter(ApplicationLinks.withRpcUrl(uri)).findFirst().orElse(null);
    }

    @Nullable
    private ApplicationLink getApplinkSafe(@Nonnull Manifest manifest) {
        try {
            return this.applinkHelper.getApplicationLink(manifest.getId());
        }
        catch (Exception e) {
            log.warn("Exception trying to get Applink for manifest with ID {}", (Object)manifest.getId());
            log.debug("Stack trace for manifest with ID {}", (Object)manifest.getId(), (Object)e);
            return null;
        }
    }

    private void validateMaxAge(long maxAge) throws InvalidArgumentException {
        if (maxAge < 0L) {
            throw this.serviceExceptionFactory.raise(InvalidValueException.class, new Serializable[]{"Max age", ">=0", Long.valueOf(maxAge)});
        }
    }

    @Nonnull
    private RemoteApplicationCapabilities updateAndGet(@Nonnull ApplicationLink applink, @Nullable CachedCapabilities currentCapabilities) {
        try {
            Manifest manifest = this.manifestDownloader.downloadNoEvent(applink.getRpcUrl());
            return this.updateAndGet(applink, manifest, currentCapabilities);
        }
        catch (ManifestNotFoundException e) {
            ApplinkError error = NetworkErrorTranslator.toApplinkError(e, "Failed to download manifest");
            return this.updateAndGet(applink.getId(), currentCapabilities, DefaultRemoteCapabilitiesService.withError(currentCapabilities, error));
        }
    }

    @Nonnull
    private RemoteApplicationCapabilities updateAndGet(@Nonnull ApplicationLink applink, @Nonnull Manifest manifest, @Nullable CachedCapabilities currentCapabilities) {
        CachedCapabilities updatedCapabilities;
        ApplicationVersion applicationVersion = DefaultRemoteCapabilitiesService.parseVersion(applink.getId(), manifest.getVersion());
        ApplicationVersion applinksVersion = DefaultRemoteCapabilitiesService.parseApplinksVersion(applink.getId(), manifest.getAppLinksVersion());
        if (applicationVersion == null || applinksVersion == null) {
            log.warn("Manifest for applink {} did not contain valid versions, skipping cache update", (Object)applink.getId());
            return this.updateAndGet(applink.getId(), currentCapabilities, DefaultRemoteCapabilitiesService.withError(currentCapabilities, DefaultRemoteCapabilitiesService.createManifestInvalidError(manifest.getVersion(), manifest.getAppLinksVersion())));
        }
        if (currentCapabilities != null) {
            if (applinksVersion.equals(currentCapabilities.capabilities.getApplinksVersion()) && currentCapabilities.capabilities.getError() == null) {
                updatedCapabilities = currentCapabilities.updatedNow();
            } else {
                updatedCapabilities = currentCapabilities.withVersions(applicationVersion, applinksVersion);
                updatedCapabilities = this.withRetrievedCapabilities(applink, applinksVersion, updatedCapabilities);
            }
        } else {
            updatedCapabilities = new CachedCapabilities(new DefaultRemoteCapabilities.Builder().applicationVersion(applicationVersion).applinksVersion(applinksVersion).build());
            updatedCapabilities = this.withRetrievedCapabilities(applink, applinksVersion, updatedCapabilities);
        }
        return this.updateAndGet(applink.getId(), currentCapabilities, updatedCapabilities);
    }

    @Nonnull
    private RemoteApplicationCapabilities updateAndGet(@Nonnull ApplicationId id, @Nullable CachedCapabilities currentCapabilities, @Nonnull CachedCapabilities newCapabilities) {
        boolean successfulUpdate;
        CachedCapabilities capabilitiesAnswer = currentCapabilities != null ? ((successfulUpdate = this.capabilitiesCache.replace((Object)id, (Object)currentCapabilities, (Object)newCapabilities)) ? newCapabilities : (CachedCapabilities)this.capabilitiesCache.get((Object)id)) : (CachedCapabilities)this.capabilitiesCache.putIfAbsent((Object)id, (Object)newCapabilities);
        capabilitiesAnswer = capabilitiesAnswer == null ? newCapabilities : capabilitiesAnswer;
        this.getRequestCache().put(id, capabilitiesAnswer);
        return capabilitiesAnswer.capabilities;
    }

    @Nonnull
    private CachedCapabilities withRetrievedCapabilities(@Nonnull ApplicationLink applink, @Nonnull ApplicationVersion applinksVersion, @Nonnull CachedCapabilities toUpdate) {
        Set<ApplinksCapabilities> remoteCapabilities;
        CachedCapabilities answer = toUpdate;
        ApplicationVersion applinksWithCapabilitiesVersion = ApplicationVersion.parse("5.0.5");
        if (applinksVersion.compareTo(applinksWithCapabilitiesVersion) < 0) {
            remoteCapabilities = EnumSet.noneOf(ApplinksCapabilities.class);
            answer = answer.withError(null);
        } else {
            AuthorisationUriAwareRequest request = RestRequestBuilder.createAnonymousRequest(applink, ApplinksCapabilitiesResource.capabilitiesUrl());
            CapabilitiesResponseHandler capabilitiesHandler = new CapabilitiesResponseHandler();
            remoteCapabilities = DefaultRemoteCapabilitiesService.executeSafe(request, capabilitiesHandler);
            answer = answer.withError(capabilitiesHandler.error);
        }
        return answer.withRemoteCapabilities(remoteCapabilities);
    }

    @Nonnull
    private static CachedCapabilities withError(@Nullable CachedCapabilities currentCapabilities, @Nonnull ApplinkError newError) {
        return currentCapabilities != null ? currentCapabilities.withError(newError) : new CachedCapabilities(new RemoteCapabilitiesError(newError));
    }

    @Nonnull
    private static ApplinkError createManifestInvalidError(@Nullable String applicationVersion, @Nullable Version applinksVersion) {
        return new SimpleApplinkError(ApplinkErrorType.UNEXPECTED_RESPONSE, String.format("applicationVersion=%s,applinksVersion=%s", applicationVersion, applinksVersion));
    }

    @Nonnull
    private static Set<ApplinksCapabilities> executeSafe(@Nonnull ApplicationLinkRequest request, @Nonnull CapabilitiesResponseHandler capabilitiesHandler) {
        try {
            return (Set)request.execute((ApplicationLinkResponseHandler)capabilitiesHandler);
        }
        catch (ResponseException e) {
            capabilitiesHandler.error = NetworkErrorTranslator.toApplinkError(e, "Failed to retrieve capabilities");
            return EnumSet.noneOf(ApplinksCapabilities.class);
        }
    }

    @Nullable
    private static ApplicationVersion parseVersion(@Nonnull ApplicationId id, @Nullable String version) {
        if (StringUtils.isBlank((CharSequence)version)) {
            return null;
        }
        try {
            return ApplicationVersion.parse(version);
        }
        catch (IllegalArgumentException e) {
            log.warn("Error when parsing application version {} for applink {}", (Object)version, (Object)id);
            log.debug("Stack trace: parsing application version {} for applink {}", new Object[]{version, id, e});
            return null;
        }
    }

    @Nullable
    private static ApplicationVersion parseApplinksVersion(@Nonnull ApplicationId id, @Nullable Version version) {
        if (version == null) {
            return null;
        }
        try {
            return ApplicationVersion.parse(version.toString());
        }
        catch (IllegalArgumentException e) {
            log.warn("Error when parsing applinks version {} for applink {}", (Object)version, (Object)id);
            log.debug("Stack trace: parsing applinks version {} for applink {}", new Object[]{version, id, e});
            return null;
        }
    }

    @Nullable
    private static ApplinkErrorType validateAtlassianApplink(@Nonnull ApplicationLink link) {
        if (link.getType() instanceof GenericApplicationType) {
            return ApplinkErrorType.GENERIC_LINK;
        }
        if (!BuiltinApplinksType.class.isInstance(link.getType())) {
            return ApplinkErrorType.NON_ATLASSIAN;
        }
        return null;
    }

    private static class CapabilitiesResponseHandler
    extends AnonymousApplinksResponseHandler<Set<ApplinksCapabilities>> {
        ApplinkError error;

        private CapabilitiesResponseHandler() {
        }

        public Set<ApplinksCapabilities> handle(Response response) throws ResponseException {
            ResponsePreconditions.checkStatus(response, Response.Status.OK, Response.Status.NOT_FOUND);
            Response.Status status = Response.Status.fromStatusCode((int)response.getStatusCode());
            if (status == Response.Status.OK) {
                try {
                    return this.getCapabilities(response);
                }
                catch (Exception e) {
                    this.error = new UnexpectedResponseError(response);
                    return EnumSet.noneOf(ApplinksCapabilities.class);
                }
            }
            return EnumSet.noneOf(ApplinksCapabilities.class);
        }

        private Set<ApplinksCapabilities> getCapabilities(Response response) throws ResponseException {
            Iterable stringCapabilities = (Iterable)response.getEntity(List.class);
            Iterable enumCapabilities = Iterables.transform((Iterable)stringCapabilities, ApplinksEnums.fromNameSafe(ApplinksCapabilities.class));
            return Sets.newEnumSet((Iterable)Iterables.filter((Iterable)enumCapabilities, (Predicate)Predicates.notNull()), ApplinksCapabilities.class);
        }
    }

    @VisibleForTesting
    static class CachedCapabilities {
        final RemoteApplicationCapabilities capabilities;
        final long lastUpdated;

        CachedCapabilities(RemoteApplicationCapabilities capabilities, long lastUpdated) {
            this.capabilities = capabilities;
            this.lastUpdated = lastUpdated;
        }

        CachedCapabilities(RemoteApplicationCapabilities capabilities) {
            this(capabilities, System.currentTimeMillis());
        }

        CachedCapabilities updatedNow() {
            return new CachedCapabilities(this.capabilities);
        }

        CachedCapabilities withError(ApplinkError error) {
            return new CachedCapabilities(new DefaultRemoteCapabilities.Builder(this.capabilities).error(error).build());
        }

        CachedCapabilities withVersions(ApplicationVersion applicationVersion, ApplicationVersion applinksVersion) {
            return new CachedCapabilities(new DefaultRemoteCapabilities.Builder(this.capabilities).applicationVersion(applicationVersion).applinksVersion(applinksVersion).build());
        }

        CachedCapabilities withRemoteCapabilities(Set<ApplinksCapabilities> remoteCapabilities) {
            return new CachedCapabilities(new DefaultRemoteCapabilities.Builder(this.capabilities).capabilities(remoteCapabilities).build());
        }

        boolean isUpToDate(long maxAge, TimeUnit units) {
            long acceptableLastUpdated = System.currentTimeMillis() - units.toMillis(maxAge);
            return this.lastUpdated >= acceptableLastUpdated;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CachedCapabilities that = (CachedCapabilities)o;
            return Objects.equals(this.lastUpdated, that.lastUpdated) && Objects.equals(this.capabilities, that.capabilities);
        }

        public int hashCode() {
            return Objects.hash(this.capabilities, this.lastUpdated);
        }
    }
}

