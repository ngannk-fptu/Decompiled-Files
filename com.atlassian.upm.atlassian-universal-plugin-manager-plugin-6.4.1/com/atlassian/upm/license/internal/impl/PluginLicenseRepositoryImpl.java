/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseAddedEvent;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.api.license.event.PluginLicenseRemovedEvent;
import com.atlassian.upm.api.license.event.PluginLicenseRoleExceededEvent;
import com.atlassian.upm.api.license.event.PluginLicenseUpdatedEvent;
import com.atlassian.upm.api.license.event.PluginLicensesRefreshedEvent;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseEntityFactory;
import com.atlassian.upm.license.internal.PluginLicenseError;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEventPublisher;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicenseStore;
import com.atlassian.upm.license.internal.PluginLicenseValidator;
import com.atlassian.upm.license.internal.event.PluginLicenseCacheInvalidateEvent;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.impl.role.PluginLicensingRoleMembershipUpdatedEvent;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginDescriptorMetadataCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class PluginLicenseRepositoryImpl
implements DisposableBean,
InitializingBean,
PluginLicenseRepository,
PluginLicenseGlobalEventPublisher {
    private static final String LICENSE_CACHE_NAME = "UpmPluginLicenseCache";
    private static final CacheSettings LICENSE_CACHE_SETTINGS = new CacheSettingsBuilder().remote().replicateViaInvalidation().expireAfterWrite(1L, TimeUnit.HOURS).build();
    private static final Logger log = LoggerFactory.getLogger(PluginLicenseRepositoryImpl.class);
    private final HostLicenseProvider hostLicenseProvider;
    private final PluginLicenseValidator licenseValidator;
    private final PluginLicenseEventPublisherRegistry publisherRegistry;
    private final LicenseEntityFactory licenseEntityFactory;
    private final Cache<String, Option<PluginLicense>> licenseCache;
    private final RoleBasedPluginDescriptorMetadataCache rbpCache;
    private final PluginLicenseStore licenseStore;
    private final UpmPluginAccessor accessor;
    private final PluginLicenseEventPublisherRegistry licenseEventPublisher;
    private final EventPublisher atlassianEventPublisher;

    public PluginLicenseRepositoryImpl(HostLicenseProvider hostLicenseProvider, PluginLicenseValidator licenseValidator, PluginLicenseEventPublisherRegistry publisherRegistry, LicenseEntityFactory licenseEntityFactory, RoleBasedPluginDescriptorMetadataCache rbpCache, PluginLicenseStore licenseStore, UpmPluginAccessor accessor, CacheFactory cacheFactory, PluginLicenseEventPublisherRegistry licenseEventPublisher, EventPublisher atlassianEventPublisher) {
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.licenseValidator = Objects.requireNonNull(licenseValidator, "licenseValidator");
        this.publisherRegistry = Objects.requireNonNull(publisherRegistry, "publisherRegistry");
        this.licenseEntityFactory = Objects.requireNonNull(licenseEntityFactory, "licenseEntityFactory");
        this.rbpCache = Objects.requireNonNull(rbpCache, "rbpCache");
        this.licenseStore = Objects.requireNonNull(licenseStore, "licenseStore");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
        this.licenseEventPublisher = Objects.requireNonNull(licenseEventPublisher, "licenseEventPublisher");
        this.atlassianEventPublisher = Objects.requireNonNull(atlassianEventPublisher, "atlassianEventPublisher");
        this.licenseCache = cacheFactory.getCache(LICENSE_CACHE_NAME, (CacheLoader)new PluginLicenseCacheLoader(), LICENSE_CACHE_SETTINGS);
    }

    public void afterPropertiesSet() {
        this.publisherRegistry.registerGlobal(this);
    }

    public void destroy() {
        this.publisherRegistry.unregisterGlobal(this);
    }

    @Override
    public void publish(PluginLicenseEvent event) {
    }

    @Override
    public void publishGlobal(PluginLicenseGlobalEvent event) {
        if (event instanceof PluginLicenseCacheInvalidateEvent) {
            this.invalidateCache();
        } else if (event instanceof PluginLicensingRoleMembershipUpdatedEvent) {
            PluginLicensingRoleMembershipUpdatedEvent membershipEvent = (PluginLicensingRoleMembershipUpdatedEvent)event;
            String pluginKey = membershipEvent.getPlugin().getKey();
            this.invalidateCacheForPlugin(pluginKey);
            for (PluginLicense license : this.getPluginLicense(pluginKey)) {
                for (int licensedRoleCount : license.getEdition()) {
                    if (membershipEvent.getNewRoleCount() <= licensedRoleCount) continue;
                    this.licenseEventPublisher.publishEvent(new PluginLicenseRoleExceededEvent(pluginKey, membershipEvent.getNewRoleCount(), licensedRoleCount));
                }
            }
        }
    }

    @Override
    @Nonnull
    public Option<PluginLicense> getPluginLicense(String pluginKey) {
        Iterator<PluginLicense> iterator = this.getCachedLicense(pluginKey).iterator();
        if (iterator.hasNext()) {
            PluginLicense cachedLicense = iterator.next();
            return Option.some(cachedLicense);
        }
        return this.getUncachedPluginLicense(pluginKey);
    }

    public List<PluginLicense> getPluginLicenses() {
        List<String> storedLicenses = this.licenseStore.getPluginLicenses();
        ArrayList<PluginLicense> licenses = new ArrayList<PluginLicense>();
        HashSet<String> licenseKeys = new HashSet<String>();
        for (String pluginKey : storedLicenses) {
            Option<PluginLicense> licenseOption = this.getPluginLicense(pluginKey);
            for (PluginLicense license : licenseOption) {
                this.licenseCache.get((Object)license.getPluginKey());
                licenses.add(license);
                licenseKeys.add(pluginKey);
            }
        }
        for (HostApplicationLicense hostLicense : this.hostLicenseProvider.getHostApplicationLicenses()) {
            for (HostApplicationEmbeddedAddonLicense addonLicense : hostLicense.getEmbeddedAddonLicenses()) {
                String pluginKey = addonLicense.getPluginKey();
                if (licenseKeys.contains(pluginKey)) continue;
                licenses.add(this.getPluginLicense(addonLicense));
                licenseKeys.add(pluginKey);
            }
        }
        return Collections.unmodifiableList(licenses);
    }

    @Override
    public Either<PluginLicenseError, Option<String>> setPluginLicense(String pluginKey, String licenseString) {
        this.licenseCache.remove((Object)pluginKey);
        if (StringUtils.isBlank((CharSequence)licenseString)) {
            return Either.left(new PluginLicenseError(PluginLicenseError.Type.SETTING_EMPTY_LICENSE));
        }
        Either<PluginLicenseError, PluginLicense> validation = this.licenseValidator.validate(pluginKey, licenseString.trim());
        Iterator<PluginLicenseError> iterator = validation.left().iterator();
        if (iterator.hasNext()) {
            PluginLicenseError error = iterator.next();
            return Either.left(error);
        }
        PluginLicense license = validation.right().get();
        Option<String> maybePreviousLicenseString = this.licenseStore.setPluginLicense(pluginKey, licenseString.trim());
        Option<PluginLicense> hadPreviousLicense = this.decodeLicense(maybePreviousLicenseString, pluginKey);
        for (PluginLicense previousLicense : hadPreviousLicense) {
            this.publisherRegistry.publishEvent(new PluginLicenseUpdatedEvent(license, previousLicense));
        }
        if (!hadPreviousLicense.isDefined()) {
            this.publisherRegistry.publishEvent(new PluginLicenseAddedEvent(license));
        }
        return Either.right(maybePreviousLicenseString);
    }

    @Override
    public Option<String> removePluginLicense(String pluginKey) {
        this.licenseCache.remove((Object)pluginKey);
        Option<String> maybePreviousLicenseString = this.licenseStore.removePluginLicense(pluginKey);
        for (PluginLicense previousLicense : this.decodeLicense(maybePreviousLicenseString, pluginKey)) {
            this.publisherRegistry.publishEvent(new PluginLicenseRemovedEvent(pluginKey, previousLicense));
        }
        return maybePreviousLicenseString;
    }

    @Override
    public void invalidateCache() {
        this.licenseCache.removeAll();
        this.rbpCache.removeAll();
        this.hostLicenseProvider.invalidateCache();
        this.atlassianEventPublisher.publish((Object)new PluginLicensesRefreshedEvent());
    }

    @Override
    public void invalidateCacheForPlugin(String pluginKey) {
        this.licenseCache.remove((Object)pluginKey);
        this.rbpCache.remove(pluginKey);
        if (this.hostLicenseProvider.getPluginLicenseDetails(pluginKey).isDefined()) {
            this.hostLicenseProvider.invalidateCache();
        }
    }

    private Option<PluginLicense> getUncachedPluginLicense(String pluginKey) {
        Iterator<String> iterator = this.licenseStore.getPluginLicense(pluginKey).iterator();
        if (iterator.hasNext()) {
            String rawLicense = iterator.next();
            return this.decodeLicense(Option.some(rawLicense), pluginKey);
        }
        return this.getEmbeddedPluginLicense(pluginKey);
    }

    private boolean licenseIsCacheable(PluginLicense license) {
        Iterator<LicenseError> iterator = license.getError().iterator();
        if (iterator.hasNext()) {
            LicenseError error = iterator.next();
            switch (error) {
                case TYPE_MISMATCH: 
                case USER_MISMATCH: 
                case EDITION_MISMATCH: 
                case ROLE_EXCEEDED: 
                case ROLE_UNDEFINED: {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private Option<PluginLicense> getCachedLicense(String pluginKey) {
        Option<PluginLicense> ret = (Option<PluginLicense>)this.licenseCache.get((Object)pluginKey);
        return ret == null ? Option.none(PluginLicense.class) : ret;
    }

    private Option<PluginLicense> getEmbeddedPluginLicense(String pluginKey) {
        try {
            Iterator<HostApplicationEmbeddedAddonLicense> iterator = this.hostLicenseProvider.getPluginLicenseDetails(pluginKey).iterator();
            if (iterator.hasNext()) {
                HostApplicationEmbeddedAddonLicense addonLicense = iterator.next();
                return Option.some(this.getPluginLicense(addonLicense));
            }
        }
        catch (Exception e) {
            PluginLicenseRepositoryImpl.logLicenseValidationError(pluginKey, e.toString(), Option.some(e));
            return Option.none();
        }
        return Option.none();
    }

    private PluginLicense getPluginLicense(HostApplicationEmbeddedAddonLicense addonLicense) {
        return this.licenseEntityFactory.getPluginLicense(addonLicense, this.accessor.getPlugin(addonLicense.getPluginKey()), this.hostLicenseProvider.getHostApplicationLicenseAttributes());
    }

    private Option<PluginLicense> decodeLicense(Option<String> maybeLicenseString, String pluginKey) {
        for (String licenseString : maybeLicenseString) {
            try {
                Either<PluginLicenseError, PluginLicense> decodeLicense = this.licenseValidator.validate(pluginKey, licenseString);
                Iterator<PluginLicenseError> iterator = decodeLicense.left().iterator();
                if (iterator.hasNext()) {
                    PluginLicenseError error = iterator.next();
                    PluginLicenseRepositoryImpl.logLicenseValidationError(pluginKey, String.valueOf((Object)error.getType()), error.getCause());
                    return Option.none(PluginLicense.class);
                }
                return decodeLicense.right().toOption();
            }
            catch (Exception e) {
                PluginLicenseRepositoryImpl.logLicenseValidationError(pluginKey, e.toString(), Option.some(e));
            }
        }
        return Option.none(PluginLicense.class);
    }

    private static void logLicenseValidationError(String pluginKey, String errorDesc, Option<? extends Throwable> error) {
        log.warn("Unexpected error decoding stored license for '" + pluginKey + "': " + errorDesc);
        for (Throwable throwable : error) {
            log.debug(throwable.toString(), throwable);
        }
    }

    private class PluginLicenseCacheLoader
    implements CacheLoader<String, Option<PluginLicense>> {
        private PluginLicenseCacheLoader() {
        }

        @Nonnull
        public Option<PluginLicense> load(@Nonnull String pluginKey) {
            for (PluginLicense license : PluginLicenseRepositoryImpl.this.getUncachedPluginLicense(pluginKey)) {
                if (!PluginLicenseRepositoryImpl.this.licenseIsCacheable(license)) continue;
                return Option.some(license);
            }
            return Option.none();
        }
    }
}

