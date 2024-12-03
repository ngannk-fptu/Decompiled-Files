/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.DefaultSpoolFileFactory;
import com.atlassian.core.spool.DeferredSpoolFileOutputStream;
import com.atlassian.core.spool.FileFactory;
import com.atlassian.core.spool.FileSpool;
import com.atlassian.core.spool.ThresholdingSpool;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class DeferredSpool
implements FileSpool,
ThresholdingSpool {
    private int maxMemorySpool;
    private FileFactory fileFactory = DefaultSpoolFileFactory.getInstance();

    @Override
    public FileFactory getFileFactory() {
        return this.fileFactory;
    }

    @Override
    public void setFileFactory(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    @Override
    public void setThresholdBytes(int bytes) {
        this.maxMemorySpool = bytes;
    }

    @Override
    public int getThresholdBytes() {
        return this.maxMemorySpool;
    }

    @Override
    public InputStream spool(InputStream is) throws IOException {
        DeferredSpoolFileOutputStream deferredStream = this.getNewDeferredSpoolFileOutputStream();
        IOUtils.copy((InputStream)is, (OutputStream)((Object)deferredStream));
        deferredStream.close();
        return new BufferedInputStream(deferredStream.getInputStream());
    }

    protected DeferredSpoolFileOutputStream getNewDeferredSpoolFileOutputStream() {
        return new DeferredSpoolFileOutputStream(this.maxMemorySpool, this.getFileFactory());
    }
}

