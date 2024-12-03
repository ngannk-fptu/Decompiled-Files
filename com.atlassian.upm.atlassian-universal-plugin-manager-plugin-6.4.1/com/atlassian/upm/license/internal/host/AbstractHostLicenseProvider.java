/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.Supplier
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.Supplier;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.BaseApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHostLicenseProvider
implements HostLicenseProvider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHostLicenseProvider.class);
    private static final CacheSettings LICENSE_CACHE_SETTINGS = new CacheSettingsBuilder().remote().replicateViaInvalidation().expireAfterWrite(30L, TimeUnit.DAYS).build();
    private final LicenseHandler licenseHandler;
    protected final HostApplicationLicenseFactory hostApplicationLicenseFactory;
    private final Option<CachedReference<Iterable<HostApplicationLicense>>> cache;
    private boolean cachingEnabled = false;
    private boolean multipleLicensesAllowed;

    protected AbstractHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager) {
        this(licenseHandler, hostApplicationLicenseFactory, appManager, Option.none(CacheFactory.class));
    }

    protected AbstractHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager, Option<CacheFactory> cacheFactory) {
        this.licenseHandler = Objects.requireNonNull(licenseHandler, "licenseHandler");
        this.hostApplicationLicenseFactory = Objects.requireNonNull(hostApplicationLicenseFactory, "hostApplicationLicenseFactory");
        this.cache = cacheFactory.map(cf -> cf.getCachedReference("HostLicenseCache", (Supplier)new LicenseInfoSupplier(), LICENSE_CACHE_SETTINGS));
        try {
            this.multipleLicensesAllowed = licenseHandler.hostAllowsMultipleLicenses() && appManager.isApplicationSupportEnabled();
        }
        catch (NoSuchMethodError e) {
            this.multipleLicensesAllowed = false;
        }
    }

    private Option<Integer> getEdition(Iterable<HostApplicationLicense> licenses) {
        Option<Integer> maxTier = Option.none();
        for (HostApplicationLicense lic : licenses) {
            Option<Integer> edition = lic.getEdition();
            if (lic.isEvaluation()) continue;
            if (!edition.isDefined()) {
                return Option.none();
            }
            if (!maxTier.isDefined()) {
                maxTier = edition;
                continue;
            }
            for (int max : maxTier) {
                for (int ed : edition) {
                    maxTier = Option.some(Math.max(max, ed));
                }
            }
        }
        return maxTier;
    }

    private LicenseType getLicenseType(Iterable<HostApplicationLicense> licenses) {
        Optional<HostApplicationLicense> noStarterNoEvaluationLicense = Iterables.toStream(licenses).filter(hostApplicationLicense -> !hostApplicationLicense.isEvaluation() && !hostApplicationLicense.isStarter()).findFirst();
        if (noStarterNoEvaluationLicense.isPresent()) {
            return noStarterNoEvaluationLicense.get().getLicenseType();
        }
        Iterator<HostApplicationLicense> iterator = licenses.iterator();
        if (iterator.hasNext()) {
            HostApplicationLicense lic = iterator.next();
            return lic.getLicenseType();
        }
        return LicenseType.COMMERCIAL;
    }

    private Option<String> getSen(Iterable<HostApplicationLicense> licenses) {
        Option<String> sen = Option.none();
        for (HostApplicationLicense lic : licenses) {
            for (String licSen : lic.getSen()) {
                if (!sen.isDefined()) {
                    sen = Option.some(licSen);
                    continue;
                }
                for (String s : sen) {
                    if (licSen.compareTo(s) >= 0) continue;
                    sen = Option.some(licSen);
                }
            }
        }
        return sen;
    }

    private Option<SubscriptionPeriod> getSubscriptionPeriod(Iterable<HostApplicationLicense> licenses) {
        boolean annual = false;
        for (HostApplicationLicense lic : licenses) {
            for (SubscriptionPeriod period : lic.getSubscriptionPeriod()) {
                switch (period) {
                    case MONTHLY: {
                        return Option.some(SubscriptionPeriod.MONTHLY);
                    }
                    case ANNUAL: {
                        annual = true;
                    }
                }
            }
        }
        return annual ? Option.some(SubscriptionPeriod.ANNUAL) : Option.none(SubscriptionPeriod.class);
    }

    private Option<DateTime> getLastModifiedDate(Iterable<HostApplicationLicense> licenses) {
        Option<DateTime> date = Option.none();
        for (HostApplicationLicense lic : licenses) {
            for (DateTime lastModified : lic.getLastModifiedDate()) {
                if (!date.isDefined()) {
                    date = Option.some(lastModified);
                    continue;
                }
                for (DateTime d : date) {
                    if (!d.isBefore((ReadableInstant)lastModified)) continue;
                    date = Option.some(lastModified);
                }
            }
        }
        return date;
    }

    private Option<DateTime> getExpiryDate(Iterable<HostApplicationLicense> licenses) {
        Option<DateTime> date = Option.none();
        for (HostApplicationLicense lic : licenses) {
            Option<DateTime> expiryDate = lic.getExpiryDate();
            if (!expiryDate.isDefined()) {
                return Option.none();
            }
            for (DateTime expiry : expiryDate) {
                if (!date.isDefined()) {
                    date = Option.some(expiry);
                    continue;
                }
                for (DateTime d : date) {
                    if (!d.isBefore((ReadableInstant)expiry)) continue;
                    date = Option.some(expiry);
                }
            }
        }
        return date;
    }

    @Override
    public Iterable<HostApplicationLicense> getHostApplicationLicenses() {
        Iterator<CachedReference<Iterable<HostApplicationLicense>>> iterator;
        if (this.cachingEnabled && (iterator = this.cache.iterator()).hasNext()) {
            CachedReference<Iterable<HostApplicationLicense>> c = iterator.next();
            return (Iterable)c.get();
        }
        return this.getAllLicenseInfoInternal();
    }

    private Iterable<HostApplicationLicense> getAllLicenseInfoInternal() {
        List<HostApplicationLicense> lics = Iterables.toList(this.getHostLicensesInternal());
        if (lics.isEmpty()) {
            logger.debug("Host product is currently unlicensed.");
        }
        return Collections.unmodifiableList(lics);
    }

    protected abstract Option<HostApplicationLicense> getSingleHostLicenseInternal();

    protected Iterable<HostApplicationLicense> getHostLicensesInternal() {
        if (this.multipleLicensesAllowed) {
            ArrayList<HostApplicationLicense> products = new ArrayList<HostApplicationLicense>();
            for (String productKey : this.licenseHandler.getProductKeys()) {
                String rawLicense = this.licenseHandler.getRawProductLicense(productKey);
                MultiProductLicenseDetails baseLicense = this.licenseHandler.decodeLicenseDetails(rawLicense);
                products.add(this.hostApplicationLicenseFactory.getHostLicense(this.licenseHandler.getProductLicenseDetails(productKey), baseLicense, productKey, rawLicense));
            }
            return Collections.unmodifiableList(products);
        }
        return this.getSingleHostLicenseInternal();
    }

    @Override
    public void invalidateCache() {
        for (CachedReference<Iterable<HostApplicationLicense>> c : this.cache) {
            c.reset();
        }
    }

    public void setCachingEnabled(boolean cachingEnabled) {
        if (this.cachingEnabled != cachingEnabled && (!cachingEnabled || this.cache.isDefined())) {
            this.cachingEnabled = cachingEnabled;
            if (!cachingEnabled) {
                this.invalidateCache();
            }
        }
    }

    @Override
    public Option<HostApplicationEmbeddedAddonLicense> getPluginLicenseDetails(String pluginKey) {
        for (HostApplicationLicense hostLicense : this.getHostApplicationLicenses()) {
            Option<HostApplicationEmbeddedAddonLicense> embedded = hostLicense.getEmbeddedAddonLicense(pluginKey);
            if (!embedded.isDefined()) continue;
            return embedded;
        }
        return Option.none();
    }

    @Override
    public HostApplicationLicenseAttributes getHostApplicationLicenseAttributes() {
        Iterable<HostApplicationLicense> licenses = this.getHostApplicationLicenses();
        return new HostApplicationLicenseAttributes(this.getEdition(licenses), this.getLicenseType(licenses), Iterables.toStream(licenses).allMatch(BaseApplicationLicense::isEvaluation), Iterables.toStream(licenses).anyMatch(BaseApplicationLicense::isDataCenter), Iterables.toStream(licenses).anyMatch(BaseApplicationLicense::isAutoRenewal), this.getSen(licenses), this.getLastModifiedDate(licenses), this.getExpiryDate(licenses), this.getSubscriptionPeriod(licenses), Iterables.toStream(licenses).allMatch(BaseApplicationLicense::isStack));
    }

    private class LicenseInfoSupplier
    implements Supplier<Iterable<HostApplicationLicense>> {
        private LicenseInfoSupplier() {
        }

        public Iterable<HostApplicationLicense> get() {
            return AbstractHostLicenseProvider.this.getAllLicenseInfoInternal();
        }
    }
}

