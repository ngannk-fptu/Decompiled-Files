/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class FileUtils {
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    FileUtils() {
    }

    public static File toFile(URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        String fileName = url.getFile().replace('/', File.separatorChar);
        fileName = FileUtils.decodeUrl(fileName);
        return new File(fileName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String decodeUrl(String url) {
        String decoded = url;
        if (url != null && url.indexOf(37) >= 0) {
            int n = url.length();
            StringBuilder buffer = new StringBuilder();
            ByteBuffer bytes = ByteBuffer.allocate(n);
            int i = 0;
            while (i < n) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            bytes.put(octet);
                        } while ((i += 3) < n && url.charAt(i) == '%');
                        continue;
                    }
                    catch (RuntimeException runtimeException) {
                    }
                    finally {
                        if (bytes.position() <= 0) continue;
                        bytes.flip();
                        buffer.append(UTF8.decode(bytes));
                        bytes.clear();
                        continue;
                    }
                }
                buffer.append(url.charAt(i++));
            }
            decoded = buffer.toString();
        }
        return decoded;
    }
}

