/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.FileSystem;
import com.twelvemonkeys.io.FileUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

final class Win32FileSystem
extends FileSystem {
    Win32FileSystem() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getFreeSpace(File file) {
        try {
            int n;
            int n2;
            BufferedReader bufferedReader = Win32FileSystem.exec(new String[]{"CMD.EXE", "/C", "DIR", "/-C", file.getAbsolutePath()});
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
            if (string != null && (n2 = string.lastIndexOf(32, (n = string.lastIndexOf(" bytes free")) - 1)) >= 0 && n >= 0) {
                try {
                    return Long.parseLong(string.substring(n2 + 1, n));
                }
                catch (NumberFormatException numberFormatException) {}
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return 0L;
    }

    @Override
    long getTotalSpace(File file) {
        return this.getFreeSpace(file);
    }

    @Override
    String getName() {
        return "Win32";
    }
}

