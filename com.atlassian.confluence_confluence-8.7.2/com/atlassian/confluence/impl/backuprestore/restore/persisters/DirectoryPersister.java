/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AttributeRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.OperationRecordsGenerator;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(DirectoryPersister.class);
    private final ObjectPersister objectPersister;
    private final OperationRecordsGenerator operationRecordsGenerator;
    private final AttributeRecordsGenerator attributeRecordsGenerator;

    public DirectoryPersister(ObjectPersister objectPersister, OperationRecordsGenerator operationRecordsGenerator, AttributeRecordsGenerator attributeRecordsGenerator) {
        this.objectPersister = objectPersister;
        this.operationRecordsGenerator = operationRecordsGenerator;
        this.attributeRecordsGenerator = attributeRecordsGenerator;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Set.of(DirectoryImpl.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.getSupportedClasses().contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("DirectoryPersister got unacceptable object with class " + importedObject.getEntityClass());
        }
        log.trace("Persisting directory with id {}", importedObject.getId());
        ArrayList<ImportedObjectV2> objectsToPersist = new ArrayList<ImportedObjectV2>();
        objectsToPersist.add(importedObject);
        Collection<ImportedObjectV2> directoryOperationRecords = this.operationRecordsGenerator.prepareOperationRecords(importedObject);
        log.trace("Prepared {} directory operation records for directory with id {}", (Object)directoryOperationRecords.size(), importedObject.getId());
        objectsToPersist.addAll(directoryOperationRecords);
        Collection<ImportedObjectV2> directoryAttributeRecords = this.attributeRecordsGenerator.prepareAttributeRecords(importedObject);
        log.trace("Prepared {} directory attribute records for directory with id {}", (Object)directoryAttributeRecords.size(), importedObject.getId());
        objectsToPersist.addAll(directoryAttributeRecords);
        this.objectPersister.persistAsynchronouslyInOneTransaction(objectsToPersist, "Persisting directory records together with directory operations and attributes");
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        return 0L;
    }
}

