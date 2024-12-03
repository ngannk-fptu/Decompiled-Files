/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.util.TreeSet;

public final class WordResolver {
    public static final int MAX_WORDS = 8192;
    static final char CHAR_NULL = '\u0000';
    static final int NEGATIVE_OFFSET = 57344;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;
    final String[] mWords;

    WordResolver(String[] words, char[] index) {
        this.mWords = words;
        this.mData = index;
    }

    public static WordResolver constructInstance(TreeSet<String> wordSet) {
        if (wordSet.size() > 8192) {
            return null;
        }
        return new Builder(wordSet).construct();
    }

    public int size() {
        return this.mWords.length;
    }

    public String find(char[] str, int start, int end) {
        char[] data = this.mData;
        if (data == null) {
            return this.findFromOne(str, start, end);
        }
        int ptr = 0;
        int offset = start;
        block0: do {
            if (offset == end) {
                if (data[ptr + 1] == '\u0000') {
                    return this.mWords[data[ptr + 2] - 57344];
                }
                return null;
            }
            char count = data[ptr++];
            char c = str[offset++];
            if (count < '\u0007') {
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                    continue;
                }
                if (data[ptr + 2] == c) {
                    ptr = data[ptr + 3];
                    continue;
                }
                int branchEnd = ptr + (count << 1);
                ptr += 4;
                while (ptr < branchEnd) {
                    if (data[ptr] == c) {
                        ptr = data[ptr + 1];
                        continue block0;
                    }
                    ptr += 2;
                }
                return null;
            }
            int low = 0;
            int high = count - '\u0001';
            while (low <= high) {
                int mid = low + high >> 1;
                int ix = ptr + (mid << 1);
                int diff = data[ix] - c;
                if (diff > 0) {
                    high = mid - 1;
                    continue;
                }
                if (diff < 0) {
                    low = mid + 1;
                    continue;
                }
                ptr = data[ix + 1];
                continue block0;
            }
            return null;
        } while (ptr < 57344);
        String word = this.mWords[ptr - 57344];
        int expLen = end - start;
        if (word.length() != expLen) {
            return null;
        }
        int i = offset - start;
        while (offset < end) {
            if (word.charAt(i) != str[offset]) {
                return null;
            }
            ++i;
            ++offset;
        }
        return word;
    }

    private String findFromOne(char[] str, int start, int end) {
        String word = this.mWords[0];
        int len = end - start;
        if (word.length() != len) {
            return null;
        }
        for (int i = 0; i < len; ++i) {
            if (word.charAt(i) == str[start + i]) continue;
            return null;
        }
        return word;
    }

    public String find(String str) {
        char[] data = this.mData;
        if (data == null) {
            String word = this.mWords[0];
            return word.equals(str) ? word : null;
        }
        int ptr = 0;
        int offset = 0;
        int end = str.length();
        block0: do {
            if (offset == end) {
                if (data[ptr + 1] == '\u0000') {
                    return this.mWords[data[ptr + 2] - 57344];
                }
                return null;
            }
            char count = data[ptr++];
            char c = str.charAt(offset++);
            if (count < '\u0007') {
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                    continue;
                }
                if (data[ptr + 2] == c) {
                    ptr = data[ptr + 3];
                    continue;
                }
                int branchEnd = ptr + (count << 1);
                ptr += 4;
                while (ptr < branchEnd) {
                    if (data[ptr] == c) {
                        ptr = data[ptr + 1];
                        continue block0;
                    }
                    ptr += 2;
                }
                return null;
            }
            int low = 0;
            int high = count - '\u0001';
            while (low <= high) {
                int mid = low + high >> 1;
                int ix = ptr + (mid << 1);
                int diff = data[ix] - c;
                if (diff > 0) {
                    high = mid - 1;
                    continue;
                }
                if (diff < 0) {
                    low = mid + 1;
                    continue;
                }
                ptr = data[ix + 1];
                continue block0;
            }
            return null;
        } while (ptr < 57344);
        String word = this.mWords[ptr - 57344];
        if (word.length() != str.length()) {
            return null;
        }
        while (offset < end) {
            if (word.charAt(offset) != str.charAt(offset)) {
                return null;
            }
            ++offset;
        }
        return word;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(16 + (this.mWords.length << 3));
        int len = this.mWords.length;
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mWords[i]);
        }
        return sb.toString();
    }

    private static final class Builder {
        final String[] mWords;
        char[] mData;
        int mSize;

        public Builder(TreeSet<String> wordSet) {
            int wordCount = wordSet.size();
            this.mWords = new String[wordCount];
            wordSet.toArray(this.mWords);
            if (wordCount < 2) {
                if (wordCount == 0) {
                    throw new IllegalArgumentException();
                }
                this.mData = null;
            } else {
                int size = wordCount * 6;
                if (size < 256) {
                    size = 256;
                }
                this.mData = new char[size];
            }
        }

        public WordResolver construct() {
            char[] result;
            if (this.mData == null) {
                result = null;
            } else {
                this.constructBranch(0, 0, this.mWords.length);
                if (this.mSize > 57344) {
                    return null;
                }
                result = new char[this.mSize];
                System.arraycopy(this.mData, 0, result, 0, this.mSize);
            }
            return new WordResolver(this.mWords, result);
        }

        private void constructBranch(int charIndex, int start, int end) {
            boolean gotRunt;
            if (this.mSize >= this.mData.length) {
                this.expand(1);
            }
            this.mData[this.mSize++] = '\u0000';
            int structStart = this.mSize + 1;
            int groupCount = 0;
            String[] words = this.mWords;
            int groupStart = start;
            if (words[groupStart].length() == charIndex) {
                if (this.mSize + 2 > this.mData.length) {
                    this.expand(2);
                }
                this.mData[this.mSize++] = '\u0000';
                this.mData[this.mSize++] = (char)(57344 + groupStart);
                ++groupStart;
                ++groupCount;
                gotRunt = true;
            } else {
                gotRunt = false;
            }
            while (groupStart < end) {
                int j;
                char c = words[groupStart].charAt(charIndex);
                for (j = groupStart + 1; j < end && words[j].charAt(charIndex) == c; ++j) {
                }
                if (this.mSize + 2 > this.mData.length) {
                    this.expand(2);
                }
                this.mData[this.mSize++] = c;
                this.mData[this.mSize++] = (char)(j - groupStart);
                groupStart = j;
                ++groupCount;
            }
            this.mData[structStart - 2] = (char)groupCount;
            groupStart = start;
            if (gotRunt) {
                structStart += 2;
                ++groupStart;
            }
            int structEnd = this.mSize;
            ++charIndex;
            while (structStart < structEnd) {
                groupCount = this.mData[structStart];
                if (groupCount == 1) {
                    this.mData[structStart] = (char)(57344 + groupStart);
                } else {
                    this.mData[structStart] = (char)this.mSize;
                    this.constructBranch(charIndex, groupStart, groupStart + groupCount);
                }
                groupStart += groupCount;
                structStart += 2;
            }
        }

        private char[] expand(int needSpace) {
            int len;
            char[] old = this.mData;
            int newSize = len + ((len = old.length) < 4096 ? len : len >> 1);
            if (newSize < this.mSize + needSpace) {
                newSize = this.mSize + needSpace + 64;
            }
            this.mData = new char[newSize];
            System.arraycopy(old, 0, this.mData, 0, len);
            return this.mData;
        }
    }
}

