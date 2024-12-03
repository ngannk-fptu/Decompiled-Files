/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm.internal;

public class CharOperation {
    public static final char[][] NO_CHAR_CHAR = new char[0][];
    public static final char[] NO_CHAR = new char[0];

    public static final char[] subarray(char[] array, int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        char[] result = new char[end - start];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static final char[][] subarray(char[][] array, int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        char[][] result = new char[end - start][];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static final char[][] splitOn(char divider, char[] array) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR_CHAR;
        }
        int wordCount = 1;
        for (int i = 0; i < length; ++i) {
            if (array[i] != divider) continue;
            ++wordCount;
        }
        char[][] split = new char[wordCount][];
        int last = 0;
        int currentWord = 0;
        for (int i = 0; i < length; ++i) {
            if (array[i] != divider) continue;
            split[currentWord] = new char[i - last];
            System.arraycopy(array, last, split[currentWord++], 0, i - last);
            last = i + 1;
        }
        split[currentWord] = new char[length - last];
        System.arraycopy(array, last, split[currentWord], 0, length - last);
        return split;
    }

    public static final int lastIndexOf(char toBeFound, char[] array) {
        int i = array.length;
        while (--i >= 0) {
            if (toBeFound != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static final int indexOf(char toBeFound, char[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (toBeFound != array[i]) continue;
            return i;
        }
        return -1;
    }

    public static final char[] concat(char[] first, char[] second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        int length1 = first.length;
        int length2 = second.length;
        char[] result = new char[length1 + length2];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        return result;
    }

    public static final boolean equals(char[] first, char[] second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (first[i] == second[i]) continue;
            return false;
        }
        return true;
    }

    public static final String toString(char[][] array) {
        char[] result = CharOperation.concatWith(array, '.');
        return new String(result);
    }

    public static final char[] concatWith(char[][] array, char separator) {
        int length;
        int n = length = array == null ? 0 : array.length;
        if (length == 0) {
            return NO_CHAR;
        }
        int size = length - 1;
        int index = length;
        while (--index >= 0) {
            if (array[index].length == 0) {
                --size;
                continue;
            }
            size += array[index].length;
        }
        if (size <= 0) {
            return NO_CHAR;
        }
        char[] result = new char[size];
        index = length;
        while (--index >= 0) {
            length = array[index].length;
            if (length <= 0) continue;
            System.arraycopy(array[index], 0, result, size -= length, length);
            if (--size < 0) continue;
            result[size] = separator;
        }
        return result;
    }

    public static final int hashCode(char[] array) {
        int hash;
        int length = array.length;
        int n = hash = length == 0 ? 31 : array[0];
        if (length < 8) {
            int i = length;
            while (--i > 0) {
                hash = hash * 31 + array[i];
            }
        } else {
            int last;
            int i = length - 1;
            int n2 = last = i > 16 ? i - 16 : 0;
            while (i > last) {
                hash = hash * 31 + array[i];
                i -= 2;
            }
        }
        return hash & Integer.MAX_VALUE;
    }

    public static final boolean equals(char[][] first, char[][] second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (CharOperation.equals(first[i], second[i])) continue;
            return false;
        }
        return true;
    }

    public static final void replace(char[] array, char toBeReplaced, char replacementChar) {
        if (toBeReplaced != replacementChar) {
            int max = array.length;
            for (int i = 0; i < max; ++i) {
                if (array[i] != toBeReplaced) continue;
                array[i] = replacementChar;
            }
        }
    }
}

