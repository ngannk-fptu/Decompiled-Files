/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.unit.DataSize
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.XmlBackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.Persister;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.Refs;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.unit.DataSize;

public class AttachmentsPersister
implements Persister {
    private static final Logger log = LoggerFactory.getLogger(AttachmentsPersister.class);
    private final AttachmentDataFileSystem attachmentDataFileSystem;
    private final AttachmentDaoInternal attachmentDao;
    private final IdMapper idMapper;
    private final Callable<ZipFile> zipFileSupplier;
    private final boolean backupAttachments;

    public AttachmentsPersister(AttachmentDataFileSystem attachmentDataFileSystem, AttachmentDaoInternal attachmentDao, IdMapper idMapper, Callable<ZipFile> zipFile, Boolean backupAttachments) {
        this.attachmentDataFileSystem = attachmentDataFileSystem;
        this.attachmentDao = attachmentDao;
        this.idMapper = idMapper;
        this.zipFileSupplier = zipFile;
        this.backupAttachments = backupAttachments;
    }

    @Override
    public boolean shouldPersist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> objectsWithDatabaseIdsByClass) {
        return this.backupAttachments && objectsWithDatabaseIdsByClass.keySet().stream().anyMatch(o -> o.getEntityClass() == Attachment.class);
    }

    @Override
    public void persist(Map<ExportableEntityInfo, Collection<ImportedObjectV2>> objectsToPersistGroupedByClass) throws BackupRestoreException {
        List attachmentEntities = objectsToPersistGroupedByClass.entrySet().stream().filter(o -> ((ExportableEntityInfo)o.getKey()).getEntityClass() == Attachment.class).collect(Collectors.toList());
        try (ZipFile zipFile = this.zipFileSupplier.call();){
            for (Map.Entry keyToValue : attachmentEntities) {
                for (ImportedObjectV2 attachmentObjectToPersist : (Collection)keyToValue.getValue()) {
                    this.persistAttachment(zipFile, attachmentObjectToPersist);
                }
            }
        }
        catch (IOException ioException) {
            throw new BackupRestoreException("Cannot access the backup file. Aborting restore.", ioException);
        }
        catch (Exception e) {
            throw new BackupRestoreException("Unexpected error when accessing the backup file. Aborting restore.", e);
        }
    }

    private void persistAttachment(ZipFile zipFile, ImportedObjectV2 attachmentObjectToPersist) {
        Long restoredOriginalVersionId = (Long)attachmentObjectToPersist.getFieldValue("originalVersion");
        Long restoredAttachmentId = (Long)attachmentObjectToPersist.getId();
        Long restoredContainerId = (Long)attachmentObjectToPersist.getFieldValue("containerContent");
        Integer version = (Integer)attachmentObjectToPersist.getFieldValue("version");
        Long attachmentIdInDb = (Long)this.idMapper.getDatabaseId(Attachment.class, restoredAttachmentId);
        if (attachmentIdInDb == null) {
            log.warn("Cannot find in database an attachment with restored id {}", (Object)restoredAttachmentId);
            return;
        }
        Attachment attachment = this.attachmentDao.getById(attachmentIdInDb);
        String pathInZip = XmlBackupContainerWriter.calculatePathInZip(restoredContainerId, restoredOriginalVersionId == null ? restoredAttachmentId : restoredOriginalVersionId, version);
        ZipEntry zipEntry = zipFile.getEntry(pathInZip);
        if (zipEntry == null) {
            log.warn("Cannot restore the attachment with id {}. No entry in the zip file. Looked in {}", (Object)restoredAttachmentId, (Object)pathInZip);
            return;
        }
        try (InputStream stream = zipFile.getInputStream(zipEntry);){
            AttachmentDataStream inputStreamSource = AttachmentDataStream.create(AttachmentDataStreamType.RAW_BINARY, () -> stream);
            this.attachmentDataFileSystem.saveAttachmentData(Refs.ref(attachment), inputStreamSource, false, DataSize.ofBytes((long)zipEntry.getSize()));
        }
        catch (IOException | IllegalStateException e) {
            log.warn("Cannot restore the attachment. Failed to read the stream for entry {}.", (Object)pathInZip, (Object)e);
        }
    }
}

