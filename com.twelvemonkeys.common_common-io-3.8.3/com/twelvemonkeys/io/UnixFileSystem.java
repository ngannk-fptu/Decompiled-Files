/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.util.StringTokenIterator
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.FileSystem;
import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.util.StringTokenIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

final class UnixFileSystem
extends FileSystem {
    UnixFileSystem() {
    }

    @Override
    long getFreeSpace(File file) {
        try {
            return this.getNumber(file, 3);
        }
        catch (IOException iOException) {
            return 0L;
        }
    }

    @Override
    long getTotalSpace(File file) {
        try {
            return this.getNumber(file, 5);
        }
        catch (IOException iOException) {
            return 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long getNumber(File file, int n) throws IOException {
        BufferedReader bufferedReader = UnixFileSystem.exec(new String[]{"df", "-k", file.getAbsolutePath()});
        String string = null;
        try {
            String string2;
            while ((string2 = bufferedReader.readLine()) != null) {
                string = string2;
            }
        }
        finally {
            FileUtil.close(bufferedReader);
        }
        if (string != null) {
            String string3 = null;
            StringTokenIterator stringTokenIterator = new StringTokenIterator(string, " ", -1);
            for (int i = 0; i < n && stringTokenIterator.hasNext(); ++i) {
                string3 = stringTokenIterator.nextToken();
            }
            if (string3 != null) {
                try {
                    return Long.parseLong(string3) * 1024L;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
        return 0L;
    }

    @Override
    String getName() {
        return "Unix";
    }
}

