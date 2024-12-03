/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.EntityInfoSqlHelper;
import com.atlassian.confluence.impl.backuprestore.restore.ImportedObjectsDispatcher;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapperFactory;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AttachmentsPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.DatabasePersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.EntityPersistersFactory;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersistersCreator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsFactory;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredActionsHolder;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipFile;

public class ImportedObjectsDispatcherFactory {
    private final ImportedObjectsStashFactory importedObjectsStashFactory;
    private final RestoreDao restoreDao;
    private final EntityInfoSqlHelper entityInfoSqlHelper;
    private final IdMapperFactory idMapperFactory;
    private final StorageFormatUserRewriter storageFormatUserRewriter;
    private final AttachmentDataFileSystem attachmentDataFileSystem;
    private final AttachmentDaoInternal attachmentDaoInternal;

    public ImportedObjectsDispatcherFactory(ImportedObjectsStashFactory importedObjectsStashFactory, RestoreDao restoreDao, EntityInfoSqlHelper entityInfoSqlHelper, IdMapperFactory idMapperFactory, StorageFormatUserRewriter storageFormatUserRewriter, AttachmentDataFileSystem attachmentDataFileSystem, AttachmentDaoInternal attachmentDaoInternal) {
        this.importedObjectsStashFactory = importedObjectsStashFactory;
        this.restoreDao = restoreDao;
        this.entityInfoSqlHelper = entityInfoSqlHelper;
        this.idMapperFactory = idMapperFactory;
        this.storageFormatUserRewriter = storageFormatUserRewriter;
        this.attachmentDataFileSystem = attachmentDataFileSystem;
        this.attachmentDaoInternal = attachmentDaoInternal;
    }

    public ImportedObjectsDispatcher createImportedObjectsDispatcher(BackupRestoreJob job, JobSource jobSource, Optional<Set<String>> allowedLowerSpaceKeys, String filePath, ParallelTasksExecutor parallelTasksExecutor, HibernateMetadataHelper hibernateMetadataHelper, OnObjectsProcessingHandler onObjectsProcessingHandler, Boolean backupAttachments) throws BackupRestoreException {
        Collection<ExportableEntityInfo> importableEntitiesInfo = this.getImportableEntitiesInfo(job, hibernateMetadataHelper);
        IdMapper idMapper = this.idMapperFactory.createIdMapper(job, jobSource, importableEntitiesInfo, onObjectsProcessingHandler);
        DatabasePersister databasePersister = new DatabasePersister(this.restoreDao, this.entityInfoSqlHelper, idMapper, onObjectsProcessingHandler);
        AttachmentsPersister attachmentsPersister = new AttachmentsPersister(this.attachmentDataFileSystem, this.attachmentDaoInternal, idMapper, () -> new ZipFile(new File(filePath)), backupAttachments);
        ObjectPersister databaseOnlyPersister = new ObjectPersister(parallelTasksExecutor, Collections.singletonList(databasePersister));
        ObjectPersister contentEntitiesPersister = new ObjectPersister(parallelTasksExecutor, Arrays.asList(databasePersister, attachmentsPersister));
        DeferredActionsHolder deferredActionsHolder = new DeferredActionsHolder();
        DeferredActionsFactory deferredActionsFactory = new DeferredActionsFactory(hibernateMetadataHelper, this.restoreDao, idMapper);
        EntityPersistersFactory entityPersistersFactory = new EntityPersistersFactory(databaseOnlyPersister, contentEntitiesPersister, this.importedObjectsStashFactory, onObjectsProcessingHandler, idMapper, deferredActionsHolder, deferredActionsFactory, this.storageFormatUserRewriter);
        return new ImportedObjectsDispatcher(new PersistersCreator(entityPersistersFactory, importableEntitiesInfo, hibernateMetadataHelper.getAdditionalExportableEntitiesByClass(), job.getJobScope(), allowedLowerSpaceKeys), deferredActionsHolder);
    }

    private Collection<ExportableEntityInfo> getImportableEntitiesInfo(BackupRestoreJob job, HibernateMetadataHelper hibernateMetadataHelper) {
        if (JobScope.SPACE.equals((Object)job.getJobScope())) {
            return hibernateMetadataHelper.getAllSpaceImportableEntities();
        }
        if (JobScope.SITE.equals((Object)job.getJobScope())) {
            return hibernateMetadataHelper.getAllSiteImportableEntities();
        }
        throw new IllegalStateException(String.format("Unknown job scope %s", job));
    }
}

