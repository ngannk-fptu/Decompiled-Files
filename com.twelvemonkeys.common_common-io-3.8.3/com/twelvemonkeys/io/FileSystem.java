/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.UnixFileSystem;
import com.twelvemonkeys.io.Win32FileSystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

abstract class FileSystem {
    FileSystem() {
    }

    abstract long getFreeSpace(File var1);

    abstract long getTotalSpace(File var1);

    abstract String getName();

    static BufferedReader exec(String[] stringArray) throws IOException {
        Process process = Runtime.getRuntime().exec(stringArray);
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    static FileSystem get() {
        String string = System.getProperty("os.name");
        if ((string = string.toLowerCase()).contains("windows")) {
            return new Win32FileSystem();
        }
        if (string.contains("linux") || string.contains("sun os") || string.contains("sunos") || string.contains("solaris") || string.contains("mpe/ix") || string.contains("hp-ux") || string.contains("aix") || string.contains("freebsd") || string.contains("irix") || string.contains("digital unix") || string.contains("unix") || string.contains("mac os x")) {
            return new UnixFileSystem();
        }
        return new UnknownFileSystem(string);
    }

    private static class UnknownFileSystem
    extends FileSystem {
        private final String osName;

        UnknownFileSystem(String string) {
            this.osName = string;
        }

        @Override
        long getFreeSpace(File file) {
            return 0L;
        }

        @Override
        long getTotalSpace(File file) {
            return 0L;
        }

        @Override
        String getName() {
            return "Unknown (" + this.osName + ")";
        }
    }
}

