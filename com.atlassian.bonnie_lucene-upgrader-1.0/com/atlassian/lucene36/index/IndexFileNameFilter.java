/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexFileNames;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

public class IndexFileNameFilter
implements FilenameFilter {
    private static IndexFileNameFilter singleton = new IndexFileNameFilter();
    private HashSet<String> extensions = new HashSet();
    private HashSet<String> extensionsInCFS;

    private IndexFileNameFilter() {
        for (String ext : IndexFileNames.INDEX_EXTENSIONS) {
            this.extensions.add(ext);
        }
        this.extensionsInCFS = new HashSet();
        for (String ext : IndexFileNames.INDEX_EXTENSIONS_IN_COMPOUND_FILE) {
            this.extensionsInCFS.add(ext);
        }
    }

    public boolean accept(File dir, String name) {
        int i = name.lastIndexOf(46);
        if (i != -1) {
            String extension = name.substring(1 + i);
            if (this.extensions.contains(extension)) {
                return true;
            }
            if (extension.startsWith("f") && extension.matches("f\\d+")) {
                return true;
            }
            if (extension.startsWith("s") && extension.matches("s\\d+")) {
                return true;
            }
        } else {
            if (name.equals("deletable")) {
                return true;
            }
            if (name.startsWith("segments")) {
                return true;
            }
        }
        return false;
    }

    public boolean isCFSFile(String name) {
        int i = name.lastIndexOf(46);
        if (i != -1) {
            String extension = name.substring(1 + i);
            if (this.extensionsInCFS.contains(extension)) {
                return true;
            }
            if (extension.startsWith("f") && extension.matches("f\\d+")) {
                return true;
            }
        }
        return false;
    }

    public static IndexFileNameFilter getFilter() {
        return singleton;
    }
}

