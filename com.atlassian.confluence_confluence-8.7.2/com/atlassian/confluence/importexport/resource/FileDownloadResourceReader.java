/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDownloadResourceReader
implements DownloadResourceReader {
    private static final Logger log = LoggerFactory.getLogger(FileDownloadResourceReader.class);
    private final File file;
    private boolean deleteFileOnClose;

    public FileDownloadResourceReader(File file, boolean deleteFileOnClose) {
        this.file = file;
        this.deleteFileOnClose = deleteFileOnClose;
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("This operation is not supported by the FileDownloadResourceReader");
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public Date getLastModificationDate() {
        return new Date(this.file.lastModified());
    }

    @Override
    public InputStream getStreamForReading() {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(this.file);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not open macro temporary file for reading [" + this.file + "]", e);
        }
        if (this.deleteFileOnClose) {
            return new FilterInputStream(inputStream){

                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    }
                    finally {
                        if (FileDownloadResourceReader.this.file.exists() && !FileDownloadResourceReader.this.file.delete()) {
                            log.warn("Could not delete file " + FileDownloadResourceReader.this.file);
                        }
                    }
                }
            };
        }
        return inputStream;
    }
}

