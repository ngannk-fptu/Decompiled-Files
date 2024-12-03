/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.methods.multipart.PartSource;

public class FilePartSource
implements PartSource {
    private File file = null;
    private String fileName = null;

    public FilePartSource(File file) throws FileNotFoundException {
        this.file = file;
        if (file != null) {
            if (!file.isFile()) {
                throw new FileNotFoundException("File is not a normal file.");
            }
            if (!file.canRead()) {
                throw new FileNotFoundException("File is not readable.");
            }
            this.fileName = file.getName();
        }
    }

    public FilePartSource(String fileName, File file) throws FileNotFoundException {
        this(file);
        if (fileName != null) {
            this.fileName = fileName;
        }
    }

    @Override
    public long getLength() {
        if (this.file != null) {
            return this.file.length();
        }
        return 0L;
    }

    @Override
    public String getFileName() {
        return this.fileName == null ? "noname" : this.fileName;
    }

    @Override
    public InputStream createInputStream() throws IOException {
        if (this.file != null) {
            return new FileInputStream(this.file);
        }
        return new ByteArrayInputStream(new byte[0]);
    }
}

