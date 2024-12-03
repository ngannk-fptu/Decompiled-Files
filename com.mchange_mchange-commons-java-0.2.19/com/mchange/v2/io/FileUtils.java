/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.io;

import com.mchange.v2.io.DirectoryDescentUtils;
import com.mchange.v2.io.FileIterator;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileUtils {
    public static File findRelativeToParent(File file, File file2) throws IOException {
        String string = file.getPath();
        String string2 = file2.getPath();
        if (!string2.startsWith(string)) {
            throw new IllegalArgumentException(string2 + " is not a child of " + string + " [no transformations or canonicalizations tried]");
        }
        String string3 = string2.substring(string.length());
        File file3 = new File(string3);
        if (file3.isAbsolute()) {
            file3 = new File(file3.getPath().substring(1));
        }
        return file3;
    }

    public static long diskSpaceUsed(File file) throws IOException {
        long l = 0L;
        FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(file);
        while (fileIterator.hasNext()) {
            File file2 = fileIterator.nextFile();
            if (!file2.isFile()) continue;
            l += file2.length();
        }
        return l;
    }

    public static void touchExisting(File file) throws IOException {
        if (file.exists()) {
            FileUtils.unguardedTouch(file);
        }
    }

    public static void touch(File file) throws IOException {
        if (!file.exists()) {
            FileUtils.createEmpty(file);
        }
        FileUtils.unguardedTouch(file);
    }

    public static void createEmpty(File file) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rws");
            randomAccessFile.setLength(0L);
        }
        finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    private static void unguardedTouch(File file) throws IOException {
        file.setLastModified(System.currentTimeMillis());
    }

    private FileUtils() {
    }
}

