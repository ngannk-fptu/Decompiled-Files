/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.AttributeRecordsGenerator;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.crowd.model.application.ApplicationImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(ApplicationPersister.class);
    private final ObjectPersister objectPersister;
    private final AttributeRecordsGenerator attributeRecordsGenerator;

    public ApplicationPersister(ObjectPersister objectPersister, AttributeRecordsGenerator attributeRecordsGenerator) {
        this.objectPersister = objectPersister;
        this.attributeRecordsGenerator = attributeRecordsGenerator;
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Set.of(ApplicationImpl.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.getSupportedClasses().contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("ApplicationPersister got unacceptable object with class " + importedObject.getEntityClass());
        }
        log.trace("Persisting application with id {}", importedObject.getId());
        ArrayList<ImportedObjectV2> objectsToPersist = new ArrayList<ImportedObjectV2>();
        objectsToPersist.add(importedObject);
        Collection<ImportedObjectV2> applicationAttributeRecords = this.attributeRecordsGenerator.prepareAttributeRecords(importedObject);
        log.trace("Prepared {} application attribute records for application with id {}", (Object)applicationAttributeRecords.size(), importedObject.getId());
        objectsToPersist.addAll(applicationAttributeRecords);
        this.objectPersister.persistAsynchronouslyInOneTransaction(objectsToPersist, "Persisting application records together with application attributes");
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        return 0L;
    }
}

