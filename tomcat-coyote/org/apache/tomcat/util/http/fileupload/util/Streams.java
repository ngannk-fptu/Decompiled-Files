/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;

public final class Streams {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    private Streams() {
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream) throws IOException {
        return Streams.copy(inputStream, outputStream, closeOutputStream, new byte[8192]);
    }

    public static long copy(InputStream inputStream, OutputStream outputStream, boolean closeOutputStream, byte[] buffer) throws IOException {
        try (OutputStream out = outputStream;){
            long l;
            block16: {
                InputStream in = inputStream;
                try {
                    int res;
                    long total = 0L;
                    while ((res = in.read(buffer)) != -1) {
                        if (res <= 0) continue;
                        total += (long)res;
                        if (out == null) continue;
                        out.write(buffer, 0, res);
                    }
                    if (out != null) {
                        if (closeOutputStream) {
                            out.close();
                        } else {
                            out.flush();
                        }
                    }
                    in.close();
                    l = total;
                    if (in == null) break block16;
                }
                catch (Throwable throwable) {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                in.close();
            }
            return l;
        }
    }

    public static String asString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Streams.copy(inputStream, baos, true);
        return baos.toString();
    }

    public static String asString(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Streams.copy(inputStream, baos, true);
        return baos.toString(encoding);
    }

    public static String checkFileName(String fileName) {
        if (fileName != null && fileName.indexOf(0) != -1) {
            StringBuilder sb = new StringBuilder();
            block3: for (int i = 0; i < fileName.length(); ++i) {
                char c = fileName.charAt(i);
                switch (c) {
                    case '\u0000': {
                        sb.append("\\0");
                        continue block3;
                    }
                    default: {
                        sb.append(c);
                    }
                }
            }
            throw new InvalidFileNameException(fileName, "Invalid file name: " + sb);
        }
        return fileName;
    }
}

