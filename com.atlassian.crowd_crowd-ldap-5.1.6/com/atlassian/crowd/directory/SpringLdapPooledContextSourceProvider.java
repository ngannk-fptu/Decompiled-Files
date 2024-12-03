/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.diff.MapDiff
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.DiffResult
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.ldap.core.ContextSource
 *  org.springframework.ldap.pool2.factory.PooledContextSource
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.common.diff.MapDiff;
import com.atlassian.crowd.directory.LdapContextSourceFactory;
import com.atlassian.crowd.directory.LdapDirectoryClearingClusterEventPublisher;
import com.atlassian.crowd.directory.SpringLdapPoolStatistics;
import com.atlassian.crowd.directory.SpringLdapPoolStatisticsProvider;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.DiffResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.pool2.factory.PooledContextSource;

public class SpringLdapPooledContextSourceProvider
implements DisposableBean,
SpringLdapPoolStatisticsProvider,
InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SpringLdapPooledContextSourceProvider.class);
    private static final Set<DirectoryType> LDAP_DIRECTORY_TYPES = EnumSet.of(DirectoryType.CONNECTOR, DirectoryType.DELEGATING);
    private final LdapContextSourceFactory ldapContextSourceFactory;
    private final ConcurrentMap<Long, ContextSourceEntry> contextSources;
    private final EventPublisher eventPublisher;
    private final LdapDirectoryClearingClusterEventPublisher ldapDirectoryClearingClusterEventPublisher;
    private final LdapConnectionPropertiesDiffResultMapper diffResultMapper;

    public SpringLdapPooledContextSourceProvider(LdapContextSourceFactory ldapContextSourceFactory, EventPublisher eventPublisher, LdapDirectoryClearingClusterEventPublisher ldapDirectoryClearingClusterEventPublisher, LdapConnectionPropertiesDiffResultMapper diffResultMapper) {
        this(ldapContextSourceFactory, Maps.newConcurrentMap(), eventPublisher, ldapDirectoryClearingClusterEventPublisher, diffResultMapper);
    }

    @VisibleForTesting
    SpringLdapPooledContextSourceProvider(LdapContextSourceFactory ldapContextSourceFactory, ConcurrentMap<Long, ContextSourceEntry> contextSources, EventPublisher eventPublisher, LdapDirectoryClearingClusterEventPublisher ldapDirectoryClearingClusterEventPublisher, LdapConnectionPropertiesDiffResultMapper diffResultMapper) {
        this.ldapContextSourceFactory = ldapContextSourceFactory;
        this.contextSources = contextSources;
        this.eventPublisher = eventPublisher;
        this.ldapDirectoryClearingClusterEventPublisher = ldapDirectoryClearingClusterEventPublisher;
        this.diffResultMapper = diffResultMapper;
    }

    ContextSource getContextSource(long directoryId, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        return this.contextSources.compute(directoryId, (id, existingEntry) -> this.mergeEntries(directoryId, (ContextSourceEntry)existingEntry, ldapPropertiesMapper, envProperties)).contextSource;
    }

    private ContextSourceEntry mergeEntries(long directoryId, ContextSourceEntry existingEntry, LDAPPropertiesMapper ldapConnectionProperties, Map<String, Object> envProperties) {
        if (this.shouldRetainExistingEntry(directoryId, existingEntry, ldapConnectionProperties, envProperties)) {
            return existingEntry;
        }
        logger.info("Allocating new Dynamic LDAP pool for directory {}", (Object)directoryId);
        PooledContextSource contextSource = this.ldapContextSourceFactory.createPooledContextSource(directoryId, ldapConnectionProperties, envProperties);
        return new ContextSourceEntry(contextSource, ldapConnectionProperties, envProperties);
    }

    private boolean shouldRetainExistingEntry(long directoryId, ContextSourceEntry existingEntry, LDAPPropertiesMapper ldapPropertiesMapper, Map<String, Object> envProperties) {
        if (existingEntry != null) {
            DirectoryAttributesAndPropertiesDifference attributesAndPropertiesDiff = this.calculateDirectoryAttributesAndPropertiesDifference(existingEntry, ldapPropertiesMapper, envProperties);
            if (attributesAndPropertiesDiff.arePoolPropertiesEqual()) {
                return true;
            }
            this.removeContextSource(directoryId, existingEntry, LdapPoolDestroyedReason.COMMONS_POOL2_PROPERTIES_CHANGED, false);
            String ldapConnectionAttributesDiff = attributesAndPropertiesDiff.ldapConnectionAttributesDiff();
            String envPropertiesDiff = attributesAndPropertiesDiff.envPropertiesDiff();
            logger.trace("Configuration changes:\n Ldap Connection Attributes: {}\n Properties: {}", (Object)ldapConnectionAttributesDiff, (Object)envPropertiesDiff);
        }
        return false;
    }

    private DirectoryAttributesAndPropertiesDifference calculateDirectoryAttributesAndPropertiesDifference(ContextSourceEntry existingEntry, LDAPPropertiesMapper attributes, Map<String, Object> envProperties) {
        return new DirectoryAttributesAndPropertiesDifference(this.diffResultMapper, existingEntry, attributes, envProperties);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
        Exception lastException = null;
        for (ContextSourceEntry value : this.contextSources.values()) {
            try {
                value.contextSource.destroy();
            }
            catch (Exception e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    @Override
    public Map<Long, SpringLdapPoolStatistics> getPoolStatistics() {
        return this.contextSources.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> SpringLdapPoolStatistics.fromPool(((ContextSourceEntry)entry.getValue()).contextSource)));
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onDirectoryDeleted(DirectoryDeletedEvent e) {
        if (LDAP_DIRECTORY_TYPES.contains(e.getDirectoryType())) {
            this.removeContextSource(e.getDirectoryId(), LdapPoolDestroyedReason.DIRECTORY_DELETED, true);
        }
    }

    @EventListener
    public void onDirectoryUpdated(DirectoryUpdatedEvent e) {
        Directory oldDirectory = e.getOldDirectory();
        if (oldDirectory != null) {
            Optional<LdapPoolDestroyedReason> removeContextSourceReason = SpringLdapPooledContextSourceProvider.shouldRemoveContextSource(e, oldDirectory);
            if (LDAP_DIRECTORY_TYPES.contains(e.getDirectoryType()) && removeContextSourceReason.isPresent()) {
                this.removeContextSource(e.getDirectoryId(), removeContextSourceReason.get(), true);
            }
        }
    }

    private static Optional<LdapPoolDestroyedReason> shouldRemoveContextSource(DirectoryUpdatedEvent e, Directory oldDirectory) {
        if (oldDirectory.isActive() && !e.getDirectory().isActive()) {
            return Optional.of(LdapPoolDestroyedReason.DIRECTORY_DEACTIVATED);
        }
        if (SpringLdapPooledContextSourceProvider.getLdapPoolType(oldDirectory) == LdapPoolType.COMMONS_POOL2 && SpringLdapPooledContextSourceProvider.getLdapPoolType(e.getDirectory()) != LdapPoolType.COMMONS_POOL2) {
            return Optional.of(LdapPoolDestroyedReason.COMMONS_POOL2_DISABLED);
        }
        return Optional.empty();
    }

    public void removeContextSource(Long directoryId, LdapPoolDestroyedReason reason, boolean propagateClusterMessage) {
        ContextSourceEntry existingEntry = (ContextSourceEntry)this.contextSources.remove(directoryId);
        this.removeContextSource(directoryId, existingEntry, reason, propagateClusterMessage);
    }

    private void removeContextSource(Long directoryId, ContextSourceEntry existingEntry, LdapPoolDestroyedReason reason, boolean propagateClusterMessage) {
        if (existingEntry != null) {
            try {
                existingEntry.contextSource.destroy();
                logger.info("Dynamic LDAP connection pool context source has been destroyed for directory {}: {}", (Object)directoryId, (Object)reason.message);
            }
            catch (Exception e) {
                logger.warn("Exception caught when removing context source for directory {}", (Object)directoryId, (Object)e);
            }
        }
        if (propagateClusterMessage) {
            this.ldapDirectoryClearingClusterEventPublisher.publishEvent(directoryId);
        }
    }

    private static LdapPoolType getLdapPoolType(Directory oldDirectory) {
        return LdapPoolType.fromString((String)oldDirectory.getAttributes().get("ldap.pool.type"));
    }

    private static class DirectoryAttributesAndPropertiesDifference {
        private static final String DIFF_RESULT_DEFAULT_STRING = "differs from";
        private static final String DIFF_RESULT_TRANSITION_STRING = "were changed to";
        private final DiffResult<Map<String, Object>> envPropertiesDiff;
        private final DiffResult<LDAPPropertiesMapper> ldapConnectionAttributesDiff;

        private DirectoryAttributesAndPropertiesDifference(@Nonnull LdapConnectionPropertiesDiffResultMapper diffResultMapper, @Nonnull ContextSourceEntry existingEntry, @Nonnull LDAPPropertiesMapper attributes, @Nonnull Map<String, Object> envProperties) {
            this.envPropertiesDiff = MapDiff.diff((Map)existingEntry.envProperties, envProperties);
            this.ldapConnectionAttributesDiff = diffResultMapper.getConnectionPropertiesDifference(existingEntry.attributes, attributes);
        }

        private boolean arePoolPropertiesEqual() {
            return this.envPropertiesDiff.getNumberOfDiffs() == 0 && this.ldapConnectionAttributesDiff.getNumberOfDiffs() == 0;
        }

        public String envPropertiesDiff() {
            return this.envPropertiesDiff.toString().replace(DIFF_RESULT_DEFAULT_STRING, DIFF_RESULT_TRANSITION_STRING);
        }

        public String ldapConnectionAttributesDiff() {
            return this.ldapConnectionAttributesDiff.toString().replace(DIFF_RESULT_DEFAULT_STRING, DIFF_RESULT_TRANSITION_STRING);
        }
    }

    @VisibleForTesting
    static class ContextSourceEntry {
        private final PooledContextSource contextSource;
        private final LDAPPropertiesMapper attributes;
        private final Map<String, Object> envProperties;

        ContextSourceEntry(PooledContextSource contextSource, LDAPPropertiesMapper attributes, Map<String, Object> envProperties) {
            this.contextSource = contextSource;
            this.attributes = attributes;
            this.envProperties = envProperties;
        }
    }

    public static enum LdapPoolDestroyedReason {
        COMMONS_POOL2_PROPERTIES_CHANGED("Dynamic LDAP pool properties have changed"),
        COMMONS_POOL2_DISABLED("Dynamic LDAP pool has been disabled for directory"),
        DIRECTORY_DEACTIVATED("Directory was deactivated"),
        DIRECTORY_DELETED("Directory was deleted"),
        REQUESTED_BY_ANOTHER_NODE("Dynamic LDAP pool removal was requested by another node");

        private final String message;

        private LdapPoolDestroyedReason(String message) {
            this.message = message;
        }
    }
}

