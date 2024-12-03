/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.archive.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import org.hibernate.boot.archive.spi.ArchiveException;
import org.jboss.logging.Logger;

public class ArchiveHelper {
    private static final Logger log = Logger.getLogger(ArchiveHelper.class);

    public static URL getJarURLFromURLEntry(URL url, String entry) throws IllegalArgumentException {
        URL jarUrl;
        String file = url.getFile();
        if (!entry.startsWith("/")) {
            entry = "/" + entry;
        }
        if ((file = file.substring(0, file.length() - entry.length())).endsWith("!")) {
            file = file.substring(0, file.length() - 1);
        }
        try {
            String protocol = url.getProtocol();
            if ("jar".equals(protocol) || "wsjar".equals(protocol)) {
                jarUrl = new URL(file);
                if ("file".equals(jarUrl.getProtocol()) && file.indexOf(32) != -1) {
                    jarUrl = new File(jarUrl.getFile()).toURI().toURL();
                }
            } else if ("zip".equals(protocol) || "code-source".equals(url.getProtocol()) || "file".equals(protocol)) {
                jarUrl = file.indexOf(32) != -1 ? new File(file).toURI().toURL() : new File(file).toURL();
            } else {
                try {
                    jarUrl = new URL(protocol, url.getHost(), url.getPort(), file);
                }
                catch (MalformedURLException e) {
                    jarUrl = url;
                }
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to determine JAR Url from " + url + ". Cause: " + e.getMessage());
        }
        log.trace((Object)("JAR URL from URL Entry: " + url + " >> " + jarUrl));
        return jarUrl;
    }

    public static URL getURLFromPath(String jarPath) {
        URL jarUrl;
        try {
            jarUrl = new URL(jarPath);
        }
        catch (MalformedURLException e) {
            try {
                jarUrl = new URL("file:" + jarPath);
            }
            catch (MalformedURLException ee) {
                throw new IllegalArgumentException("Unable to find jar:" + jarPath, ee);
            }
        }
        return jarUrl;
    }

    public static byte[] getBytesFromInputStreamSafely(InputStream inputStream) throws ArchiveException {
        try {
            return ArchiveHelper.getBytesFromInputStream(inputStream);
        }
        catch (IOException e) {
            throw new ArchiveException("Unable to extract bytes from InputStream", e);
        }
    }

    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        int size;
        LinkedList<byte[]> data = new LinkedList<byte[]>();
        int bufferSize = 4096;
        byte[] tmpByte = new byte[4096];
        int offset = 0;
        int total = 0;
        while ((size = inputStream.read(tmpByte, offset, 4096 - offset)) != -1) {
            if ((offset += size) != tmpByte.length) continue;
            data.add(tmpByte);
            tmpByte = new byte[4096];
            offset = 0;
            total += tmpByte.length;
        }
        byte[] result = new byte[total + offset];
        int count = 0;
        for (byte[] arr : data) {
            System.arraycopy(arr, 0, result, count * arr.length, arr.length);
            ++count;
        }
        System.arraycopy(tmpByte, 0, result, count * tmpByte.length, offset);
        return result;
    }

    private ArchiveHelper() {
    }
}

