/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.stash.IOFriendlyFunction;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsSerialiser;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class StashObjectsWriterImpl
implements StashObjectsWriter {
    static final int OBJECT_LENGTH_PREFIX_SIZE = 4;
    private final AtomicLong numberOfWrittenObjects = new AtomicLong();
    private final AtomicBoolean processingFinished = new AtomicBoolean();
    private final StashObjectsSerialiser stashObjectsSerialiser;
    private final File file;
    private final AtomicReference<FileOutputStream> outputStream = new AtomicReference();
    private final IOFriendlyFunction<File, FileOutputStream> fileOutputStreamFunction;

    public StashObjectsWriterImpl(StashObjectsSerialiser stashObjectsSerialiser, File file) {
        this(stashObjectsSerialiser, file, FileOutputStream::new);
    }

    @VisibleForTesting
    public StashObjectsWriterImpl(StashObjectsSerialiser stashObjectsSerialiser, File file, IOFriendlyFunction<File, FileOutputStream> fileOutputStreamFunction) {
        this.stashObjectsSerialiser = stashObjectsSerialiser;
        this.file = file;
        this.fileOutputStreamFunction = fileOutputStreamFunction;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeObject(ImportedObjectV2 object) throws BackupRestoreException {
        try {
            String string = this.file.getAbsolutePath();
            synchronized (string) {
                if (this.processingFinished.get()) {
                    throw new IllegalStateException("Unable to write to the closed stash writer. File: " + this.file.getAbsolutePath());
                }
                this.createOutputStreamOnFirstTouch();
                this.writeObjectToFile(object);
                this.numberOfWrittenObjects.incrementAndGet();
            }
        }
        catch (IOException e) {
            throw new BackupRestoreException("Unable to serialise object with id " + object.getId(), e);
        }
    }

    private void createOutputStreamOnFirstTouch() throws BackupRestoreException {
        if (this.outputStream.get() != null) {
            return;
        }
        try {
            this.outputStream.set(this.fileOutputStreamFunction.apply(this.file));
        }
        catch (IOException e) {
            throw new BackupRestoreException("Unable to create file for stash: " + e.getMessage() + ". File: " + this.file.getAbsolutePath(), e);
        }
    }

    private void writeObjectToFile(ImportedObjectV2 object) throws IOException {
        byte[] objectBytes = this.stashObjectsSerialiser.serialise(object);
        this.outputStream.get().write(ByteBuffer.allocate(4).putInt(objectBytes.length).array());
        this.outputStream.get().write(objectBytes);
    }

    @Override
    public long getNumberOfWrittenObjects() {
        return this.numberOfWrittenObjects.get();
    }

    @Override
    public void close() throws BackupRestoreException {
        if (this.processingFinished.get()) {
            throw new IllegalStateException("Unable to close writer that is already closed. File: " + this.file.getAbsolutePath());
        }
        if (this.numberOfWrittenObjects.get() > 0L) {
            try {
                this.outputStream.get().close();
                this.outputStream.set(null);
            }
            catch (IOException e) {
                throw new BackupRestoreException("Unable to close the output stream for stash " + this.file.getAbsolutePath(), e);
            }
        }
        this.processingFinished.set(true);
    }
}

