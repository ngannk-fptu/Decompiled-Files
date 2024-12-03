/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.xhtmlrenderer.util.XRLog;

public class IOUtil {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static File copyFile(File page, File outputDir) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            int len;
            in = new BufferedInputStream(new FileInputStream(page));
            File outputFile = new File(outputDir, page.getName());
            out = new BufferedOutputStream(new FileOutputStream(outputFile));
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.flush();
            out.close();
            File file = outputFile;
            return file;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static void deleteAllFiles(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File file = files[i];
            if (file.delete()) continue;
            throw new IOException("Cleanup directory " + dir + ", can't delete file " + file);
        }
    }

    public static InputStream openStreamAtUrl(String uri) {
        InputStream is = null;
        try {
            URLConnection uc = new URL(uri).openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(10000));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(30000));
            uc.connect();
            is = uc.getInputStream();
        }
        catch (MalformedURLException e) {
            XRLog.exception("bad URL given: " + uri, e);
        }
        catch (FileNotFoundException e) {
            XRLog.exception("item at URI " + uri + " not found");
        }
        catch (IOException e) {
            XRLog.exception("IO problem for " + uri, e);
        }
        return is;
    }
}

