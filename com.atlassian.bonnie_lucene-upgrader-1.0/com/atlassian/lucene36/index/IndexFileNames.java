/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import java.util.regex.Pattern;

public final class IndexFileNames {
    public static final String SEGMENTS = "segments";
    public static final String SEGMENTS_GEN = "segments.gen";
    public static final String DELETABLE = "deletable";
    public static final String NORMS_EXTENSION = "nrm";
    public static final String FREQ_EXTENSION = "frq";
    public static final String PROX_EXTENSION = "prx";
    public static final String TERMS_EXTENSION = "tis";
    public static final String TERMS_INDEX_EXTENSION = "tii";
    public static final String FIELDS_INDEX_EXTENSION = "fdx";
    public static final String FIELDS_EXTENSION = "fdt";
    public static final String VECTORS_FIELDS_EXTENSION = "tvf";
    public static final String VECTORS_DOCUMENTS_EXTENSION = "tvd";
    public static final String VECTORS_INDEX_EXTENSION = "tvx";
    public static final String COMPOUND_FILE_EXTENSION = "cfs";
    public static final String COMPOUND_FILE_STORE_EXTENSION = "cfx";
    public static final String DELETES_EXTENSION = "del";
    public static final String FIELD_INFOS_EXTENSION = "fnm";
    public static final String PLAIN_NORMS_EXTENSION = "f";
    public static final String SEPARATE_NORMS_EXTENSION = "s";
    public static final String GEN_EXTENSION = "gen";
    public static final String[] INDEX_EXTENSIONS = new String[]{"cfs", "fnm", "fdx", "fdt", "tii", "tis", "frq", "prx", "del", "tvx", "tvd", "tvf", "gen", "nrm", "cfx"};
    public static final String[] INDEX_EXTENSIONS_IN_COMPOUND_FILE = new String[]{"fnm", "fdx", "fdt", "tii", "tis", "frq", "prx", "tvx", "tvd", "tvf", "nrm"};
    public static final String[] STORE_INDEX_EXTENSIONS = new String[]{"tvx", "tvf", "tvd", "fdx", "fdt"};
    public static final String[] NON_STORE_INDEX_EXTENSIONS = new String[]{"fnm", "frq", "prx", "tis", "tii", "nrm"};
    public static final String[] COMPOUND_EXTENSIONS = new String[]{"fnm", "frq", "prx", "fdx", "fdt", "tii", "tis"};
    public static final String[] VECTOR_EXTENSIONS = new String[]{"tvx", "tvd", "tvf"};

    public static final String fileNameFromGeneration(String base, String ext, long gen) {
        if (gen == -1L) {
            return null;
        }
        if (gen == 0L) {
            return IndexFileNames.segmentFileName(base, ext);
        }
        StringBuilder res = new StringBuilder(base.length() + 6 + ext.length()).append(base).append('_').append(Long.toString(gen, 36));
        if (ext.length() > 0) {
            res.append('.').append(ext);
        }
        return res.toString();
    }

    public static final boolean isDocStoreFile(String fileName) {
        if (fileName.endsWith(COMPOUND_FILE_STORE_EXTENSION)) {
            return true;
        }
        for (String ext : STORE_INDEX_EXTENSIONS) {
            if (!fileName.endsWith(ext)) continue;
            return true;
        }
        return false;
    }

    public static final String segmentFileName(String segmentName, String ext) {
        if (ext.length() > 0) {
            return new StringBuilder(segmentName.length() + 1 + ext.length()).append(segmentName).append('.').append(ext).toString();
        }
        return segmentName;
    }

    public static final boolean matchesExtension(String filename, String ext) {
        return filename.endsWith("." + ext);
    }

    public static final String stripSegmentName(String filename) {
        int idx = filename.indexOf(95, 1);
        if (idx == -1) {
            idx = filename.indexOf(46);
        }
        if (idx != -1) {
            filename = filename.substring(idx);
        }
        return filename;
    }

    public static boolean isSeparateNormsFile(String filename) {
        int idx = filename.lastIndexOf(46);
        if (idx == -1) {
            return false;
        }
        String ext = filename.substring(idx + 1);
        return Pattern.matches("s[0-9]+", ext);
    }
}

