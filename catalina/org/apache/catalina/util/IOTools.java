/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class IOTools {
    protected static final int DEFAULT_BUFFER_SIZE = 4096;

    private IOTools() {
    }

    public static void flow(Reader reader, Writer writer, char[] buf) throws IOException {
        int numRead;
        while ((numRead = reader.read(buf)) >= 0) {
            writer.write(buf, 0, numRead);
        }
    }

    public static void flow(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[4096];
        IOTools.flow(reader, writer, buf);
    }

    public static void flow(InputStream is, OutputStream os) throws IOException {
        int numRead;
        byte[] buf = new byte[4096];
        while ((numRead = is.read(buf)) >= 0) {
            if (os == null) continue;
            os.write(buf, 0, numRead);
        }
    }

    public static int readFully(InputStream is, byte[] buf) throws IOException {
        int bytesRead;
        int read;
        for (bytesRead = 0; bytesRead < buf.length && (read = is.read(buf, bytesRead, buf.length - bytesRead)) >= 0; bytesRead += read) {
        }
        return bytesRead;
    }
}

