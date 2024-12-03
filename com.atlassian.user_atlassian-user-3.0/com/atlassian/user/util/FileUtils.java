/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

public class FileUtils {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final Logger log = Logger.getLogger(FileUtils.class);

    public static void copyFile(File source, File destination) throws IOException {
        if (!source.exists()) {
            String message = "File " + source + " does not exist";
            throw new IOException(message);
        }
        if (destination.getParentFile() != null && !destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        if (destination.exists() && !destination.canWrite()) {
            String message = "Unable to open file " + destination + " for writing.";
            throw new IOException(message);
        }
        FileInputStream input = new FileInputStream(source);
        FileOutputStream output = new FileOutputStream(destination);
        FileUtils.copy(input, output);
        FileUtils.shutdownStream(input);
        FileUtils.shutdownStream(output);
        if (source.length() != destination.length()) {
            String message = "Failed to copy full contents from " + source + " to " + destination;
            throw new IOException(message);
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return FileUtils.copy(input, output, 4096);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void shutdownStream(OutputStream output) {
        if (output == null) {
            return;
        }
        try {
            output.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void shutdownStream(InputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
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
            for (int i = 0; i < files.length; ++i) {
                boolean deleted;
                File file = files[i];
                boolean bl = deleted = !file.delete();
                if (!deleted || !file.isDirectory()) continue;
                FileUtils.deleteDir(file);
            }
        }
        return dir.delete();
    }

    public static void deleteFilesBeginningWith(String directory, String prefix) {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("directory arg. is not a dir. [" + directory + "]");
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File file = files[i];
            if (!file.getName().startsWith(prefix) || file.delete()) continue;
            throw new RuntimeException("Could not delete " + file.getName());
        }
    }

    public static File copyIntoTemporaryFile(String source, String fileName) throws IOException, Exception {
        File temp = File.createTempFile(fileName, null);
        temp.deleteOnExit();
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write(source);
        out.close();
        return temp;
    }

    public static String getInputStreamTextContent(InputStream is) {
        if (is == null) {
            return null;
        }
        String result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(is.available());
            FileUtils.pump(is, baos);
            result = new String(baos.toByteArray());
            is.close();
        }
        catch (IOException e) {
            log.error((Object)("IOException reading stream: " + e), (Throwable)e);
        }
        return result;
    }

    private static void pump(InputStream is, OutputStream os) throws IOException {
        int lengthRead;
        byte[] buffer = new byte[4096];
        while ((lengthRead = is.read(buffer)) >= 0) {
            os.write(buffer, 0, lengthRead);
        }
    }
}

