/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 */
package org.apache.poi.xssf.streaming;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.poi.xssf.streaming.OpcOutputStream;

class OpcZipArchiveOutputStream
extends ZipArchiveOutputStream {
    private final OpcOutputStream out;

    OpcZipArchiveOutputStream(OutputStream out) {
        super(out);
        this.out = new OpcOutputStream(out);
    }

    public void setLevel(int level) {
        this.out.setLevel(level);
    }

    public void putArchiveEntry(ArchiveEntry archiveEntry) throws IOException {
        this.out.putNextEntry(archiveEntry.getName());
    }

    public void closeArchiveEntry() throws IOException {
        this.out.closeEntry();
    }

    public void finish() throws IOException {
        this.out.finish();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
    }

    public void close() throws IOException {
        this.out.close();
    }

    public void write(int b) throws IOException {
        this.out.write(b);
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void write(byte[] b) throws IOException {
        this.out.write(b);
    }
}

