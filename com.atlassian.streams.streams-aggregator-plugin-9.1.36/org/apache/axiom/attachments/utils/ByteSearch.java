/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments.utils;

import java.util.Arrays;

public class ByteSearch {
    public static int skipSearch(byte[] pattern, boolean direction, byte[] buffer, int start, int end, short[] skip) {
        int patternLength = pattern.length;
        if (patternLength > end - start) {
            return -1;
        }
        if (direction) {
            int k = 0;
            for (k = start + patternLength - 1; k < end; k += skip[buffer[k] & 0xFF]) {
                if (!ByteSearch.isEqual(pattern, buffer, k - patternLength + 1, end)) continue;
                return k - patternLength + 1;
            }
        } else {
            for (int k = end - patternLength; k <= start; k -= skip[buffer[k] & 0xFF]) {
                if (!ByteSearch.isEqual(pattern, buffer, k, end)) continue;
                return k;
            }
        }
        return -1;
    }

    public static short[] getSkipArray(byte[] pattern, boolean direction) {
        short[] skip = new short[256];
        Arrays.fill(skip, (short)pattern.length);
        if (direction) {
            for (int k = 0; k < pattern.length - 1; ++k) {
                skip[pattern[k] & 0xFF] = (short)(pattern.length - k - 1);
            }
        } else {
            for (int k = pattern.length - 2; k >= 0; --k) {
                skip[pattern[k] & 0xFF] = (short)(pattern.length - k - 1);
            }
        }
        return skip;
    }

    public static boolean isEqual(byte[] pattern, byte[] buffer, int start, int end) {
        if (pattern.length > end - start) {
            return false;
        }
        for (int j = 0; j < pattern.length; ++j) {
            if (pattern[j] == buffer[start + j]) continue;
            return false;
        }
        return true;
    }

    public static int search(byte[] search, byte[] bytes, int start, int end, boolean direction) {
        int idx = -1;
        if (search == null || search.length == 0 || bytes == null || bytes.length == 0 || start < 0 || end <= 0) {
            return idx;
        }
        if (direction) {
            for (int i = start; idx < 0 && i < end; ++i) {
                if (bytes[i] != search[0]) continue;
                boolean found = true;
                for (int i2 = 1; found && i2 < search.length; ++i2) {
                    found = i + i2 >= end ? false : bytes[i + i2] == search[i2];
                }
                if (!found) continue;
                idx = i;
            }
        } else {
            for (int i = end - 1; idx < 0 && i >= start; --i) {
                if (bytes[i] != search[0]) continue;
                boolean found = true;
                for (int i2 = 1; found && i2 < search.length; ++i2) {
                    found = i + i2 >= end ? false : bytes[i + i2] == search[i2];
                }
                if (!found) continue;
                idx = i;
            }
        }
        return idx;
    }
}

