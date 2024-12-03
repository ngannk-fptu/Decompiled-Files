/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import java.io.File;
import java.io.FilenameFilter;

public class EndsWithFilenameFilter
implements FilenameFilter {
    public static final int ALWAYS = 0;
    public static final int NEVER = 1;
    public static final int MATCH = 2;
    String[] endings = null;
    int accept_dirs;

    public EndsWithFilenameFilter(String[] stringArray, int n) {
        this.endings = stringArray;
        this.accept_dirs = n;
    }

    public EndsWithFilenameFilter(String string, int n) {
        this.endings = new String[]{string};
        this.accept_dirs = n;
    }

    @Override
    public boolean accept(File file, String string) {
        if (this.accept_dirs != 2 && new File(file, string).isDirectory()) {
            return this.accept_dirs == 0;
        }
        int n = this.endings.length;
        while (--n >= 0) {
            if (!string.endsWith(this.endings[n])) continue;
            return true;
        }
        return false;
    }
}

