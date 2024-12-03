/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.axis.attachments;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;

public class SourceDataSource
implements DataSource {
    public static final String CONTENT_TYPE = "text/xml";
    private final String name;
    private final String contentType;
    private byte[] data;
    private ByteArrayOutputStream os;

    public SourceDataSource(String name, StreamSource data) {
        this(name, CONTENT_TYPE, data);
    }

    public SourceDataSource(String name, String contentType, StreamSource data) {
        this.name = name;
        this.contentType = contentType == null ? CONTENT_TYPE : contentType;
        this.os = new ByteArrayOutputStream();
        try {
            if (data != null) {
                Reader reader = data.getReader();
                if (reader != null) {
                    int ch;
                    reader = new BufferedReader(reader);
                    while ((ch = reader.read()) != -1) {
                        this.os.write(ch);
                    }
                } else {
                    String id;
                    InputStream is = data.getInputStream();
                    if (is == null && (id = data.getSystemId()) != null) {
                        URL url = new URL(id);
                        is = url.openStream();
                    }
                    if (is != null) {
                        int avail;
                        is = new BufferedInputStream(is);
                        byte[] bytes = null;
                        while ((avail = is.available()) > 0) {
                            if (bytes == null || avail > bytes.length) {
                                bytes = new byte[avail];
                            }
                            is.read(bytes, 0, avail);
                            this.os.write(bytes, 0, avail);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    public String getName() {
        return this.name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public InputStream getInputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return new ByteArrayInputStream(this.data == null ? new byte[]{} : this.data);
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.os.size() != 0) {
            this.data = this.os.toByteArray();
            this.os.reset();
        }
        return this.os;
    }
}

