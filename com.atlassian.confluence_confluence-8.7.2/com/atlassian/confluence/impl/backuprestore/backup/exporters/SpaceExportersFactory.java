/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ContentPropertyDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterCreatorHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ReferenceEntityFromBodyContentExtractor;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SimpleEntityExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SingleDependencyGenericExporter;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.internal.relations.dao.Content2ContentRelationEntity;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaRecord;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceExportersFactory {
    private static final Logger log = LoggerFactory.getLogger(SpaceExportersFactory.class);
    private final ReferenceEntityFromBodyContentExtractor referenceEntityFromBodyContentExtractor;
    private Set<Exporter> allExporters;
    private Set<Class<?>> simpleEntitiesClasses;

    public SpaceExportersFactory(ReferenceEntityFromBodyContentExtractor referenceEntityFromBodyContentExtractor) {
        this.referenceEntityFromBodyContentExtractor = referenceEntityFromBodyContentExtractor;
    }

    public Set<Exporter> createExporters(DatabaseExporterHelper databaseExporterHelper, ExporterFactory exporterFactory, boolean keepCollectionsForContentProperties) {
        this.allExporters = new HashSet<Exporter>();
        this.simpleEntitiesClasses = new HashSet();
        HashSet<ExportableEntityInfo> exportableEntitiesToProcess = new HashSet<ExportableEntityInfo>(databaseExporterHelper.getHibernateMetadataHelper().getAllExportableEntities().values());
        ContentPropertyDataExporter contentPropertyDataExporter = this.createContentPropertyExporter(exporterFactory, exportableEntitiesToProcess, keepCollectionsForContentProperties);
        int iterationNumber = 0;
        while (true) {
            int numberOfEntitiesToProcessBeforeIteration = exportableEntitiesToProcess.size();
            this.createSimpleExporters(exporterFactory, exportableEntitiesToProcess);
            if (exportableEntitiesToProcess.isEmpty() || numberOfEntitiesToProcessBeforeIteration == exportableEntitiesToProcess.size()) break;
            log.debug("Iteration N {} of creating simple data exporters finished, created {} simple data exporters, entities {} are pending", new Object[]{iterationNumber++, numberOfEntitiesToProcessBeforeIteration - exportableEntitiesToProcess.size(), exportableEntitiesToProcess.size()});
        }
        log.debug("{} simple data exporters have been created.", (Object)this.allExporters.size());
        this.createContentEntityExporter(exporterFactory, exportableEntitiesToProcess, keepCollectionsForContentProperties, contentPropertyDataExporter);
        this.createOtherExporters(exporterFactory, exportableEntitiesToProcess);
        if (exportableEntitiesToProcess.isEmpty()) {
            log.debug("Success: {} data exporters for all entities have been created.", (Object)this.allExporters.size());
        } else {
            log.warn("Data exporters were not created for {} exportable entities", (Object)exportableEntitiesToProcess.size());
            for (ExportableEntityInfo entityInfo : exportableEntitiesToProcess) {
                log.warn("Data exporter was not created for entity {}", (Object)entityInfo);
            }
        }
        return this.allExporters;
    }

    private ContentPropertyDataExporter createContentPropertyExporter(ExporterFactory exporterFactory, Set<ExportableEntityInfo> exportableEntitiesToProcess, boolean keepCollectionsForContentProperties) {
        Iterator<ExportableEntityInfo> entitiesIterator = exportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (!entityInfo.getEntityClass().equals(ContentProperty.class)) continue;
            entitiesIterator.remove();
            ContentPropertyDataExporter contentPropertyDataExporter = exporterFactory.createContentPropertySpaceExporter(entityInfo, keepCollectionsForContentProperties);
            this.allExporters.add(contentPropertyDataExporter);
            this.simpleEntitiesClasses.add(entityInfo.getEntityClass());
            return contentPropertyDataExporter;
        }
        throw new IllegalStateException("ContentProperty entity has not been found. Unable to create an exporter for it.");
    }

    private void createSimpleExporters(ExporterFactory exporterFactory, Set<ExportableEntityInfo> exportableEntitiesToProcess) {
        Iterator<ExportableEntityInfo> entitiesIterator = exportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (entityInfo.getEntityClass().equals(BucketPropertySetItem.class)) {
                this.allExporters.add(exporterFactory.createBucketsExporter(entityInfo));
                this.simpleEntitiesClasses.add(entityInfo.getEntityClass());
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(ConfluenceUserImpl.class)) {
                this.allExporters.add(exporterFactory.createConfluenceUserExporter(entityInfo));
                this.simpleEntitiesClasses.add(entityInfo.getEntityClass());
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(ConfluenceBandanaRecord.class)) {
                this.allExporters.add(exporterFactory.createBandanaDataExporter(entityInfo));
                this.simpleEntitiesClasses.add(entityInfo.getEntityClass());
                entitiesIterator.remove();
                continue;
            }
            if (!SimpleEntityExporter.isSuitableForExporter(entityInfo, this.simpleEntitiesClasses)) continue;
            this.allExporters.add(exporterFactory.createSimpleEntityExporter(entityInfo));
            this.simpleEntitiesClasses.add(entityInfo.getEntityClass());
            entitiesIterator.remove();
        }
    }

    private void createOtherExporters(ExporterFactory exporterFactory, Set<ExportableEntityInfo> exportableEntitiesToProcess) {
        Iterator<ExportableEntityInfo> entitiesIterator = exportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (entityInfo.getEntityClass().equals(Space.class)) {
                this.allExporters.add(exporterFactory.createSpaceDatabaseDataExporter(entityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(Notification.class)) {
                this.allExporters.add(exporterFactory.createNotificationDatabaseDataExporter(entityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(Labelling.class)) {
                this.allExporters.add(exporterFactory.createLabellingExporter(entityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(Content2ContentRelationEntity.class)) {
                this.allExporters.add(exporterFactory.createContent2ContentRelationEntityExporter(entityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(PageTemplate.class)) {
                this.allExporters.add(exporterFactory.createPageTemplateExporter(entityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(BodyContent.class)) {
                this.allExporters.add(exporterFactory.createBodyContentExporter(entityInfo, this.referenceEntityFromBodyContentExtractor));
                entitiesIterator.remove();
                continue;
            }
            if (!SingleDependencyGenericExporter.isSuitableForExporter(entityInfo, this.simpleEntitiesClasses)) continue;
            this.allExporters.add(exporterFactory.createSingleDependencyGenericExporter(entityInfo, this.simpleEntitiesClasses));
            entitiesIterator.remove();
        }
    }

    private void createContentEntityExporter(ExporterFactory exporterFactory, Set<ExportableEntityInfo> exportableEntities, boolean keepCollectionsForContentProperties, ContentPropertyDataExporter contentPropertyDataExporter) {
        List<ExportableEntityInfo> allContentEntityInfos = ExporterCreatorHelper.cutAllContentEntityInfos(exportableEntities);
        if (allContentEntityInfos.isEmpty()) {
            throw new IllegalStateException("Content entity has not been found. Unable to create an exporter for it.");
        }
        this.allExporters.add(exporterFactory.createContentEntitySpaceExporter(allContentEntityInfos, contentPropertyDataExporter, keepCollectionsForContentProperties));
    }
}

