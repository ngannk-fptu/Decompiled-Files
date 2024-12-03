/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.matcher;

import java.util.Arrays;
import org.apache.commons.text.matcher.StringMatcher;

abstract class AbstractStringMatcher
implements StringMatcher {
    protected AbstractStringMatcher() {
    }

    static final class TrimMatcher
    extends AbstractStringMatcher {
        private static final int SPACE_INT = 32;

        TrimMatcher() {
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            return buffer[start] <= ' ' ? 1 : 0;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            return buffer.charAt(start) <= ' ' ? 1 : 0;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    static final class NoneMatcher
    extends AbstractStringMatcher {
        NoneMatcher() {
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            return 0;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            return 0;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    static final class CharSetMatcher
    extends AbstractStringMatcher {
        private final char[] chars;

        CharSetMatcher(char[] chars) {
            this.chars = (char[])chars.clone();
            Arrays.sort(this.chars);
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            return Arrays.binarySearch(this.chars, buffer[start]) >= 0 ? 1 : 0;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            return Arrays.binarySearch(this.chars, buffer.charAt(start)) >= 0 ? 1 : 0;
        }

        @Override
        public int size() {
            return 1;
        }

        public String toString() {
            return super.toString() + Arrays.toString(this.chars);
        }
    }

    static final class CharMatcher
    extends AbstractStringMatcher {
        private final char ch;

        CharMatcher(char ch) {
            this.ch = ch;
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            return this.ch == buffer[start] ? 1 : 0;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            return this.ch == buffer.charAt(start) ? 1 : 0;
        }

        @Override
        public int size() {
            return 1;
        }

        public String toString() {
            return super.toString() + "['" + this.ch + "']";
        }
    }

    static final class CharArrayMatcher
    extends AbstractStringMatcher {
        private final char[] chars;
        private final String string;

        CharArrayMatcher(char ... chars) {
            this.string = String.valueOf(chars);
            this.chars = (char[])chars.clone();
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            int len = this.size();
            if (start + len > bufferEnd) {
                return 0;
            }
            int j = start;
            int i = 0;
            while (i < len) {
                if (this.chars[i] != buffer[j]) {
                    return 0;
                }
                ++i;
                ++j;
            }
            return len;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            int len = this.size();
            if (start + len > bufferEnd) {
                return 0;
            }
            int j = start;
            int i = 0;
            while (i < len) {
                if (this.chars[i] != buffer.charAt(j)) {
                    return 0;
                }
                ++i;
                ++j;
            }
            return len;
        }

        @Override
        public int size() {
            return this.chars.length;
        }

        public String toString() {
            return super.toString() + "[\"" + this.string + "\"]";
        }
    }

    static final class AndStringMatcher
    extends AbstractStringMatcher {
        private final StringMatcher[] stringMatchers;

        AndStringMatcher(StringMatcher ... stringMatchers) {
            this.stringMatchers = (StringMatcher[])stringMatchers.clone();
        }

        @Override
        public int isMatch(char[] buffer, int start, int bufferStart, int bufferEnd) {
            int total = 0;
            int curStart = start;
            for (StringMatcher stringMatcher : this.stringMatchers) {
                if (stringMatcher == null) continue;
                int len = stringMatcher.isMatch(buffer, curStart, bufferStart, bufferEnd);
                if (len == 0) {
                    return 0;
                }
                total += len;
                curStart += len;
            }
            return total;
        }

        @Override
        public int isMatch(CharSequence buffer, int start, int bufferStart, int bufferEnd) {
            int total = 0;
            int curStart = start;
            for (StringMatcher stringMatcher : this.stringMatchers) {
                if (stringMatcher == null) continue;
                int len = stringMatcher.isMatch(buffer, curStart, bufferStart, bufferEnd);
                if (len == 0) {
                    return 0;
                }
                total += len;
                curStart += len;
            }
            return total;
        }

        @Override
        public int size() {
            int total = 0;
            for (StringMatcher stringMatcher : this.stringMatchers) {
                if (stringMatcher == null) continue;
                total += stringMatcher.size();
            }
            return total;
        }
    }
}

