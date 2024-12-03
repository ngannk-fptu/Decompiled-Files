/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsReader;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsWriter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedObjectsStashImpl
implements ImportedObjectsStash {
    private static final Logger log = LoggerFactory.getLogger(ImportedObjectsStashImpl.class);
    private final String name;
    private final Integer iteration;
    private final StashObjectsWriter stashObjectsWriter;
    private final StashObjectsReader stashObjectsReader;

    public ImportedObjectsStashImpl(String name, int iteration, StashObjectsWriter stashObjectsWriter, StashObjectsReader stashObjectsReader) {
        this.name = name;
        this.iteration = iteration;
        this.stashObjectsWriter = stashObjectsWriter;
        this.stashObjectsReader = stashObjectsReader;
        log.debug("Stash '{}' (iteration {}) has been created", (Object)name, (Object)iteration);
    }

    @Override
    public void add(ImportedObjectV2 object) throws BackupRestoreException {
        log.trace("Adding an object with id {} to the queue of '{}' stash", object.getId(), (Object)this.name);
        this.stashObjectsWriter.writeObject(object);
    }

    @Override
    public List<ImportedObjectV2> readObjects(int numberOfObjects) throws BackupRestoreException {
        log.trace("Reading {} objects from '{}' stash", (Object)numberOfObjects, (Object)this.name);
        if (numberOfObjects < 1) {
            throw new IllegalArgumentException("Number of objects to retrieve should be more than 0. Actual value is " + numberOfObjects + ". Stash name: " + this.name);
        }
        return this.stashObjectsReader.readObjects(numberOfObjects);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getIterationNumber() {
        return this.iteration;
    }

    @Override
    public long getNumberOfWrittenObjects() {
        return this.stashObjectsWriter.getNumberOfWrittenObjects();
    }

    @Override
    public long getNumberOfRetrievedObjects() {
        return this.stashObjectsReader.getNumberOfRetrievedObjects();
    }

    @Override
    public boolean hasMoreRecords() {
        return this.getNumberOfWrittenObjects() > this.getNumberOfRetrievedObjects();
    }
}

