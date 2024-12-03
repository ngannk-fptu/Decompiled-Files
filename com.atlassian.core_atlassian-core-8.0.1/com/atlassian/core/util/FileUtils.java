/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.core.util.zip.FolderArchiver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return IOUtils.copy((InputStream)input, (OutputStream)output);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        return IOUtils.copy((InputStream)input, (OutputStream)output);
    }

    public static void shutdownStream(OutputStream output) {
        IOUtils.closeQuietly((OutputStream)output);
    }

    public static void shutdownStream(InputStream input) {
        IOUtils.closeQuietly((InputStream)input);
    }

    public static boolean deleteDir(File dir) {
        File candir;
        if (dir == null) {
            return false;
        }
        try {
            candir = dir.getCanonicalFile();
        }
        catch (IOException e) {
            return false;
        }
        if (!candir.equals(dir.getAbsoluteFile())) {
            return false;
        }
        File[] files = candir.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean deleted;
                boolean bl = deleted = !file.delete();
                if (!deleted || !file.isDirectory()) continue;
                FileUtils.deleteDir(file);
            }
        }
        return dir.delete();
    }

    public static void recursiveDelete(File file) {
        File[] files;
        if (!file.isDirectory()) {
            file.delete();
            return;
        }
        for (File next : files = file.listFiles()) {
            FileUtils.recursiveDelete(next);
        }
        file.delete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getResourceContent(String resource) {
        InputStream is = ClassLoaderUtils.getResourceAsStream(resource, FileUtils.class);
        if (is == null) {
            return null;
        }
        try {
            String string = IOUtils.toString((InputStream)is);
            return string;
        }
        catch (IOException e) {
            log.error("IOException reading stream: " + e, (Throwable)e);
            String string = null;
            return string;
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getResourceContent(HttpServletRequest req, String resource) {
        InputStream is = req.getSession().getServletContext().getResourceAsStream(resource);
        if (is == null) {
            return "";
        }
        try {
            String result = IOUtils.toString((InputStream)is);
            String string = result == null ? "" : result;
            return string;
        }
        catch (IOException e) {
            log.error("IOException reading stream: " + e, (Throwable)e);
            String string = "";
            return string;
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }

    public static String getInputStreamTextContent(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            String string = IOUtils.toString((InputStream)is);
            return string;
        }
        catch (IOException e) {
            log.error("IOException reading stream: " + e, (Throwable)e);
            String string = null;
            return string;
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }

    public static void saveTextFile(String stringContent, File destFile) throws IOException {
        FileUtils.ensureFileAndPathExist(destFile);
        FileWriter writer = new FileWriter(destFile);
        writer.write(stringContent);
        writer.close();
    }

    public static void ensureFileAndPathExist(File file) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    public static boolean moveDir(File dirName, File destDir) {
        File destParent = new File(destDir.getParent());
        if (destDir.exists()) {
            destDir.delete();
        }
        destParent.mkdirs();
        return dirName.renameTo(destDir);
    }

    public static void createZipFile(File baseDir, File zipFile) throws Exception {
        FolderArchiver compressor = new FolderArchiver(baseDir, zipFile);
        compressor.doArchive();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List readResourcesAsList(String resource) {
        ArrayList result = new ArrayList();
        InputStream is = ClassLoaderUtils.getResourceAsStream(resource, FileUtils.class);
        try {
            result.addAll(IOUtils.readLines((InputStream)is));
        }
        catch (IOException e) {
            log.error("IOException reading stream: " + e, (Throwable)e);
            ArrayList arrayList = result;
            return arrayList;
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
        Iterator iterator = result.iterator();
        while (iterator.hasNext()) {
            String s = (String)iterator.next();
            if (!StringUtils.isBlank((CharSequence)s) && !StringUtils.trimToEmpty((String)s).startsWith("#")) continue;
            iterator.remove();
        }
        return result;
    }

    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        FileUtils.copyDirectory(srcDir, destDir, false);
    }

    public static void copyDirectory(File srcDir, File destDir, boolean overwrite) throws IOException {
        File[] files = srcDir.listFiles();
        if (!destDir.exists()) {
            destDir.mkdirs();
        } else {
            log.debug(destDir.getAbsolutePath() + " already exists");
        }
        if (files != null) {
            for (File file : files) {
                File dest = new File(destDir, file.getName());
                if (file.isFile()) {
                    FileUtils.copyFile(file, dest, overwrite);
                    continue;
                }
                FileUtils.copyDirectory(file, dest, overwrite);
            }
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileUtils.copyFile(srcFile, destFile, true);
    }

    public static void copyFile(File srcFile, File destFile, int bufferSize) throws IOException {
        FileUtils.copyFile(srcFile, destFile, true, bufferSize);
    }

    public static void copyFile(File srcFile, File destFile, boolean overwrite) throws IOException {
        FileUtils.copyFile(srcFile, destFile, overwrite, 8192);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyFile(File srcFile, File destFile, boolean overwrite, int bufferSize) throws IOException {
        if (!srcFile.exists()) {
            throw new IOException("File " + srcFile + " does not exist");
        }
        InputStream input = Files.newInputStream(srcFile.toPath(), new OpenOption[0]);
        try {
            FileUtils.copyFile(input, destFile, overwrite, bufferSize);
        }
        finally {
            IOUtils.closeQuietly((InputStream)input);
        }
        if (srcFile.length() != srcFile.length()) {
            throw new IOException("Failed to copy full contents from " + srcFile + " to " + destFile);
        }
    }

    public static void copyFile(InputStream srcStream, File destFile) throws IOException {
        FileUtils.copyFile(srcStream, destFile, false);
    }

    public static void copyFile(InputStream srcStream, File destFile, boolean overwrite) throws IOException {
        FileUtils.copyFile(srcStream, destFile, overwrite, 8192);
    }

    public static void copyFile(InputStream srcStream, File destFile, boolean overwrite, int bufferSize) throws IOException {
        File parentFile = destFile.getParentFile();
        if (!parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
        if (destFile.exists()) {
            if (!destFile.canWrite()) {
                throw new IOException("Unable to open file " + destFile + " for writing.");
            }
            if (!overwrite) {
                log.warn(destFile.getAbsolutePath() + " already exists");
                return;
            }
            log.debug("Overwriting file at: " + destFile.getAbsolutePath());
        } else {
            destFile.createNewFile();
        }
        OutputStream output = Files.newOutputStream(destFile.toPath(), new OpenOption[0]);
        try {
            IOUtils.copy((InputStream)srcStream, (OutputStream)output, (int)bufferSize);
        }
        catch (IOException e) {
            log.error("Error writing stream to file: " + destFile.getAbsolutePath());
            throw e;
        }
        finally {
            IOUtils.closeQuietly((OutputStream)output);
        }
    }
}

