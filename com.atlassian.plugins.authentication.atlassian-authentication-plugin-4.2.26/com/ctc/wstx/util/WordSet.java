/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import java.util.TreeSet;

public final class WordSet {
    static final char CHAR_NULL = '\u0000';
    static final int NEGATIVE_OFFSET = 49152;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;

    private WordSet(char[] data) {
        this.mData = data;
    }

    public static WordSet constructSet(TreeSet<String> wordSet) {
        return new WordSet(new Builder(wordSet).construct());
    }

    public static char[] constructRaw(TreeSet<String> wordSet) {
        return new Builder(wordSet).construct();
    }

    public boolean contains(char[] buf, int start, int end) {
        return WordSet.contains(this.mData, buf, start, end);
    }

    public static boolean contains(char[] data, char[] str, int start, int end) {
        int ptr = 0;
        block0: do {
            char count;
            int left;
            if ((left = end - start) == 0) {
                return data[ptr + 1] == '\u0000';
            }
            if ((count = data[ptr++]) >= '\uc000') {
                int expCount = count - 49152;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr] != str[start]) {
                        return false;
                    }
                    ++ptr;
                    ++start;
                }
                return true;
            }
            char c = str[start++];
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
                return false;
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
            return false;
        } while (ptr != 0);
        return start == end;
    }

    public boolean contains(String str) {
        return WordSet.contains(this.mData, str);
    }

    public static boolean contains(char[] data, String str) {
        int ptr = 0;
        int start = 0;
        int end = str.length();
        block0: do {
            char count;
            int left;
            if ((left = end - start) == 0) {
                return data[ptr + 1] == '\u0000';
            }
            if ((count = data[ptr++]) >= '\uc000') {
                int expCount = count - 49152;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr] != str.charAt(start)) {
                        return false;
                    }
                    ++ptr;
                    ++start;
                }
                return true;
            }
            char c = str.charAt(start++);
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
                return false;
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
            return false;
        } while (ptr != 0);
        return start == end;
    }

    private static final class Builder {
        final String[] mWords;
        char[] mData;
        int mSize;

        public Builder(TreeSet<String> wordSet) {
            int wordCount = wordSet.size();
            this.mWords = new String[wordCount];
            wordSet.toArray(this.mWords);
            int size = wordCount * 12;
            if (size < 256) {
                size = 256;
            }
            this.mData = new char[size];
        }

        public char[] construct() {
            if (this.mWords.length == 1) {
                this.constructLeaf(0, 0);
            } else {
                this.constructBranch(0, 0, this.mWords.length);
            }
            char[] result = new char[this.mSize];
            System.arraycopy(this.mData, 0, result, 0, this.mSize);
            return result;
        }

        private void constructBranch(int charIndex, int start, int end) {
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
                this.mData[this.mSize++] = '\u0000';
                ++groupStart;
                ++groupCount;
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
            if (this.mData[structStart] == '\u0000') {
                structStart += 2;
                ++groupStart;
            }
            int structEnd = this.mSize;
            ++charIndex;
            while (structStart < structEnd) {
                groupCount = this.mData[structStart];
                this.mData[structStart] = (char)this.mSize;
                if (groupCount == 1) {
                    String word = words[groupStart];
                    if (word.length() == charIndex) {
                        this.mData[structStart] = '\u0000';
                    } else {
                        this.constructLeaf(charIndex, groupStart);
                    }
                } else {
                    this.constructBranch(charIndex, groupStart, groupStart + groupCount);
                }
                groupStart += groupCount;
                structStart += 2;
            }
        }

        private void constructLeaf(int charIndex, int wordIndex) {
            char[] data;
            String word = this.mWords[wordIndex];
            int len = word.length();
            if (this.mSize + len + 1 >= (data = this.mData).length) {
                data = this.expand(len + 1);
            }
            data[this.mSize++] = (char)(49152 + (len - charIndex));
            while (charIndex < len) {
                data[this.mSize++] = word.charAt(charIndex);
                ++charIndex;
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

