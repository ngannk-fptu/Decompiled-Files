/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.DefaultSpoolFileFactory;
import com.atlassian.core.spool.FileFactory;
import com.atlassian.core.spool.FileSpool;
import com.atlassian.core.spool.SpoolFileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class BufferedFileSpool
implements FileSpool {
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
    public InputStream spool(InputStream is) throws IOException {
        File spoolFile = this.fileFactory.createNewFile();
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(spoolFile));
        IOUtils.copy((InputStream)is, (OutputStream)os);
        ((OutputStream)os).close();
        return new BufferedInputStream(new SpoolFileInputStream(spoolFile));
    }
}

