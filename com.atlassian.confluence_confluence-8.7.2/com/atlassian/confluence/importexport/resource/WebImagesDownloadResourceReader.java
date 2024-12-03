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

public class WebImagesDownloadResourceReader
implements DownloadResourceReader {
    private final String resourcePath;
    private InputStreamSource inputStreamSource;

    public WebImagesDownloadResourceReader(String resourcePath, InputStreamSource inputStreamSource) {
        this.resourcePath = resourcePath;
        this.inputStreamSource = inputStreamSource;
    }

    @Override
    public String getName() {
        try {
            return this.resourcePath.substring(this.resourcePath.lastIndexOf("/") + 1);
        }
        catch (IndexOutOfBoundsException e) {
            return this.resourcePath;
        }
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("This operation is not supported by the WebImagesDownloadResourceReader");
    }

    @Override
    public long getContentLength() {
        throw new UnsupportedOperationException("This operation is not supported by the WebImagesDownloadResourceReader");
    }

    @Override
    public Date getLastModificationDate() {
        throw new UnsupportedOperationException("This operation is not supported by the WebImagesDownloadResourceReader");
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

