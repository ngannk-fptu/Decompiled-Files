/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.cache.spi;

import org.hibernate.metamodel.model.domain.NavigableRole;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=90001001, max=90002000)
public interface SecondLevelCacheLogger
extends BasicLogger {
    public static final SecondLevelCacheLogger INSTANCE = (SecondLevelCacheLogger)Logger.getMessageLogger(SecondLevelCacheLogger.class, (String)"org.hibernate.orm.cache");
    public static final int NAMESPACE = 90001000;

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempt to restart an already started RegionFactory.  Use sessionFactory.close() between repeated calls to buildSessionFactory. Using previously created RegionFactory.", id=90001001)
    public void attemptToStartAlreadyStartedCacheProvider();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Attempt to restop an already stopped JCacheRegionFactory.", id=90001002)
    public void attemptToStopAlreadyStoppedCacheProvider();

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Read-only caching was requested for mutable entity [%s]", id=90001003)
    public void readOnlyCachingMutableEntity(NavigableRole var1);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Read-only caching was requested for mutable natural-id for entity [%s]", id=90001004)
    public void readOnlyCachingMutableNaturalId(NavigableRole var1);

    @LogMessage(level=Logger.Level.INFO)
    @Message(value="Cache[%s] Key[%s]\nA soft-locked cache entry was expired by the underlying cache. If this happens regularly you should consider increasing the cache timeouts and/or capacity limits", id=90001005)
    public void softLockedCacheExpired(String var1, Object var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Missing cache[%1$s] was created on-the-fly. The created cache will use a provider-specific default configuration: make sure you defined one. You can disable this warning by setting '%2$s' to '%3$s'.", id=90001006)
    public void missingCacheCreated(String var1, String var2, String var3);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Using legacy cache name [%2$s] because configuration could not be found for cache [%1$s]. Update your configuration to rename cache [%2$s] to [%1$s].", id=90001007)
    public void usingLegacyCacheName(String var1, String var2);

    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Cache [%1$s] uses the [%2$s] access type, but [%3$s] does not support it natively. Make sure your cache implementation supports JTA transactions.", id=90001008)
    public void nonStandardSupportForAccessType(String var1, String var2, String var3);
}

