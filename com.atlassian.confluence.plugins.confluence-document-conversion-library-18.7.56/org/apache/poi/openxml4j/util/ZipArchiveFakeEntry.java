/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.openxml4j.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.poifs.crypt.temp.EncryptedTempData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.TempFile;

class ZipArchiveFakeEntry
extends ZipArchiveEntry
implements Closeable {
    private static final Logger LOG = LogManager.getLogger(ZipArchiveFakeEntry.class);
    private static final int DEFAULT_MAX_ENTRY_SIZE = 100000000;
    private static int MAX_ENTRY_SIZE = 100000000;
    private byte[] data;
    private File tempFile;
    private EncryptedTempData encryptedTempData;

    public static void setMaxEntrySize(int maxEntrySize) {
        MAX_ENTRY_SIZE = maxEntrySize;
    }

    public static int getMaxEntrySize() {
        return MAX_ENTRY_SIZE;
    }

    ZipArchiveFakeEntry(ZipArchiveEntry entry, InputStream inp) throws IOException {
        super(entry.getName());
        long entrySize = entry.getSize();
        int threshold = ZipInputStreamZipEntrySource.getThresholdBytesForTempFiles();
        if (threshold >= 0 && entrySize >= (long)threshold) {
            if (ZipInputStreamZipEntrySource.shouldEncryptTempFiles()) {
                this.encryptedTempData = new EncryptedTempData();
                try (OutputStream os = this.encryptedTempData.getOutputStream();){
                    IOUtils.copy(inp, os);
                }
            } else {
                this.tempFile = TempFile.createTempFile("poi-zip-entry", ".tmp");
                Supplier[] supplierArray = new Supplier[3];
                supplierArray[0] = () -> this.tempFile.getAbsolutePath();
                supplierArray[1] = () -> ((ZipArchiveEntry)entry).getName();
                supplierArray[2] = () -> entrySize;
                LOG.atInfo().log("created for temp file {} for zip entry {} of size {} bytes", supplierArray);
                IOUtils.copy(inp, this.tempFile);
            }
        } else {
            if (entrySize < -1L || entrySize >= Integer.MAX_VALUE) {
                throw new IOException("ZIP entry size is too large or invalid");
            }
            this.data = entrySize == -1L ? IOUtils.toByteArrayWithMaxLength(inp, ZipArchiveFakeEntry.getMaxEntrySize()) : IOUtils.toByteArray(inp, (int)entrySize, ZipArchiveFakeEntry.getMaxEntrySize());
        }
    }

    public InputStream getInputStream() throws IOException {
        if (this.encryptedTempData != null) {
            try {
                return this.encryptedTempData.getInputStream();
            }
            catch (IOException e) {
                throw new IOException("failed to read from encrypted temp data", e);
            }
        }
        if (this.tempFile != null) {
            try {
                return new FileInputStream(this.tempFile);
            }
            catch (FileNotFoundException e) {
                throw new IOException("temp file " + this.tempFile.getAbsolutePath() + " is missing");
            }
        }
        if (this.data != null) {
            return new UnsynchronizedByteArrayInputStream(this.data);
        }
        throw new IOException("Cannot retrieve data from Zip Entry, probably because the Zip Entry was closed before the data was requested.");
    }

    @Override
    public void close() throws IOException {
        this.data = null;
        if (this.encryptedTempData != null) {
            this.encryptedTempData.dispose();
        }
        if (this.tempFile != null && this.tempFile.exists() && !this.tempFile.delete()) {
            LOG.atDebug().log("temp file was already deleted (probably due to previous call to close this resource)");
        }
    }
}

