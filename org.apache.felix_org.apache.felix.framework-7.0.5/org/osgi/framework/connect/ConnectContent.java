/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.connect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface ConnectContent {
    public static final String TAG_OSGI_CONNECT = "osgi.connect";

    public Optional<Map<String, String>> getHeaders();

    public Iterable<String> getEntries() throws IOException;

    public Optional<ConnectEntry> getEntry(String var1);

    public Optional<ClassLoader> getClassLoader();

    public void open() throws IOException;

    public void close() throws IOException;

    @ConsumerType
    public static interface ConnectEntry {
        public String getName();

        public long getContentLength();

        public long getLastModified();

        default public byte[] getBytes() throws IOException {
            long longLength = this.getContentLength();
            if (longLength > 0x7FFFFFF7L) {
                throw new IOException("Entry is to big to fit into a byte[]: " + this.getName());
            }
            try (InputStream in = this.getInputStream();){
                int nRead;
                int length = (int)longLength;
                if (length > 0) {
                    byte[] result = new byte[length];
                    int readcount = 0;
                    for (int bytesread = 0; bytesread < length; bytesread += readcount) {
                        readcount = in.read(result, bytesread, length - bytesread);
                        if (readcount > 0) continue;
                    }
                    byte[] byArray = result;
                    return byArray;
                }
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                while ((nRead = in.read(data, 0, data.length)) > 0) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] byArray = buffer.toByteArray();
                return byArray;
            }
        }

        public InputStream getInputStream() throws IOException;
    }
}

