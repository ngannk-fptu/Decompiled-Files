/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Sets
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public final class HibernateMappingResourcesFactory {
    private final Set<Resource> commonResources = Set.of(new ClassPathResource("com/atlassian/confluence/core/ContentEntityObject.hbm.xml"), new ClassPathResource("com/atlassian/confluence/core/BodyContent.hbm.xml"), new ClassPathResource("com/atlassian/confluence/links/OutgoingLink.hbm.xml"), new ClassPathResource("com/atlassian/confluence/mail/notification/Notification.hbm.xml"), new ClassPathResource("com/atlassian/confluence/pages/AttachmentQueries.hbm.xml"), new ClassPathResource("com/atlassian/confluence/pages/templates/PageTemplate.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/SpacePermission.hbm.xml"), new ClassPathResource("com/atlassian/confluence/spaces/Space.hbm.xml"), new ClassPathResource("com/atlassian/confluence/labels/Label.hbm.xml"), new ClassPathResource("com/atlassian/confluence/labels/MostUsedLabelsCacheRecord.hbm.xml"), new ClassPathResource("com/atlassian/confluence/user/PropertySetItem.hbm.xml"), new ClassPathResource("com/atlassian/confluence/user/RememberMeToken.hbm.xml"), new ClassPathResource("com/atlassian/confluence/pages/AttachmentData.hbm.xml"), new ClassPathResource("com/atlassian/confluence/pages/attachments/ImageDetailsDto.hbm.xml"), new ClassPathResource("com/atlassian/confluence/setup/bandana/ConfluenceBandanaRecord.hbm.xml"), new ClassPathResource("com/atlassian/confluence/core/PersistentDecorator.hbm.xml"), new ClassPathResource("com/atlassian/confluence/plugin/persistence/hibernate/PluginData.hbm.xml"), new ClassPathResource("com/atlassian/confluence/core/VersionHistory.hbm.xml"), new ClassPathResource("com/atlassian/confluence/core/ClusterSafetyNumber.hbm.xml"), new ClassPathResource("com/atlassian/confluence/core/ZduStatusEntity.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/ContentPermission.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/ContentPermissionSet.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/KeyStore.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/TrustedApplication.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/TrustedApplicationRestriction.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContent.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContentViewPermission.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedLock.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedServiceStateRecord.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSid.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSpacePermission.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedStateChangeLog.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/backgroundjob/ArchivedBackgroundJob.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/backgroundjob/BackgroundJob.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/backuprestore/BackupRestoreJob.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/backuprestore/BackupRestoreJobSettings.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/backuprestore/BackupRestoreJobStatistics.hbm.xml"), new ClassPathResource("com/atlassian/confluence/impl/search/v2/lucene/filter/SpacePermissionsFilter.hbm.xml"), new ClassPathResource("com/atlassian/confluence/follow/Connection.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateApplication.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateDirectory.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateDirectorySynchronisationStatus.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateDirectorySynchronisationToken.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateGroup.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateMembership.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateTombstone.hbm.xml"), new ClassPathResource("com/atlassian/crowd/embedded/hibernate2/HibernateUser.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/UserLoginInfo.hbm.xml"), new ClassPathResource("com/atlassian/confluence/like/Like.hbm.xml"), new ClassPathResource("com/atlassian/confluence/user/ConfluenceUser.hbm.xml"), new ClassPathResource("com/atlassian/confluence/relations/RelationEntity.hbm.xml"), new ClassPathResource("com/atlassian/confluence/schedule/service/Scheduler.hbm.xml"), new ClassPathResource("com/atlassian/confluence/audit/AuditRecord.hbm.xml"), new ClassPathResource("com/atlassian/synchrony/Events.hbm.xml"), new ClassPathResource("com/atlassian/synchrony/Secrets.hbm.xml"), new ClassPathResource("com/atlassian/synchrony/Snapshots.hbm.xml"), new ClassPathResource("com/atlassian/confluence/internal/diagnostics/AlertEntity.hbm.xml"), new ClassPathResource("com/atlassian/confluence/security/legacy/LegacySecurityMappings.hbm.xml"));
    private final HibernateDatabaseCapabilities databaseCapabilities;

    public HibernateMappingResourcesFactory(HibernateDatabaseCapabilities databaseCapabilities) {
        this.databaseCapabilities = Objects.requireNonNull(databaseCapabilities);
    }

    public Collection<Resource> hibernateMappingResources() {
        return Sets.union(this.commonResources, this.buildCapabilitySpecificMappings());
    }

    private Set<Resource> buildCapabilitySpecificMappings() {
        ImmutableSet.Builder mappings = ImmutableSet.builder();
        if (this.databaseCapabilities.supportsIdentityColumns()) {
            mappings.add((Object)new ClassPathResource("com/atlassian/confluence/journal/JournalEntry-identity.hbm.xml"));
        } else if (this.databaseCapabilities.supportsSequences() && this.databaseCapabilities.isOracle()) {
            mappings.add((Object)new ClassPathResource("com/atlassian/confluence/journal/JournalEntry-oracle.hbm.xml"));
        } else if (this.databaseCapabilities.supportsSequences()) {
            mappings.add((Object)new ClassPathResource("com/atlassian/confluence/journal/JournalEntry-sequence.hbm.xml"));
        } else {
            throw new IllegalStateException("Cannot determine DB type");
        }
        mappings.add((Object)new ClassPathResource("com/atlassian/confluence/journal/JournalEntry-common.hbm.xml"));
        mappings.add((Object)this.getDenormalisedSpaceChangeLogMapping());
        mappings.add((Object)this.getDenormalisedContentChangeLogMapping());
        return mappings.build();
    }

    private Resource getDenormalisedSpaceChangeLogMapping() {
        return this.databaseCapabilities.isOracle() ? new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSpaceChangeLogSequence.hbm.xml") : new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedSpaceChangeLogIdentity.hbm.xml");
    }

    private Resource getDenormalisedContentChangeLogMapping() {
        return this.databaseCapabilities.isOracle() ? new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContentChangeLogSequence.hbm.xml") : new ClassPathResource("com/atlassian/confluence/security/denormalisedpermissions/DenormalisedContentChangeLogIdentity.hbm.xml");
    }
}

