/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class FileUtil {
    public static void downloadSource(PrintStream out, PrintStream err, URL srcURL, String dirStr, boolean extract) {
        String fileName = srcURL.getFile().lastIndexOf(47) > 0 ? srcURL.getFile().substring(srcURL.getFile().lastIndexOf(47) + 1) : srcURL.getFile();
        try {
            out.println("Connecting...");
            File dir = new File(dirStr);
            if (!dir.exists()) {
                err.println("Destination directory does not exist.");
            }
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            URLConnection conn = srcURL.openConnection();
            int total = conn.getContentLength();
            InputStream is = conn.getInputStream();
            if (total > 0) {
                out.println("Downloading " + fileName + " ( " + total + " bytes ).");
            } else {
                out.println("Downloading " + fileName + ".");
            }
            byte[] buffer = new byte[4096];
            int count = 0;
            int len = is.read(buffer);
            while (len > 0) {
                count += len;
                ((OutputStream)os).write(buffer, 0, len);
                len = is.read(buffer);
            }
            ((OutputStream)os).close();
            is.close();
            if (extract) {
                is = new FileInputStream(file);
                JarInputStream jis = new JarInputStream(is);
                out.println("Extracting...");
                FileUtil.unjar(jis, dir);
                jis.close();
                file.delete();
            }
        }
        catch (Exception ex) {
            err.println(ex);
        }
    }

    public static void unjar(JarInputStream jis, File dir) throws IOException {
        byte[] buffer = new byte[4096];
        JarEntry je = jis.getNextJarEntry();
        while (je != null) {
            if (je.getName().startsWith("/")) {
                throw new IOException("JAR resource cannot contain absolute paths.");
            }
            File target = new File(dir, je.getName());
            if (je.isDirectory()) {
                if (!target.exists() && !target.mkdirs()) {
                    throw new IOException("Unable to create target directory: " + target);
                }
            } else {
                int lastIndex = je.getName().lastIndexOf(47);
                String name = lastIndex >= 0 ? je.getName().substring(lastIndex + 1) : je.getName();
                String destination = lastIndex >= 0 ? je.getName().substring(0, lastIndex) : "";
                destination = destination.replace('/', File.separatorChar);
                FileUtil.copy(jis, dir, name, destination, buffer);
            }
            je = jis.getNextJarEntry();
        }
    }

    public static void copy(InputStream is, File dir, String destName, String destDir, byte[] buffer) throws IOException {
        File targetDir;
        if (destDir == null) {
            destDir = "";
        }
        if (!(targetDir = new File(dir, destDir)).exists()) {
            if (!targetDir.mkdirs()) {
                throw new IOException("Unable to create target directory: " + targetDir);
            }
        } else if (!targetDir.isDirectory()) {
            throw new IOException("Target is not a directory: " + targetDir);
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(targetDir, destName)));
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            bos.write(buffer, 0, count);
        }
        bos.close();
    }
}

