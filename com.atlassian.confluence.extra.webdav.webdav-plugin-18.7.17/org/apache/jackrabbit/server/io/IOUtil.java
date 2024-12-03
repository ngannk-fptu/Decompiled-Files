/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;

public final class IOUtil {
    public static final long UNDEFINED_TIME = -1L;
    public static final long UNDEFINED_LENGTH = -1L;

    private IOUtil() {
    }

    public static String getLastModified(long modificationTime) {
        if (modificationTime <= -1L) {
            modificationTime = new Date().getTime();
        }
        return HttpDateFormat.modificationDateFormat().format(new Date(modificationTime));
    }

    public static String getCreated(long createdTime) {
        if (createdTime <= -1L) {
            createdTime = 0L;
        }
        return HttpDateFormat.creationDateFormat().format(new Date(createdTime));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void spool(InputStream in, OutputStream out) throws IOException {
        try {
            int read;
            byte[] buffer = new byte[8192];
            while ((read = in.read(buffer)) >= 0) {
                out.write(buffer, 0, read);
            }
        }
        finally {
            in.close();
        }
    }

    public static String buildContentType(String mimeType, String encoding) {
        String contentType = mimeType;
        if (contentType != null && encoding != null) {
            contentType = contentType + "; charset=" + encoding;
        }
        return contentType;
    }

    public static String getMimeType(String contentType) {
        String mimeType = contentType;
        if (mimeType == null) {
            return mimeType;
        }
        int semi = mimeType.indexOf(59);
        return semi > 0 ? mimeType.substring(0, semi) : mimeType;
    }

    public static String getEncoding(String contentType) {
        int equal;
        if (contentType == null || (equal = contentType.indexOf("charset=")) == -1) {
            return null;
        }
        String encoding = contentType.substring(equal + 8);
        int semi = encoding.indexOf(59);
        if (semi != -1) {
            encoding = encoding.substring(0, semi);
        }
        return encoding;
    }

    public static File getTempFile(InputStream inputStream) throws IOException {
        int read;
        if (inputStream == null) {
            return null;
        }
        File tmpFile = File.createTempFile("__importcontext", ".tmp");
        FileOutputStream out = new FileOutputStream(tmpFile);
        byte[] buffer = new byte[8192];
        while ((read = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
        out.close();
        inputStream.close();
        return tmpFile;
    }

    public static Node mkDirs(Node root, String relPath, String dirNodeType) throws RepositoryException {
        for (String seg : Text.explode(relPath, 47)) {
            if (!root.hasNode(seg)) {
                root.addNode(seg, dirNodeType);
            }
            root = root.getNode(seg);
        }
        return root;
    }
}

