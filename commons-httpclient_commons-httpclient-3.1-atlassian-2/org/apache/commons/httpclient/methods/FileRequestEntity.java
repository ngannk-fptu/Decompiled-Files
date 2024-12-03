/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.httpclient.methods.RequestEntity;

public class FileRequestEntity
implements RequestEntity {
    final File file;
    final String contentType;

    public FileRequestEntity(File file, String contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeRequest(OutputStream out) throws IOException {
        byte[] tmp = new byte[4096];
        int i = 0;
        FileInputStream instream = new FileInputStream(this.file);
        try {
            while ((i = ((InputStream)instream).read(tmp)) >= 0) {
                out.write(tmp, 0, i);
            }
        }
        finally {
            ((InputStream)instream).close();
        }
    }
}

