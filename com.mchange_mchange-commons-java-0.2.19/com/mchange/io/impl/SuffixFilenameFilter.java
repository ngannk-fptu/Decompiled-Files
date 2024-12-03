/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import java.io.File;
import java.io.FilenameFilter;

public class SuffixFilenameFilter
implements FilenameFilter {
    public static final int ALWAYS = 0;
    public static final int NEVER = 1;
    public static final int MATCH = 2;
    String[] suffixes = null;
    int accept_dirs;

    public SuffixFilenameFilter(String[] stringArray, int n) {
        this.suffixes = stringArray;
        this.accept_dirs = n;
    }

    public SuffixFilenameFilter(String string, int n) {
        this.suffixes = new String[]{string};
        this.accept_dirs = n;
    }

    @Override
    public boolean accept(File file, String string) {
        if (this.accept_dirs != 2 && new File(file, string).isDirectory()) {
            return this.accept_dirs == 0;
        }
        int n = this.suffixes.length;
        while (--n >= 0) {
            if (!string.endsWith(this.suffixes[n])) continue;
            return true;
        }
        return false;
    }
}

