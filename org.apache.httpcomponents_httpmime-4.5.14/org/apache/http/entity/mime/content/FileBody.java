/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.entity.ContentType
 *  org.apache.http.util.Args
 */
package org.apache.http.entity.mime.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.util.Args;

public class FileBody
extends AbstractContentBody {
    private final File file;
    private final String filename;

    @Deprecated
    public FileBody(File file, String filename, String mimeType, String charset) {
        this(file, ContentType.create((String)mimeType, (String)charset), filename);
    }

    @Deprecated
    public FileBody(File file, String mimeType, String charset) {
        this(file, null, mimeType, charset);
    }

    @Deprecated
    public FileBody(File file, String mimeType) {
        this(file, ContentType.create((String)mimeType), null);
    }

    public FileBody(File file) {
        this(file, ContentType.DEFAULT_BINARY, file != null ? file.getName() : null);
    }

    public FileBody(File file, ContentType contentType, String filename) {
        super(contentType);
        Args.notNull((Object)file, (String)"File");
        this.file = file;
        this.filename = filename == null ? file.getName() : filename;
    }

    public FileBody(File file, ContentType contentType) {
        this(file, contentType, file != null ? file.getName() : null);
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        Args.notNull((Object)out, (String)"Output stream");
        FileInputStream in = new FileInputStream(this.file);
        try {
            int l;
            byte[] tmp = new byte[4096];
            while ((l = ((InputStream)in).read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        }
        finally {
            ((InputStream)in).close();
        }
    }

    @Override
    public String getTransferEncoding() {
        return "binary";
    }

    @Override
    public long getContentLength() {
        return this.file.length();
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    public File getFile() {
        return this.file;
    }
}

