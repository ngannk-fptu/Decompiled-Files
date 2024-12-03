/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AncestorRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ApplicationPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AttributeRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.BodyContentPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ContentPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ContentPersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ContentPropertiesPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.DirectoryMappingPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.DirectoryPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.GenericPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.InternalUserCredentialRecordPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ListIndexColumnValueCalculator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectQueueProcessor;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.OperationRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PageTemplatePersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PageTemplatePersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.SpacePersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsFactory;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsHolder;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.importexport.impl.ContentUserKeyExtractor;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityPersistersFactory {
    private static final Logger log = LoggerFactory.getLogger(EntityPersistersFactory.class);
    private final ObjectPersister databaseOnlyPersister;
    private final ObjectPersister contentEntitiesPersister;
    private final ImportedObjectsStashFactory importedObjectsStashFactory;
    private final IdMapper idMapper;
    private final DeferredActionsHolder deferredActionsHolder;
    private final DeferredActionsFactory deferredActionsFactory;
    private final ContentUserKeyExtractor contentUserKeyExtractor;
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;
    private final PersisterHelper persisterHelper;

    public EntityPersistersFactory(ObjectPersister databaseOnlyPersister, ObjectPersister contentEntitiesPersister, ImportedObjectsStashFactory importedObjectsStashFactory, OnObjectsProcessingHandler onObjectsProcessingHandler, IdMapper idMapper, DeferredActionsHolder deferredActionsHolder, DeferredActionsFactory deferredActionsFactory, StorageFormatUserRewriter storageFormatUserRewriter) {
        this.databaseOnlyPersister = databaseOnlyPersister;
        this.contentEntitiesPersister = contentEntitiesPersister;
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
        this.idMapper = idMapper;
        this.deferredActionsHolder = deferredActionsHolder;
        this.deferredActionsFactory = deferredActionsFactory;
        this.contentUserKeyExtractor = new ContentUserKeyExtractor(storageFormatUserRewriter);
        this.persisterHelper = new PersisterHelper(onObjectsProcessingHandler);
    }

    public EntityPersister createSpacePersister(Optional<Set<String>> allowedLowerSpaceKeys) {
        log.debug("Creating space persister with lower space keys {}", allowedLowerSpaceKeys);
        return new SpacePersister(this.importedObjectsStashFactory, this.databaseOnlyPersister, this.idMapper, this.deferredActionsHolder, this.deferredActionsFactory, this.persisterHelper, this.onObjectsProcessingHandler, allowedLowerSpaceKeys);
    }

    public AncestorRecordsGenerator createAncestorsGenerator(ExportableEntityInfo ancestorsEntityInfo) {
        log.debug("Creating ancestors generator");
        return new AncestorRecordsGenerator(ancestorsEntityInfo);
    }

    public ContentPersister createContentEntityPersister(AncestorRecordsGenerator ancestorRecordsGenerator) {
        log.debug("Creating content entity persister");
        return new ContentPersister(this.contentEntitiesPersister, this.importedObjectsStashFactory, this.idMapper, new ContentPersisterHelper(this.onObjectsProcessingHandler), ancestorRecordsGenerator, this.onObjectsProcessingHandler);
    }

    public GenericPersister createGenericPersister(ExportableEntityInfo entityInfo) {
        log.debug("Creating generic persister for class {}", entityInfo.getEntityClass());
        ObjectQueueProcessor objectQueueProcessor = new ObjectQueueProcessor(this.databaseOnlyPersister, this.persisterHelper);
        return new GenericPersister(objectQueueProcessor, entityInfo.getEntityClass(), this.idMapper, this.importedObjectsStashFactory);
    }

    public PageTemplatePersister createPageTemplatePersister() {
        log.debug("Creating page template persister");
        PageTemplatePersisterHelper pageTemplatePersisterHelper = new PageTemplatePersisterHelper(this.onObjectsProcessingHandler);
        return new PageTemplatePersister(this.databaseOnlyPersister, this.idMapper, this.importedObjectsStashFactory, pageTemplatePersisterHelper, new ObjectQueueProcessor(this.databaseOnlyPersister, pageTemplatePersisterHelper));
    }

    public ContentPropertiesPersister createContentPropertiesPersister() {
        log.debug("Creating content properties persister");
        return new ContentPropertiesPersister(this.databaseOnlyPersister, this.importedObjectsStashFactory, false, this.persisterHelper, this.idMapper);
    }

    public BodyContentPersister createBodyContentPersister() {
        log.debug("Creating Body Content persister for space restore");
        return new BodyContentPersister(this.importedObjectsStashFactory, this.databaseOnlyPersister, this.persisterHelper, this.idMapper, this.contentUserKeyExtractor);
    }

    public InternalUserCredentialRecordPersister createInternalUserCredentialRecordPersister() {
        log.debug("Creating InternalUserCredentialRecord persister");
        ListIndexColumnValueCalculator listIndexColumnValueCalculator = new ListIndexColumnValueCalculator("credentialRecords", "user");
        return new InternalUserCredentialRecordPersister(this.databaseOnlyPersister, this.persisterHelper, this.importedObjectsStashFactory, this.idMapper, listIndexColumnValueCalculator);
    }

    public DirectoryMappingPersister createDirectoryMappingPersister(ExportableEntityInfo directoryMappingOperationEntityInfo) {
        log.debug("Creating directory mapping persister");
        OperationRecordsGenerator directoryMappingOperationRecordsGenerator = new OperationRecordsGenerator(directoryMappingOperationEntityInfo, "directoryMappingId", "operationType");
        ListIndexColumnValueCalculator listIndexColumnValueCalculator = new ListIndexColumnValueCalculator("directoryMappings", "application");
        return new DirectoryMappingPersister(this.databaseOnlyPersister, this.importedObjectsStashFactory, this.persisterHelper, this.idMapper, directoryMappingOperationRecordsGenerator, listIndexColumnValueCalculator);
    }

    public DirectoryPersister createDirectoryPersister(ExportableEntityInfo directoryOperationEntityInfo, ExportableEntityInfo directoryAttributeEntityInfo) {
        log.debug("Creating directory persister");
        OperationRecordsGenerator directoryOperationRecordsGenerator = new OperationRecordsGenerator(directoryOperationEntityInfo, "directoryId", "operationType");
        AttributeRecordsGenerator directoryAttributeRecordsGenerator = new AttributeRecordsGenerator(directoryAttributeEntityInfo, "directoryId");
        return new DirectoryPersister(this.databaseOnlyPersister, directoryOperationRecordsGenerator, directoryAttributeRecordsGenerator);
    }

    public ApplicationPersister createApplicationPersister(ExportableEntityInfo directoryAttributeEntityInfo) {
        log.debug("Creating application persister");
        AttributeRecordsGenerator applicationAttributeRecordsGenerator = new AttributeRecordsGenerator(directoryAttributeEntityInfo, "applicationId");
        return new ApplicationPersister(this.databaseOnlyPersister, applicationAttributeRecordsGenerator);
    }
}

