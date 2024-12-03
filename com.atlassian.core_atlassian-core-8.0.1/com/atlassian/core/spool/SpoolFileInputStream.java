/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.spool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoolFileInputStream
extends FileInputStream {
    private static final Logger log = LoggerFactory.getLogger(SpoolFileInputStream.class);
    private File fileToDelete;
    private boolean closed = false;

    public SpoolFileInputStream(File file) throws FileNotFoundException {
        super(file);
        this.init(file);
    }

    public SpoolFileInputStream(String name) throws FileNotFoundException {
        super(name);
        this.init(new File(name));
    }

    private void init(File file) {
        this.fileToDelete = file;
    }

    @Override
    public void close() throws IOException {
        try {
            this.closed = true;
            super.close();
        }
        catch (IOException ex) {
            log.error("Error closing spool stream", (Throwable)ex);
        }
        finally {
            if (this.fileToDelete.exists() && !this.fileToDelete.delete()) {
                log.warn("Could not delete spool file " + this.fileToDelete);
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        int n = super.read();
        if (n == -1) {
            this.close();
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (this.closed) {
            return -1;
        }
        int n = super.read(b);
        if (n == -1) {
            this.close();
        }
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        int n = super.read(b, off, len);
        if (n == -1) {
            this.close();
        }
        return n;
    }
}

