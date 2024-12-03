/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.LicenseException
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nullable
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license.store;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.event.NoOpEventPublisher;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.internal.license.store.LicenseStoreInternal;
import com.atlassian.confluence.license.store.InternalLicenceUpdatedEvent;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.LicenseException;
import com.atlassian.extras.api.LicenseManager;
import com.atlassian.license.LicensePair;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ApplicationConfigurationLicenseStore
implements LicenseStoreInternal {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfigurationLicenseStore.class);
    private final ApplicationConfiguration store;
    private final LicenseManager manager;
    private final ResettableLazyReference<ClusterConfigurationHelperInternal> clusterConfigurationHelperInternal;
    private EventPublisher publisher = new NoOpEventPublisher();
    private final Lock installationLock = new ReentrantLock();
    private AtlassianLicense activeLicense;

    public ApplicationConfigurationLicenseStore(ApplicationConfiguration store, LicenseManager manager) {
        this.store = (ApplicationConfiguration)Preconditions.checkNotNull((Object)store);
        this.manager = (LicenseManager)Preconditions.checkNotNull((Object)manager);
        this.clusterConfigurationHelperInternal = new ResettableLazyReference<ClusterConfigurationHelperInternal>(){

            protected ClusterConfigurationHelperInternal create() {
                if (ContainerManager.isContainerSetup()) {
                    return (ClusterConfigurationHelperInternal)ContainerManager.getComponent((String)"clusterConfigurationHelper");
                }
                return null;
            }
        };
    }

    @Override
    public void install(String licenseString) {
        this.installLicense(licenseString, true, true, true);
        ClusterConfigurationHelperInternal configurationHelper = (ClusterConfigurationHelperInternal)this.clusterConfigurationHelperInternal.get();
        if (configurationHelper == null) {
            this.clusterConfigurationHelperInternal.reset();
            return;
        }
        configurationHelper.saveSharedProperty("atlassian.license.message", licenseString);
    }

    @Override
    public void installTransiently(String licenseString) {
        this.installLicense(licenseString, false, false, false);
    }

    @Override
    public void clearLicenseFromMemory() {
        if (this.installationLock.tryLock()) {
            try {
                this.activeLicense = null;
            }
            finally {
                this.installationLock.unlock();
            }
        } else {
            log.warn("Unable to acquire to the lock to clear the license from memory");
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public @NonNull AtlassianLicense retrieve() {
        try {
            boolean locked = this.installationLock.tryLock(1L, TimeUnit.SECONDS);
            if (!locked) throw new com.atlassian.confluence.license.exception.LicenseException("Unable to acquire to the lock to retrieve the license");
            try {
                if (this.activeLicense != null) {
                    AtlassianLicense atlassianLicense = this.activeLicense;
                    return atlassianLicense;
                }
                this.activeLicense = this.retrieveFromStorage();
                if (this.activeLicense == null) {
                    throw new com.atlassian.confluence.license.exception.LicenseException("Failed to retrieve license from storage.");
                }
                AtlassianLicense atlassianLicense = this.activeLicense;
                return atlassianLicense;
            }
            finally {
                this.installationLock.unlock();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new com.atlassian.confluence.license.exception.LicenseException("Interrupted trying to lock to retrieve the license", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public @NonNull Optional<AtlassianLicense> retrieveOptional() throws com.atlassian.confluence.license.exception.LicenseException {
        try {
            boolean locked = this.installationLock.tryLock(1L, TimeUnit.SECONDS);
            if (!locked) throw new com.atlassian.confluence.license.exception.LicenseException("Unable to acquire to the lock to retrieve the license");
            try {
                if (this.activeLicense != null) {
                    Optional<AtlassianLicense> optional = Optional.of(this.activeLicense);
                    return optional;
                }
                this.activeLicense = this.retrieveFromStorage();
                Optional<AtlassianLicense> optional = Optional.ofNullable(this.activeLicense);
                return optional;
            }
            finally {
                this.installationLock.unlock();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new com.atlassian.confluence.license.exception.LicenseException("Interrupted trying to lock to retrieve the license", e);
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof InternalLicenceUpdatedEvent) {
            log.debug("Handling a license change from another node");
            String licenseString = ((InternalLicenceUpdatedEvent)event).getLicenseString();
            this.installLicense(licenseString, true, true, false);
        }
    }

    @Nullable
    private AtlassianLicense retrieveFromStorage() {
        AtlassianLicense result = null;
        String licenseString = (String)this.store.getProperty((Object)"atlassian.license.message");
        if (licenseString != null) {
            try {
                result = this.manager.getLicense(licenseString);
            }
            catch (LicenseException ex) {
                log.warn("Unable to parse license string: {}", (Object)ex.getMessage());
            }
        } else {
            String licenseMessage = (String)this.store.getProperty((Object)"confluence.license.message");
            String licenseHash = (String)this.store.getProperty((Object)"confluence.license.hash");
            if (licenseMessage != null && licenseHash != null) {
                try {
                    LicensePair pair = new LicensePair(licenseMessage, licenseHash);
                    result = this.manager.getLicense(pair.getOriginalLicenseString());
                }
                catch (com.atlassian.license.LicenseException e) {
                    log.warn("Unable to parse license string", (Throwable)e);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void installLicense(String licenseString, boolean persist, boolean notifyLocally, boolean notifyGlobally) {
        if (this.installationLock.tryLock()) {
            AtlassianLicense newLicense;
            AtlassianLicense currentLicense;
            try {
                currentLicense = this.retrieveFromStorage();
                newLicense = this.manager.getLicense(licenseString);
                if (newLicense == null) {
                    throw new com.atlassian.confluence.license.exception.LicenseException(String.format("Failed to install license [%s] via [%s], return value was null.", licenseString, LicenseManager.class.getName()));
                }
                if (persist) {
                    try {
                        this.store.setProperty((Object)"atlassian.license.message", (Object)licenseString);
                        this.store.removeProperty((Object)"confluence.license.hash");
                        this.store.removeProperty((Object)"confluence.license.message");
                        this.store.save();
                        log.info("License has been successfully stored.");
                    }
                    catch (ConfigurationException e) {
                        throw new com.atlassian.confluence.license.exception.LicenseException(String.format("Failed to install license [%s] to [%s], see cause.", licenseString, ApplicationConfiguration.class.getName()), e);
                    }
                }
                this.activeLicense = newLicense;
            }
            finally {
                this.installationLock.unlock();
            }
            if (notifyLocally) {
                log.debug("Notifying locally that the license has changed");
                this.publisher.publish((Object)new LicenceUpdatedEvent(this, newLicense, currentLicense));
            }
            if (notifyGlobally) {
                log.debug("Notifying globally that the license has changed");
                this.publisher.publish((Object)new InternalLicenceUpdatedEvent(this, licenseString));
            }
        } else {
            throw new com.atlassian.confluence.license.exception.LicenseException("Concurrent license installation detected, please check the updated license before attempting to install a new one.");
        }
    }

    void notifyPublisherAvailable(EventPublisher publisher) {
        this.publisher = (EventPublisher)Preconditions.checkNotNull((Object)publisher);
        this.publisher.register((Object)this);
    }

    void notifyPublisherUnavailable() {
        this.publisher.unregister((Object)this);
        this.publisher = new NoOpEventPublisher();
    }
}

