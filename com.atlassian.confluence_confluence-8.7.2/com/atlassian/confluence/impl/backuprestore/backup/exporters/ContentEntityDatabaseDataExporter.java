/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.CommonPersister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ContentPropertyDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Persister;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Subscriber;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.AbstractDatabaseDataConverter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.Converter;
import com.atlassian.confluence.impl.backuprestore.backup.models.AttachmentInfo;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentEntityDatabaseDataExporter
implements Persister,
Exporter,
Subscriber {
    public static final String CONTENT_IDS = "contentIds";
    private static final Logger log = LoggerFactory.getLogger(ContentEntityDatabaseDataExporter.class);
    private final AtomicInteger batchCounter = new AtomicInteger();
    private final boolean keepCollectionsForContentProperties;
    private final ContentPropertyDataExporter contentPropertyDataExporter;
    private final Converter converter;
    private final DatabaseExporterHelper helper;
    private final CommonPersister commonPersister;
    private static final String CONTENT_BY_PARENT_ID_QUERY = "SELECT * FROM CONTENT WHERE PARENTID IN (:contentIds) AND PREVVER IS NULL";
    private static final String TOP_LEVEL_CONTENT_BY_ID_QUERY = "SELECT * FROM CONTENT WHERE CONTENTID IN (:contentIds) AND PARENTID IS NULL AND PREVVER IS NULL AND PAGEID IS NULL AND SPACEID IS NULL";
    private static final String TOP_LEVEL_CONTENT_BY_SPACE_ID_QUERY = "SELECT * FROM CONTENT WHERE SPACEID = :spaceId AND PARENTID IS NULL AND PREVVER IS NULL AND PAGEID IS NULL";
    private static final String TOP_LEVEL_CONTENT_BY_CONTAINER_ID = "SELECT * FROM CONTENT WHERE PAGEID IN (:contentIds) AND PARENTCOMMENTID IS NULL AND PREVVER IS NULL";
    private static final String COMMENT_BY_COMMENTPARENT_IDS_QUERY = "SELECT * FROM CONTENT WHERE PARENTCOMMENTID IN (:contentIds) AND PREVVER IS NULL";
    private static final String HISTORICAL_VERSIONS_QUERY = "SELECT * FROM CONTENT WHERE PREVVER IN (:contentIds)";

    public ContentEntityDatabaseDataExporter(DatabaseExporterHelper databaseExporterHelper, boolean keepCollectionsForContentProperties, ContentPropertyDataExporter contentPropertyDataExporter, Converter databaseDataConverter, CommonPersister commonPersister) {
        this.keepCollectionsForContentProperties = keepCollectionsForContentProperties;
        this.contentPropertyDataExporter = contentPropertyDataExporter;
        this.converter = databaseDataConverter;
        this.helper = databaseExporterHelper;
        this.commonPersister = commonPersister;
    }

    @Override
    public Collection<Class<?>> getWatchingEntityClasses() {
        return Collections.singletonList(Space.class);
    }

    @Override
    public ExportableEntityInfo getEntityInfo() {
        throw new IllegalArgumentException("Content entity extractor does not support getting default entity. The entity class has to be provided");
    }

    @Override
    public ExportableEntityInfo getEntityInfo(Class<?> exportedClass) {
        return this.converter.getEntityInfo(exportedClass);
    }

    @Override
    public void persistObjects(Collection<Object> objectIds) throws InterruptedException, BackupRestoreException {
        this.helper.runTaskAsync(() -> {
            try {
                log.debug("Starting CEO export for IDs: {}", (Object)objectIds);
                Collection longIdList = objectIds.stream().map(AbstractDatabaseDataConverter::convertToLong).collect(Collectors.toList());
                this.executeCEOExport(longIdList);
            }
            catch (InterruptedException e) {
                log.warn("Content entity data exporter was interrupted: {}", (Object)e.getMessage());
                Thread.currentThread().interrupt();
            }
            return null;
        }, "content entities export triggered by persistObjects call");
    }

    @Override
    public void onMonitoredObjectsExport(Class<?> exportedClass, Collection<Object> idList) {
        if (!exportedClass.equals(Space.class)) {
            throw new IllegalArgumentException("Content exporter listens to Space objects only, but received events from unexpected class: " + exportedClass);
        }
        this.helper.runTaskAsync(() -> {
            try {
                for (Object id : idList) {
                    this.exportAllContentEntitiesFromSpace(AbstractDatabaseDataConverter.convertToLong(id));
                }
            }
            catch (InterruptedException e) {
                log.warn("Content entity data exporter was interrupted: {}", (Object)e.getMessage());
                Thread.currentThread().interrupt();
            }
            return null;
        }, "content entities export triggered by " + exportedClass.getSimpleName());
    }

    private void executeCEOExport(Collection<Object> idList) throws InterruptedException, BackupRestoreException {
        log.debug("Start exporting content entity object with content ids");
        Set<Object> haveNotPersistedIdObjects = this.commonPersister.getPersistableObjects(idList);
        Collection haveNotPersistedIds = haveNotPersistedIdObjects.stream().map(AbstractDatabaseDataConverter::convertToLong).collect(Collectors.toList());
        Set<Long> processedContentIds = this.exportNonHistoricalVersionsRecursively(haveNotPersistedIds);
        log.debug("Exported {} non-historical content entity objects for CEO {}", (Object)processedContentIds.size(), idList);
        int numberOfHistoricalObjects = this.exportHistoricalVersions(processedContentIds).size();
        log.debug("Exported {} historical content entity objects for CEO {}", (Object)numberOfHistoricalObjects, idList);
    }

    private void exportAllContentEntitiesFromSpace(long spaceId) throws InterruptedException, BackupRestoreException {
        log.debug("Start exporting content entity objects for space with id {}", (Object)spaceId);
        Set<Long> processedContentIds = this.exportNonHistoricalVersionsRecursively(spaceId);
        log.debug("Exported {} non-historical content entity objects for space {}", (Object)processedContentIds.size(), (Object)spaceId);
        int numberOfHistoricalObjects = this.exportHistoricalVersions(processedContentIds).size();
        log.debug("Exported {} historical content entity objects for space {}", (Object)numberOfHistoricalObjects, (Object)spaceId);
    }

    private Collection<Long> exportHistoricalVersions(Set<Long> originalPageIds) throws InterruptedException, BackupRestoreException {
        return this.runQueryAndExportObjectsInBatches(HISTORICAL_VERSIONS_QUERY, CONTENT_IDS, originalPageIds, "historical version");
    }

    private Set<Long> exportNonHistoricalVersionsRecursively(Collection<Long> idList) throws InterruptedException, BackupRestoreException {
        Collection<Long> topLevelIdsToProcess = this.runQueryAndExportObjectsInBatches(TOP_LEVEL_CONTENT_BY_ID_QUERY, CONTENT_IDS, idList, "top level pages in by CEOs");
        return this.exportNonHistoricalVersionsRecursivelyForPages(topLevelIdsToProcess);
    }

    private Set<Long> exportNonHistoricalVersionsRecursively(long spaceId) throws InterruptedException, BackupRestoreException {
        Collection<Long> topLevelIdsToProcess = this.runQueryAndExportObjectsInBatches(TOP_LEVEL_CONTENT_BY_SPACE_ID_QUERY, "spaceId", Collections.singletonList(spaceId), "top level pages in the space");
        return this.exportNonHistoricalVersionsRecursivelyForPages(topLevelIdsToProcess);
    }

    private Set<Long> exportNonHistoricalVersionsRecursivelyForPages(Collection<Long> parentPageIdsToProcessParam) throws InterruptedException, BackupRestoreException {
        HashSet<Long> fullListOfProcessedContentIds = new HashSet<Long>();
        int protectionLevel = 1000;
        while (protectionLevel-- > 0) {
            if (parentPageIdsToProcessParam.isEmpty()) {
                return fullListOfProcessedContentIds;
            }
            fullListOfProcessedContentIds.addAll(parentPageIdsToProcessParam);
            parentPageIdsToProcessParam = this.findAndExportDependantContentObjects(parentPageIdsToProcessParam);
            log.debug("Processed next level of content. ProtectionLevel: {}, processed objects on this iteration: {} (of {} overall)", new Object[]{protectionLevel, parentPageIdsToProcessParam.size(), fullListOfProcessedContentIds.size()});
        }
        throw new IllegalStateException("Something went wrong or the children level is too high");
    }

    private List<Long> findAndExportDependantContentObjects(Collection<Long> pageIdsToProcess) throws InterruptedException, BackupRestoreException {
        ArrayList<Long> foundDependantsIds = new ArrayList<Long>();
        foundDependantsIds.addAll(this.runQueryAndExportObjectsInBatches(CONTENT_BY_PARENT_ID_QUERY, CONTENT_IDS, pageIdsToProcess, "children pages"));
        foundDependantsIds.addAll(this.runQueryAndExportObjectsInBatches(TOP_LEVEL_CONTENT_BY_CONTAINER_ID, CONTENT_IDS, pageIdsToProcess, "top level pages in containers"));
        foundDependantsIds.addAll(this.runQueryAndExportObjectsInBatches(COMMENT_BY_COMMENTPARENT_IDS_QUERY, CONTENT_IDS, pageIdsToProcess, "children comment"));
        return foundDependantsIds;
    }

    private Collection<Long> runQueryAndExportObjectsInBatches(String query, String fieldName, Collection<Long> ids, String reason) throws InterruptedException, BackupRestoreException {
        Stopwatch globalWatch = Stopwatch.createStarted();
        ArrayList<Long> processedObjectIds = new ArrayList<Long>();
        List partitions = Lists.partition(new ArrayList<Long>(ids), (int)this.helper.getRegularBatchSize());
        log.debug("Processing query content for {} ids, it is split to {} batches", (Object)ids.size(), (Object)partitions.size());
        for (List partition : partitions) {
            int batchNumber = this.batchCounter.getAndIncrement();
            log.trace("Start partition {} of {} with {} elements", new Object[]{batchNumber, partitions.size(), partition.size()});
            Stopwatch stopwatch = Stopwatch.createStarted();
            List extractedRawObjects = (List)this.helper.doInReadOnlyTransaction(tx -> this.helper.runQueryWithInCondition(query, fieldName, partition));
            List<EntityObjectReadyForExport> extractedObjects = this.converter.convertToObjectsReadyForSerialisation(extractedRawObjects);
            List<EntityObjectReadyForExport> extractedObjectsWasNotExportedBefore = this.excludeAlreadyProcessedObjects(extractedObjects);
            if (this.keepCollectionsForContentProperties) {
                this.writeObjectsWithContentPropertiesInLegacyFormat(extractedObjectsWasNotExportedBefore);
            } else {
                this.helper.writeObjectsAndNotifyOtherExporters(extractedObjectsWasNotExportedBefore);
                this.writeAttachments(extractedObjectsWasNotExportedBefore);
            }
            processedObjectIds.addAll(extractedObjectsWasNotExportedBefore.stream().map(x -> AbstractDatabaseDataConverter.convertToLong(x.getId().getValue())).collect(Collectors.toList()));
            log.trace("Finish partition {} of {} with {} elements. Extracted {} objects, Duration: {}", new Object[]{batchNumber, partitions.size(), partition.size(), extractedObjects.size(), stopwatch});
        }
        log.debug("Finished processing query content for {} ids. Processed objects: {}. Duration: {}", new Object[]{ids.size(), processedObjectIds.size(), globalWatch});
        return processedObjectIds;
    }

    private void writeObjectsWithContentPropertiesInLegacyFormat(List<EntityObjectReadyForExport> contentEntityObjects) throws InterruptedException, BackupRestoreException {
        Set contentEntityObjectIdsToExport = contentEntityObjects.stream().map(o -> o.getId().getLongValue()).collect(Collectors.toSet());
        Map<Long, List<EntityObjectReadyForExport>> allContentProperties = this.contentPropertyDataExporter.findContentPropertiesForContentEntityObjects(contentEntityObjectIdsToExport);
        for (EntityObjectReadyForExport object : contentEntityObjects) {
            Long contentEntityObjectId = object.getId().getLongValue();
            Collection contentProperties = allContentProperties.get(contentEntityObjectId);
            if (contentProperties != null) {
                object.addCollectionOfElements(new EntityObjectReadyForExport.CollectionOfElements("contentProperties", Collection.class, ContentProperty.class, contentProperties.stream().map(p -> p.getId().getValue()).collect(Collectors.toSet())));
            }
            this.writeObjects(Collections.singleton(object));
            if (contentProperties != null) {
                this.writeObjects(contentProperties);
            }
            this.writeAttachments(Collections.singleton(object));
        }
        this.helper.writeAllReferencedSimpleObjects(contentEntityObjects);
        this.helper.notifyExportersAboutPersistedObjects(contentEntityObjects);
    }

    private List<EntityObjectReadyForExport> excludeAlreadyProcessedObjects(List<EntityObjectReadyForExport> extractedObjects) {
        Collection objectIdsToBeExported = extractedObjects.stream().map(o -> o.getId()).collect(Collectors.toList());
        Set<Object> unexportedObjectIds = this.commonPersister.getPersistableObjects(objectIdsToBeExported);
        return extractedObjects.stream().filter(o -> unexportedObjectIds.contains(o.getId())).collect(Collectors.toList());
    }

    private void writeObjects(Collection<EntityObjectReadyForExport> entities) throws BackupRestoreException {
        this.helper.writeObjects(entities);
    }

    private void writeAttachments(Collection<EntityObjectReadyForExport> entities) {
        List<AttachmentInfo> attachmentInfos = entities.stream().filter(entity -> entity.getClazz().equals(Attachment.class)).map(entity -> new AttachmentInfo((EntityObjectReadyForExport)entity)).collect(Collectors.toList());
        this.helper.getContainerWriter().addAttachments(attachmentInfos);
    }
}

