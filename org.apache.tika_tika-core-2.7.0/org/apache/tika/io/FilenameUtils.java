/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.util.HashSet;
import java.util.Locale;

public class FilenameUtils {
    public static final char[] RESERVED_FILENAME_CHARACTERS = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '?', ':', '*', '<', '>', '|'};
    private static final HashSet<Character> RESERVED = new HashSet(38);

    public static String normalize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (RESERVED.contains(Character.valueOf(c))) {
                sb.append('%').append(c < '\u0010' ? "0" : "").append(Integer.toHexString(c).toUpperCase(Locale.ROOT));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getName(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        int unix = path.lastIndexOf("/");
        int windows = path.lastIndexOf("\\");
        int colon = path.lastIndexOf(":");
        String cand = path.substring(Math.max(colon, Math.max(unix, windows)) + 1);
        if (cand.equals("..") || cand.equals(".")) {
            return "";
        }
        return cand;
    }

    public static String getSuffixFromPath(String path) {
        String n = FilenameUtils.getName(path);
        int i = n.lastIndexOf(".");
        if (i > -1 && n.length() - i < 6) {
            return n.substring(i);
        }
        return "";
    }

    static {
        for (char reservedFilenameCharacter : RESERVED_FILENAME_CHARACTERS) {
            RESERVED.add(Character.valueOf(reservedFilenameCharacter));
        }
    }
}

