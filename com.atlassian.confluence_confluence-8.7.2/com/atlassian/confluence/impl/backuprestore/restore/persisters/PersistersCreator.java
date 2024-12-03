/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.user.InternalUserCredentialRecord
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterCreatorHelper;
import com.atlassian.confluence.impl.backuprestore.hibernate.AncestorsEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.ApplicationAttributeEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryAttributeEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryMappingOperationEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryOperationEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AncestorRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.EntityPersistersFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.user.InternalUserCredentialRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PersistersCreator {
    private final EntityPersistersFactory entityPersistersFactory;
    private final List<Collection<EntityPersister>> persistersGroupedByStages = new ArrayList<Collection<EntityPersister>>();

    public PersistersCreator(EntityPersistersFactory entityPersistersFactory, Collection<ExportableEntityInfo> exportableEntityInfos, Map<Class<?>, ExportableEntityInfo> additionalExportableEntities, JobScope jobScope, Optional<Set<String>> allowedLowerSpaceKeys) throws BackupRestoreException {
        this.entityPersistersFactory = entityPersistersFactory;
        this.createAllPersisters(exportableEntityInfos, additionalExportableEntities, jobScope, allowedLowerSpaceKeys);
    }

    public List<Collection<EntityPersister>> getPersistersGroupedByStages() {
        return this.persistersGroupedByStages;
    }

    private void createAllPersisters(Collection<ExportableEntityInfo> exportableEntities, Map<Class<?>, ExportableEntityInfo> additionalExportableEntities, JobScope job, Optional<Set<String>> allowedLowerSpaceKeys) throws BackupRestoreException {
        HashSet<ExportableEntityInfo> pendingExportableEntitiesToProcess = new HashSet<ExportableEntityInfo>(exportableEntities);
        HashSet processedClasses = new HashSet();
        Collection<EntityPersister> zeroStagePersisters = this.createZeroStagePersisters(pendingExportableEntitiesToProcess, additionalExportableEntities, allowedLowerSpaceKeys);
        this.persistersGroupedByStages.add(zeroStagePersisters);
        processedClasses.addAll(zeroStagePersisters.stream().map(EntityPersister::getSupportedClasses).flatMap(Collection::stream).collect(Collectors.toList()));
        ExportableEntityInfo ancestorsEntityInfo = additionalExportableEntities.get(AncestorsEntityInfo.EntityClass.class);
        EntityPersister contentPersister = this.createContentPersister(pendingExportableEntitiesToProcess, this.entityPersistersFactory.createAncestorsGenerator(ancestorsEntityInfo));
        processedClasses.addAll(contentPersister.getSupportedClasses());
        EntityPersister pageTemplatePersister = this.createPageTemplatePersister(pendingExportableEntitiesToProcess);
        processedClasses.addAll(pageTemplatePersister.getSupportedClasses());
        this.persistersGroupedByStages.add(List.of(contentPersister, pageTemplatePersister));
        while (pendingExportableEntitiesToProcess.size() > 0) {
            int numberOfEntitiesBeforeProcessing = pendingExportableEntitiesToProcess.size();
            Collection<EntityPersister> currentStagePersisters = this.createNextStagePersisters(pendingExportableEntitiesToProcess, processedClasses, additionalExportableEntities, job);
            if (pendingExportableEntitiesToProcess.size() > 0 && numberOfEntitiesBeforeProcessing == pendingExportableEntitiesToProcess.size()) {
                throw new BackupRestoreException("Unable to split persisters to stages, some entities (" + pendingExportableEntitiesToProcess.size() + ") were not processed, one of them is " + ((ExportableEntityInfo)pendingExportableEntitiesToProcess.iterator().next()).getEntityClass());
            }
            this.persistersGroupedByStages.add(currentStagePersisters);
            processedClasses.addAll(currentStagePersisters.stream().map(EntityPersister::getSupportedClasses).flatMap(Collection::stream).collect(Collectors.toList()));
        }
    }

    private Collection<EntityPersister> createNextStagePersisters(Collection<ExportableEntityInfo> pendingExportableEntitiesToProcess, Set<Class<?>> processedClasses, Map<Class<?>, ExportableEntityInfo> additionalExportableEntities, JobScope job) {
        ArrayList<EntityPersister> currentStagePersisters = new ArrayList<EntityPersister>();
        Iterator<ExportableEntityInfo> entitiesIterator = pendingExportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            Collection<HibernateField> allExternalReferences = entityInfo.getAllExternalReferences();
            if (!allExternalReferences.stream().allMatch(field -> this.isReferencedClassAlreadyProcessed(entityInfo.getEntityClass(), field.getReferencedClass(), processedClasses))) continue;
            if (entityInfo.getEntityClass().equals(ContentProperty.class)) {
                currentStagePersisters.add(this.entityPersistersFactory.createContentPropertiesPersister());
            } else if (entityInfo.getEntityClass().equals(DirectoryMapping.class)) {
                ExportableEntityInfo directoryMappingOperationEntityInfo = additionalExportableEntities.get(DirectoryMappingOperationEntityInfo.EntityClass.class);
                currentStagePersisters.add(this.entityPersistersFactory.createDirectoryMappingPersister(directoryMappingOperationEntityInfo));
            } else if (entityInfo.getEntityClass().equals(InternalUserCredentialRecord.class)) {
                currentStagePersisters.add(this.entityPersistersFactory.createInternalUserCredentialRecordPersister());
            } else if (JobScope.SPACE.equals((Object)job) && entityInfo.getEntityClass().equals(BodyContent.class)) {
                currentStagePersisters.add(this.entityPersistersFactory.createBodyContentPersister());
            } else {
                currentStagePersisters.add(this.entityPersistersFactory.createGenericPersister(entityInfo));
            }
            entitiesIterator.remove();
        }
        return currentStagePersisters;
    }

    private boolean isReferencedClassAlreadyProcessed(Class<?> entityClass, Class<?> referencedClass, Set<Class<?>> processedClasses) {
        if (referencedClass == null) {
            return true;
        }
        if (referencedClass.equals(entityClass)) {
            return true;
        }
        if (processedClasses.contains(referencedClass)) {
            return true;
        }
        return ConfluenceEntityObject.class.isAssignableFrom(referencedClass) && processedClasses.contains(ContentEntityObject.class);
    }

    private Collection<EntityPersister> createZeroStagePersisters(Collection<ExportableEntityInfo> pendingExportableEntitiesToProcess, Map<Class<?>, ExportableEntityInfo> additionalExportableEntities, Optional<Set<String>> allowedLowerSpaceKeys) {
        ArrayList<EntityPersister> zeroStagePersisters = new ArrayList<EntityPersister>();
        Iterator<ExportableEntityInfo> entitiesIterator = pendingExportableEntitiesToProcess.iterator();
        while (entitiesIterator.hasNext()) {
            ExportableEntityInfo entityInfo = entitiesIterator.next();
            if (entityInfo.getEntityClass().equals(Space.class)) {
                zeroStagePersisters.add(this.entityPersistersFactory.createSpacePersister(allowedLowerSpaceKeys));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(ApplicationImpl.class)) {
                ExportableEntityInfo applicationAttributeEntityInfo = additionalExportableEntities.get(ApplicationAttributeEntityInfo.EntityClass.class);
                zeroStagePersisters.add(this.entityPersistersFactory.createApplicationPersister(applicationAttributeEntityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(DirectoryImpl.class)) {
                ExportableEntityInfo directoryOperationEntityInfo = additionalExportableEntities.get(DirectoryOperationEntityInfo.EntityClass.class);
                ExportableEntityInfo directoryAttributeEntityInfo = additionalExportableEntities.get(DirectoryAttributeEntityInfo.EntityClass.class);
                zeroStagePersisters.add(this.entityPersistersFactory.createDirectoryPersister(directoryOperationEntityInfo, directoryAttributeEntityInfo));
                entitiesIterator.remove();
                continue;
            }
            if (entityInfo.getEntityClass().equals(BucketPropertySetItem.class) || !entityInfo.getAllExternalReferences().isEmpty()) continue;
            zeroStagePersisters.add(this.entityPersistersFactory.createGenericPersister(entityInfo));
            entitiesIterator.remove();
        }
        return zeroStagePersisters;
    }

    private EntityPersister createContentPersister(Collection<ExportableEntityInfo> pendingExportableEntitiesToProcess, AncestorRecordsGenerator ancestorRecordsGenerator) {
        List<ExportableEntityInfo> allContentEntityInfos = ExporterCreatorHelper.cutAllContentEntityInfos(pendingExportableEntitiesToProcess);
        if (allContentEntityInfos.isEmpty()) {
            throw new IllegalStateException("No content persisters found!");
        }
        return this.entityPersistersFactory.createContentEntityPersister(ancestorRecordsGenerator);
    }

    private EntityPersister createPageTemplatePersister(Collection<ExportableEntityInfo> pendingExportableEntitiesToProcess) {
        ExportableEntityInfo pageTemplateEntityInfo = ExporterCreatorHelper.cutPageTemplateEntityInfo(pendingExportableEntitiesToProcess);
        if (pageTemplateEntityInfo == null) {
            throw new IllegalStateException("No page template persister found");
        }
        return this.entityPersistersFactory.createPageTemplatePersister();
    }
}

