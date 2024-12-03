/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.DefaultSpoolFileFactory;
import com.atlassian.core.spool.DeferredFileOutputStream;
import com.atlassian.core.spool.FileFactory;
import com.atlassian.core.spool.SpoolFileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DeferredSpoolFileOutputStream
extends DeferredFileOutputStream {
    private FileFactory fileFactory = DefaultSpoolFileFactory.getInstance();
    private boolean unspooling = false;

    public DeferredSpoolFileOutputStream(int threshold, File outputFile) {
        super(threshold, outputFile);
    }

    public DeferredSpoolFileOutputStream(int threshold, FileFactory fileFactory) {
        super(threshold, null);
        this.fileFactory = fileFactory;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isUnspooling() {
        return this.unspooling;
    }

    @Override
    protected void thresholdReached() throws IOException {
        if (this.outputFile == null) {
            this.outputFile = this.fileFactory.createNewFile();
        }
        super.thresholdReached();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream spoolStream;
        if (!this.isClosed()) {
            throw new IOException("Output stream not closed");
        }
        if (this.isUnspooling()) {
            throw new IOException("Stream is already being unspooled");
        }
        if (this.isInMemory()) {
            spoolStream = new ByteArrayInputStream(this.getData());
        } else {
            try {
                spoolStream = new SpoolFileInputStream(this.getFile());
            }
            catch (FileNotFoundException ex) {
                throw new IOException("Deferred file does not exist");
            }
        }
        this.unspooling = true;
        return spoolStream;
    }
}

