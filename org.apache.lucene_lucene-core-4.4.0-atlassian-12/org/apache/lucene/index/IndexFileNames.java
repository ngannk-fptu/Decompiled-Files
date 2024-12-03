/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.regex.Pattern;

public final class IndexFileNames {
    public static final String SEGMENTS = "segments";
    public static final String GEN_EXTENSION = "gen";
    public static final String SEGMENTS_GEN = "segments.gen";
    public static final String COMPOUND_FILE_EXTENSION = "cfs";
    public static final String COMPOUND_FILE_ENTRIES_EXTENSION = "cfe";
    public static final String[] INDEX_EXTENSIONS = new String[]{"cfs", "cfe", "gen"};
    public static final Pattern CODEC_FILE_PATTERN = Pattern.compile("_[a-z0-9]+(_.*)?\\..*");

    private IndexFileNames() {
    }

    public static String fileNameFromGeneration(String base, String ext, long gen) {
        if (gen == -1L) {
            return null;
        }
        if (gen == 0L) {
            return IndexFileNames.segmentFileName(base, "", ext);
        }
        assert (gen > 0L);
        StringBuilder res = new StringBuilder(base.length() + 6 + ext.length()).append(base).append('_').append(Long.toString(gen, 36));
        if (ext.length() > 0) {
            res.append('.').append(ext);
        }
        return res.toString();
    }

    public static String segmentFileName(String segmentName, String segmentSuffix, String ext) {
        if (ext.length() > 0 || segmentSuffix.length() > 0) {
            assert (!ext.startsWith("."));
            StringBuilder sb = new StringBuilder(segmentName.length() + 2 + segmentSuffix.length() + ext.length());
            sb.append(segmentName);
            if (segmentSuffix.length() > 0) {
                sb.append('_').append(segmentSuffix);
            }
            if (ext.length() > 0) {
                sb.append('.').append(ext);
            }
            return sb.toString();
        }
        return segmentName;
    }

    public static boolean matchesExtension(String filename, String ext) {
        return filename.endsWith("." + ext);
    }

    private static int indexOfSegmentName(String filename) {
        int idx = filename.indexOf(95, 1);
        if (idx == -1) {
            idx = filename.indexOf(46);
        }
        return idx;
    }

    public static String stripSegmentName(String filename) {
        int idx = IndexFileNames.indexOfSegmentName(filename);
        if (idx != -1) {
            filename = filename.substring(idx);
        }
        return filename;
    }

    public static String parseSegmentName(String filename) {
        int idx = IndexFileNames.indexOfSegmentName(filename);
        if (idx != -1) {
            filename = filename.substring(0, idx);
        }
        return filename;
    }

    public static String stripExtension(String filename) {
        int idx = filename.indexOf(46);
        if (idx != -1) {
            filename = filename.substring(0, idx);
        }
        return filename;
    }
}

