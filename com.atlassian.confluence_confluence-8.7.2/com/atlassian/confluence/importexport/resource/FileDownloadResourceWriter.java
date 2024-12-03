/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileDownloadResourceWriter
implements DownloadResourceWriter {
    private final String resourcePath;
    private final File file;

    public FileDownloadResourceWriter(String resourcePath, File file) {
        this.resourcePath = resourcePath;
        this.file = file;
    }

    @Override
    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public OutputStream getStreamForWriting() {
        try {
            return new FileOutputStream(this.file);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not open macro temporary file for writing [" + this.file + "]", e);
        }
    }
}

