/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.Win32Lnk;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

final class Win32File
extends File {
    private static final boolean IS_WINDOWS = Win32File.isWindows();

    private static boolean isWindows() {
        try {
            String string = System.getProperty("os.name");
            return string.toLowerCase().indexOf("windows") >= 0;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private Win32File(File file) {
        super(file.getPath());
    }

    public static void main(String[] stringArray) {
        int n = 0;
        boolean bl = false;
        while (stringArray.length > n + 1 && stringArray[n].charAt(0) == '-' && stringArray[n].length() > 1) {
            if (stringArray[n].charAt(1) == 'R' || stringArray[n].equals("--recursive")) {
                bl = true;
            } else {
                System.err.println("Unknown option: " + stringArray[n]);
            }
            ++n;
        }
        File file = Win32File.wrap(new File(stringArray[n]));
        System.out.println("file: " + file);
        System.out.println("file.getClass(): " + file.getClass());
        Win32File.listFiles(file, 0, bl);
    }

    private static void listFiles(File file, int n, boolean bl) {
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (int i = 0; i < n; ++i) {
                System.out.print(" ");
            }
            System.out.println("Contents of " + file + ": ");
            for (File file2 : fileArray) {
                for (int i = 0; i < n; ++i) {
                    System.out.print(" ");
                }
                System.out.println("  " + file2);
                if (!bl) continue;
                Win32File.listFiles(file2, n + 1, n < 4);
            }
        }
    }

    public static File wrap(File file) {
        if (file == null) {
            return null;
        }
        if (IS_WINDOWS) {
            if (file instanceof Win32File || file instanceof Win32Lnk) {
                return file;
            }
            if (file.exists() && file.getName().endsWith(".lnk")) {
                try {
                    return new Win32Lnk(file);
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            }
            return new Win32File(file);
        }
        return file;
    }

    public static File[] wrap(File[] fileArray) {
        if (IS_WINDOWS) {
            for (int i = 0; fileArray != null && i < fileArray.length; ++i) {
                fileArray[i] = Win32File.wrap(fileArray[i]);
            }
        }
        return fileArray;
    }

    @Override
    public File getAbsoluteFile() {
        return Win32File.wrap(super.getAbsoluteFile());
    }

    @Override
    public File getCanonicalFile() throws IOException {
        return Win32File.wrap(super.getCanonicalFile());
    }

    @Override
    public File getParentFile() {
        return Win32File.wrap(super.getParentFile());
    }

    @Override
    public File[] listFiles() {
        return Win32File.wrap(super.listFiles());
    }

    @Override
    public File[] listFiles(FileFilter fileFilter) {
        return Win32File.wrap(super.listFiles(fileFilter));
    }

    @Override
    public File[] listFiles(FilenameFilter filenameFilter) {
        return Win32File.wrap(super.listFiles(filenameFilter));
    }
}

