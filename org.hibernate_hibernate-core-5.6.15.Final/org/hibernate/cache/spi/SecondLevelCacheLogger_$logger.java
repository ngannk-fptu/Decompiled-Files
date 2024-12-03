/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.DelegatingBasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class SecondLevelCacheLogger_$logger
extends DelegatingBasicLogger
implements SecondLevelCacheLogger,
BasicLogger,
Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FQCN = SecondLevelCacheLogger_$logger.class.getName();
    private static final Locale LOCALE = Locale.ROOT;

    public SecondLevelCacheLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    @Override
    public final void attemptToStartAlreadyStartedCacheProvider() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.attemptToStartAlreadyStartedCacheProvider$str(), new Object[0]);
    }

    protected String attemptToStartAlreadyStartedCacheProvider$str() {
        return "HHH90001001: Attempt to restart an already started RegionFactory.  Use sessionFactory.close() between repeated calls to buildSessionFactory. Using previously created RegionFactory.";
    }

    @Override
    public final void attemptToStopAlreadyStoppedCacheProvider() {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.attemptToStopAlreadyStoppedCacheProvider$str(), new Object[0]);
    }

    protected String attemptToStopAlreadyStoppedCacheProvider$str() {
        return "HHH90001002: Attempt to restop an already stopped JCacheRegionFactory.";
    }

    @Override
    public final void readOnlyCachingMutableEntity(NavigableRole navigableRole) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.readOnlyCachingMutableEntity$str(), (Object)navigableRole);
    }

    protected String readOnlyCachingMutableEntity$str() {
        return "HHH90001003: Read-only caching was requested for mutable entity [%s]";
    }

    @Override
    public final void readOnlyCachingMutableNaturalId(NavigableRole navigableRole) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.readOnlyCachingMutableNaturalId$str(), (Object)navigableRole);
    }

    protected String readOnlyCachingMutableNaturalId$str() {
        return "HHH90001004: Read-only caching was requested for mutable natural-id for entity [%s]";
    }

    @Override
    public final void softLockedCacheExpired(String regionName, Object key) {
        this.log.logf(FQCN, Logger.Level.INFO, null, this.softLockedCacheExpired$str(), (Object)regionName, key);
    }

    protected String softLockedCacheExpired$str() {
        return "HHH90001005: Cache[%s] Key[%s]\nA soft-locked cache entry was expired by the underlying cache. If this happens regularly you should consider increasing the cache timeouts and/or capacity limits";
    }

    @Override
    public final void missingCacheCreated(String regionName, String configurationPropertyToDisableKey, String configurationPropertyToDisableValue) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.missingCacheCreated$str(), (Object)regionName, (Object)configurationPropertyToDisableKey, (Object)configurationPropertyToDisableValue);
    }

    protected String missingCacheCreated$str() {
        return "HHH90001006: Missing cache[%1$s] was created on-the-fly. The created cache will use a provider-specific default configuration: make sure you defined one. You can disable this warning by setting '%2$s' to '%3$s'.";
    }

    @Override
    public final void usingLegacyCacheName(String currentName, String legacyName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.usingLegacyCacheName$str(), (Object)currentName, (Object)legacyName);
    }

    protected String usingLegacyCacheName$str() {
        return "HHH90001007: Using legacy cache name [%2$s] because configuration could not be found for cache [%1$s]. Update your configuration to rename cache [%2$s] to [%1$s].";
    }

    @Override
    public final void nonStandardSupportForAccessType(String key, String accessType, String regionName) {
        this.log.logf(FQCN, Logger.Level.WARN, null, this.nonStandardSupportForAccessType$str(), (Object)key, (Object)accessType, (Object)regionName);
    }

    protected String nonStandardSupportForAccessType$str() {
        return "HHH90001008: Cache [%1$s] uses the [%2$s] access type, but [%3$s] does not support it natively. Make sure your cache implementation supports JTA transactions.";
    }
}

