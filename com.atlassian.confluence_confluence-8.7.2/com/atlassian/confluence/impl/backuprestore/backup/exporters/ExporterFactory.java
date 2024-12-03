/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.tombstone.AbstractTombstone
 *  com.atlassian.crowd.model.tombstone.UserMembershipTombstone
 *  com.atlassian.crowd.model.tombstone.UserTombstone
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.BandanaDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.BodyContentDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.BucketsExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonPersister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Content2ContentRelationEntityDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ContentEntityDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ContentPropertyDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.EmptyPostExportAction;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.LabellingExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.NotificationDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.PageTemplateExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ReferenceEntityFromBodyContentExtractor;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SimpleEntityExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SingleDependencyGenericExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SpaceDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AliasedKeyDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.CommonDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Content2ContentRelationEntityDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.ContentEntityDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.EntityWithAdditionalDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.AllowedOperationsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.AttributesEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.ExportObjectsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.UserEmailEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.BucketPropertySetItemQueryProvider;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.ContentEntityPostExportAction;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.GenericSiteExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.SingleColumnQueryProvider;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.SingleColumnWithDiscriminatorQueryProvider;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.pages.Page;
import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.atlassian.crowd.model.tombstone.UserMembershipTombstone;
import com.atlassian.crowd.model.tombstone.UserTombstone;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExporterFactory {
    private final DatabaseExporterHelper helper;
    private final StatisticsCollector statisticsCollector;

    public ExporterFactory(DatabaseExporterHelper databaseExporterHelper, StatisticsCollector statisticsCollector) {
        this.helper = databaseExporterHelper;
        this.statisticsCollector = statisticsCollector;
    }

    public GenericSiteExporter createGenericSiteExporter(ExportableEntityInfo entityInfo) {
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    public SingleDependencyGenericExporter createSingleDependencyGenericSiteExporter(ExportableEntityInfo entityInfo) {
        return this.createSingleDependencyGenericExporter(entityInfo, Collections.emptySet());
    }

    public GenericSiteExporter createBucketPropertySetItemsExporter(ExportableEntityInfo entityInfo) {
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new GenericSiteExporter(this.helper, converter, new BucketPropertySetItemQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    public GenericSiteExporter createContentSiteExporter(List<ExportableEntityInfo> allContentEntityInfos, boolean includeAttachments) {
        ContentEntityDatabaseDataConverter converter = new ContentEntityDatabaseDataConverter(allContentEntityInfos, this.helper.getHibernateMetadataHelper(), this.statisticsCollector);
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, converter.getEntityInfo(Page.class)), new ContentEntityPostExportAction(this.helper, includeAttachments));
    }

    public ContentPropertyDataExporter createContentPropertySpaceExporter(ExportableEntityInfo entityInfo, boolean keepCollectionsForContentProperties) {
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new ContentPropertyDataExporter(new CommonDatabaseDataExporter(converter, this.helper, new CommonPersister()), keepCollectionsForContentProperties);
    }

    public Exporter createContentEntitySpaceExporter(List<ExportableEntityInfo> allContentEntityInfos, ContentPropertyDataExporter contentPropertyDataExporter, boolean keepCollectionsForContentProperties) {
        return new ContentEntityDatabaseDataExporter(this.helper, keepCollectionsForContentProperties, contentPropertyDataExporter, new ContentEntityDatabaseDataConverter(allContentEntityInfos, this.helper.getHibernateMetadataHelper(), this.statisticsCollector), new CommonPersister());
    }

    public Exporter createPageTemplateExporter(ExportableEntityInfo entityInfo) {
        return new PageTemplateExporter(entityInfo, new CommonDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper, new CommonPersister()));
    }

    public Exporter createSimpleEntityExporter(ExportableEntityInfo entityInfo) {
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new SimpleEntityExporter(new CommonDatabaseDataExporter(converter, this.helper, new CommonPersister()));
    }

    public Exporter createLabellingExporter(ExportableEntityInfo entityInfo) {
        return new LabellingExporter(entityInfo, new CommonDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper, new CommonPersister()));
    }

    public Exporter createSpaceDatabaseDataExporter(ExportableEntityInfo entityInfo) {
        return new SpaceDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper);
    }

    public Exporter createNotificationDatabaseDataExporter(ExportableEntityInfo entityInfo) {
        return new NotificationDatabaseDataExporter(entityInfo, new CommonDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper, new CommonPersister()));
    }

    public SingleDependencyGenericExporter createSingleDependencyGenericExporter(ExportableEntityInfo entityInfo, Set<Class<?>> simpleEntitiesClasses) {
        return new SingleDependencyGenericExporter(new CommonDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper, new CommonPersister()), entityInfo, simpleEntitiesClasses);
    }

    public Exporter createBodyContentExporter(ExportableEntityInfo entityInfo, ReferenceEntityFromBodyContentExtractor referenceEntityFromBodyContentExtractor) {
        return new BodyContentDataExporter(this.helper, this.createCommonDatabaseConverter(entityInfo), referenceEntityFromBodyContentExtractor);
    }

    public Exporter createConfluenceUserExporter(ExportableEntityInfo entityInfo) {
        UserEmailEnrichment userEmailEnrichment = new UserEmailEnrichment(this.helper);
        EntityWithAdditionalDataConverter converter = this.createConverterWithAdditionalData(entityInfo, Collections.singletonList(userEmailEnrichment));
        return new SimpleEntityExporter(new CommonDatabaseDataExporter(converter, this.helper, new CommonPersister()));
    }

    public Exporter createBandanaDataExporter(ExportableEntityInfo entityInfo) {
        return new BandanaDataExporter(entityInfo, new CommonDatabaseDataExporter(this.createCommonDatabaseConverter(entityInfo), this.helper, new CommonPersister()));
    }

    public Exporter createContent2ContentRelationEntityExporter(ExportableEntityInfo entityInfo) {
        return new Content2ContentRelationEntityDataExporter(entityInfo, new CommonDatabaseDataExporter(new Content2ContentRelationEntityDataConverter(entityInfo, this.helper.getHibernateMetadataHelper(), this.statisticsCollector), this.helper, new CommonPersister()));
    }

    public Exporter createBucketsExporter(ExportableEntityInfo entityInfo) {
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new BucketsExporter(new CommonDatabaseDataExporter(converter, this.helper, new CommonPersister()));
    }

    public GenericSiteExporter createAliasedKeySiteExporter(ExportableEntityInfo entityInfo) {
        AliasedKeyDatabaseDataConverter converter = new AliasedKeyDatabaseDataConverter(entityInfo, this.helper.getHibernateMetadataHelper(), this.statisticsCollector);
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    public GenericSiteExporter createDirectoryExporter(ExportableEntityInfo entityInfo) {
        AllowedOperationsEnrichment allowedOperationsEnrichment = new AllowedOperationsEnrichment(this.helper, "cwd_directory_operation", "directory_id");
        AttributesEnrichment attributesEnrichment = new AttributesEnrichment(this.helper, "cwd_directory_attribute", "directory_id");
        EntityWithAdditionalDataConverter converter = this.createConverterWithAdditionalData(entityInfo, List.of(allowedOperationsEnrichment, attributesEnrichment));
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    public Exporter createDirectoryMappingExporter(ExportableEntityInfo entityInfo) {
        AllowedOperationsEnrichment allowedOperationsEnrichment = new AllowedOperationsEnrichment(this.helper, "cwd_app_dir_operation", "app_dir_mapping_id");
        EntityWithAdditionalDataConverter converter = this.createConverterWithAdditionalData(entityInfo, List.of(allowedOperationsEnrichment));
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    public GenericSiteExporter createApplicationExporter(ExportableEntityInfo entityInfo) {
        AttributesEnrichment attributesEnrichment = new AttributesEnrichment(this.helper, "cwd_application_attribute", "application_id");
        EntityWithAdditionalDataConverter converter = this.createConverterWithAdditionalData(entityInfo, List.of(attributesEnrichment));
        return new GenericSiteExporter(this.helper, converter, new SingleColumnQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }

    private CommonDatabaseDataConverter createCommonDatabaseConverter(ExportableEntityInfo entityInfo) {
        return new CommonDatabaseDataConverter(entityInfo, this.helper.getHibernateMetadataHelper(), this.statisticsCollector);
    }

    private EntityWithAdditionalDataConverter createConverterWithAdditionalData(ExportableEntityInfo entityInfo, List<ExportObjectsEnrichment> fieldConverters) {
        return new EntityWithAdditionalDataConverter(this.createCommonDatabaseConverter(entityInfo), fieldConverters);
    }

    public Exporter createTombstoneExporter(ExportableEntityInfo entityInfo) {
        Set<Class<AbstractTombstone>> forbiddenClasses = Set.of(UserTombstone.class, UserMembershipTombstone.class, AbstractTombstone.class);
        if (forbiddenClasses.contains(entityInfo.getEntityClass())) {
            return null;
        }
        CommonDatabaseDataConverter converter = this.createCommonDatabaseConverter(entityInfo);
        return new GenericSiteExporter(this.helper, converter, new SingleColumnWithDiscriminatorQueryProvider(this.helper, entityInfo), new EmptyPostExportAction());
    }
}

