/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.stash.IOFriendlyFunction;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsReader;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsSerialiser;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StashObjectsReaderImpl
implements StashObjectsReader {
    private static final Logger log = LoggerFactory.getLogger(StashObjectsReaderImpl.class);
    private static final int NUMBER_OF_THREADS_FOR_DESERIALISATION = Integer.getInteger("confluence.restore.deserialisation-thread-pools-count", Runtime.getRuntime().availableProcessors());
    private final AtomicLong numberOfRetrievedObjects = new AtomicLong();
    private final StashObjectsWriter stashObjectsWriter;
    private final StashObjectsSerialiser stashObjectsSerialiser;
    private final File file;
    private final AtomicReference<FileInputStream> inputStream = new AtomicReference();
    private final IOFriendlyFunction<File, FileInputStream> fileInputStreamFunction;

    public StashObjectsReaderImpl(StashObjectsWriter stashObjectsWriter, StashObjectsSerialiser stashObjectsSerialiser, File file) {
        this(stashObjectsWriter, stashObjectsSerialiser, file, FileInputStream::new);
    }

    @VisibleForTesting
    public StashObjectsReaderImpl(StashObjectsWriter stashObjectsWriter, StashObjectsSerialiser stashObjectsSerialiser, File file, IOFriendlyFunction<File, FileInputStream> fileInputStreamFunction) {
        this.stashObjectsWriter = stashObjectsWriter;
        this.stashObjectsSerialiser = stashObjectsSerialiser;
        this.file = file;
        this.fileInputStreamFunction = fileInputStreamFunction;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ImportedObjectV2> readObjects(int numberOfObjects) throws BackupRestoreException {
        try {
            String string = this.file.getAbsolutePath();
            synchronized (string) {
                if (!this.hasMoreRecords()) {
                    throw new IllegalStateException("No more records in the stash. File: " + this.file.getAbsolutePath());
                }
                this.openFileIfRequired(this.file);
                List<ImportedObjectV2> objectsToReturn = this.readObjectsFromFile(numberOfObjects);
                log.trace("{} objects have been read from stash (file: {})", (Object)objectsToReturn.size(), (Object)this.file.getAbsolutePath());
                this.numberOfRetrievedObjects.addAndGet(objectsToReturn.size());
                if (!this.hasMoreRecords()) {
                    log.trace("The stash has no more records (file: {})", (Object)this.file.getAbsolutePath());
                    this.close();
                }
                return objectsToReturn;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            throw new BackupRestoreException("Unable to read objects from stash: " + e.getMessage() + ". File name: " + this.file.getAbsolutePath(), e);
        }
    }

    private void close() throws IOException {
        this.inputStream.get().close();
        if (!this.file.delete()) {
            log.warn("Unable to remove temp file for stash. It would be removed later by a cleaner. File name: " + this.file.getAbsolutePath());
        }
    }

    private List<ImportedObjectV2> readObjectsFromFile(int numberOfObjects) throws IOException, ClassNotFoundException {
        byte[] objectBytes;
        ArrayList<byte[]> objectsBytes = new ArrayList<byte[]>();
        while (numberOfObjects-- > 0 && (objectBytes = StashObjectsReaderImpl.readObjectDataAsByteArray(this.inputStream.get())) != null) {
            objectsBytes.add(objectBytes);
        }
        log.debug("Number of threads for deserialisation: {}.", (Object)NUMBER_OF_THREADS_FOR_DESERIALISATION);
        ForkJoinPool poolForDeserialisation = new ForkJoinPool(NUMBER_OF_THREADS_FOR_DESERIALISATION);
        try {
            List list = (List)((ForkJoinTask)poolForDeserialisation.submit(() -> objectsBytes.parallelStream().map(objectBytes -> {
                try {
                    return this.stashObjectsSerialiser.deserialise((byte[])objectBytes);
                }
                catch (IOException | ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            }).collect(Collectors.toList()))).get();
            return list;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            List<ImportedObjectV2> list = Collections.emptyList();
            return list;
        }
        catch (ExecutionException e) {
            throw new IllegalArgumentException(e);
        }
        finally {
            poolForDeserialisation.shutdown();
        }
    }

    private static byte[] readObjectDataAsByteArray(FileInputStream fileInputStream) throws IOException {
        byte[] objectLengthBytes = fileInputStream.readNBytes(4);
        if (objectLengthBytes.length == 0) {
            return null;
        }
        int objectLength = ByteBuffer.wrap(objectLengthBytes).getInt();
        return fileInputStream.readNBytes(objectLength);
    }

    private void openFileIfRequired(File file) throws IOException, BackupRestoreException {
        if (this.inputStream.get() != null) {
            return;
        }
        this.stashObjectsWriter.close();
        this.inputStream.set(this.fileInputStreamFunction.apply(file));
    }

    @Override
    public long getNumberOfRetrievedObjects() {
        return this.numberOfRetrievedObjects.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasMoreRecords() {
        String string = this.file.getAbsolutePath();
        synchronized (string) {
            long numberOfWrittenObjects = this.stashObjectsWriter.getNumberOfWrittenObjects();
            return numberOfWrittenObjects > 0L && this.getNumberOfRetrievedObjects() < numberOfWrittenObjects;
        }
    }
}

