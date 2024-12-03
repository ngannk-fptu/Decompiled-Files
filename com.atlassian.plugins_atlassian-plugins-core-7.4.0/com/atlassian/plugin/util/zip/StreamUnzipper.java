/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.util.zip;

import com.atlassian.plugin.util.zip.AbstractUnzipper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class StreamUnzipper
extends AbstractUnzipper {
    private ZipInputStream zis;

    public StreamUnzipper(InputStream zipStream, File destDir) {
        if (zipStream == null) {
            throw new IllegalArgumentException("zip stream cannot be null");
        }
        this.zis = new ZipInputStream(zipStream);
        this.destDir = destDir;
    }

    @Override
    public void unzip() throws IOException {
        ZipEntry zipEntry = this.zis.getNextEntry();
        try {
            while (zipEntry != null) {
                this.saveEntry(this.zis, zipEntry);
                this.zis.closeEntry();
                zipEntry = this.zis.getNextEntry();
            }
        }
        finally {
            IOUtils.closeQuietly((InputStream)this.zis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File unzipFileInArchive(String fileName) throws IOException {
        File result = null;
        try {
            ZipEntry zipEntry = this.zis.getNextEntry();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                if (StringUtils.isNotEmpty((CharSequence)entryName) && entryName.startsWith("/")) {
                    entryName = entryName.substring(1);
                }
                if (fileName.equals(entryName)) {
                    result = this.saveEntry(this.zis, zipEntry);
                    break;
                }
                this.zis.closeEntry();
                zipEntry = this.zis.getNextEntry();
            }
        }
        finally {
            IOUtils.closeQuietly((InputStream)this.zis);
        }
        return result;
    }

    @Override
    public ZipEntry[] entries() throws IOException {
        return this.entries(this.zis);
    }
}

