/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
 *  org.apache.commons.compress.archivers.zip.Zip64Mode
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 *  org.apache.commons.compress.parallel.InputStreamSupplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.ArchiveWriter;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamZipWriter
implements ArchiveWriter {
    private static final Logger log = LoggerFactory.getLogger(StreamZipWriter.class);
    private ZipArchiveOutputStream zipArchiveOutputStream;
    private final ParallelScatterZipCreator scatterZipCreator;

    public StreamZipWriter(@Nonnull OutputStream zippedOutputStream, @Nonnull ParallelScatterZipCreator scatterZipCreator) throws IOException {
        this(zippedOutputStream, scatterZipCreator, Zip64Mode.AsNeeded);
    }

    @VisibleForTesting
    StreamZipWriter(@Nonnull OutputStream zippedOutputStream, @Nonnull ParallelScatterZipCreator scatterZipCreator, Zip64Mode zipMode) throws IOException {
        this.scatterZipCreator = scatterZipCreator;
        this.zipArchiveOutputStream = new ZipArchiveOutputStream(zippedOutputStream);
        this.zipArchiveOutputStream.setUseZip64(zipMode);
    }

    @Override
    public synchronized void compressFromStreamSupplier(InputStreamSupplier streamSupplier, String pathInZip) {
        this.compressFromStreamSupplier(streamSupplier, pathInZip, null);
    }

    @Override
    public synchronized void compressFromStreamSupplier(InputStreamSupplier streamSupplier, String pathInZip, String comment) {
        if (this.zipArchiveOutputStream == null) {
            throw new IllegalStateException("Backup zip output stream has not been created.");
        }
        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(pathInZip);
        zipArchiveEntry.setMethod(8);
        zipArchiveEntry.setComment(comment);
        this.scatterZipCreator.addArchiveEntry(zipArchiveEntry, streamSupplier);
    }

    @Override
    public synchronized void compressFromStream(InputStream streamToZip, String pathInZip) {
        this.compressFromStream(streamToZip, pathInZip, null);
    }

    @Override
    public synchronized void compressFromStream(InputStream streamToZip, String pathInZip, String comment) {
        InputStreamSupplier streamSupplier = () -> streamToZip;
        this.compressFromStreamSupplier(streamSupplier, pathInZip, comment);
    }

    @Override
    public synchronized void close() throws BackupRestoreException {
        try {
            this.scatterZipCreator.writeTo(this.zipArchiveOutputStream);
            this.zipArchiveOutputStream.close();
            this.zipArchiveOutputStream = null;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (IOException | ExecutionException e) {
            throw new BackupRestoreException(e);
        }
    }
}

