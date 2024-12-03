/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io.impl;

import java.io.File;
import java.io.FilenameFilter;

public class StartsWithFilenameFilter
implements FilenameFilter {
    public static final int ALWAYS = 0;
    public static final int NEVER = 1;
    public static final int MATCH = 2;
    String[] beginnings = null;
    int accept_dirs;

    public StartsWithFilenameFilter(String[] stringArray, int n) {
        this.beginnings = stringArray;
        this.accept_dirs = n;
    }

    public StartsWithFilenameFilter(String string, int n) {
        this.beginnings = new String[]{string};
        this.accept_dirs = n;
    }

    @Override
    public boolean accept(File file, String string) {
        if (this.accept_dirs != 2 && new File(file, string).isDirectory()) {
            return this.accept_dirs == 0;
        }
        int n = this.beginnings.length;
        while (--n >= 0) {
            if (!string.startsWith(this.beginnings[n])) continue;
            return true;
        }
        return false;
    }
}

