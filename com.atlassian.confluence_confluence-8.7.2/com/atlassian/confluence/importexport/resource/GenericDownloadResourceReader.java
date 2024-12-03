/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.springframework.core.io.InputStreamSource;

public class GenericDownloadResourceReader
implements DownloadResourceReader {
    private final String name;
    private final InputStreamSource inputStreamSource;

    public GenericDownloadResourceReader(String name, InputStreamSource inputStreamSource) {
        this.name = name;
        this.inputStreamSource = inputStreamSource;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("This operation is not supported by the GenericDownloadResourceReader");
    }

    @Override
    public long getContentLength() {
        throw new UnsupportedOperationException("This operation is not supported by the GenericDownloadResourceReader");
    }

    @Override
    public Date getLastModificationDate() {
        throw new UnsupportedOperationException("This operation is not supported by the GenericDownloadResourceReader");
    }

    @Override
    public InputStream getStreamForReading() {
        try {
            return this.inputStreamSource.getInputStream();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

