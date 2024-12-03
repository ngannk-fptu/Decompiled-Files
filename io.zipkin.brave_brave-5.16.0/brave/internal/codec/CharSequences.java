/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

public final class CharSequences {
    public static boolean regionMatches(CharSequence expected, CharSequence input, int beginIndex, int endIndex) {
        if (expected == null) {
            throw new NullPointerException("expected == null");
        }
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        int regionLength = CharSequences.regionLength(input.length(), beginIndex, endIndex);
        if (expected.length() > regionLength) {
            return false;
        }
        int i = 0;
        int inputIndex = beginIndex;
        while (i < regionLength) {
            if (expected.charAt(i) != input.charAt(inputIndex)) {
                return false;
            }
            ++i;
            ++inputIndex;
        }
        return true;
    }

    public static CharSequence withoutSubSequence(CharSequence input, int beginIndex, int endIndex) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        int length = input.length();
        int skippedRegionLength = CharSequences.regionLength(length, beginIndex, endIndex);
        if (skippedRegionLength == 0) {
            return input;
        }
        if (beginIndex == 0 && endIndex == length) {
            return "";
        }
        if (beginIndex == 0) {
            return new SubSequence(input, endIndex, length);
        }
        if (endIndex == length) {
            return new SubSequence(input, 0, beginIndex);
        }
        return new WithoutSubSequence(input, 0, beginIndex, endIndex, length);
    }

    static int regionLength(int inputLength, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("beginIndex < 0");
        }
        if (endIndex < 0) {
            throw new IndexOutOfBoundsException("endIndex < 0");
        }
        if (beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("beginIndex > endIndex");
        }
        int regionLength = endIndex - beginIndex;
        if (endIndex > inputLength) {
            throw new IndexOutOfBoundsException("endIndex > input");
        }
        return regionLength;
    }

    static final class WithoutSubSequence
    implements CharSequence {
        final CharSequence input;
        final int begin;
        final int beginSkip;
        final int endSkip;
        final int end;
        final int skipLength;
        final int length;

        WithoutSubSequence(CharSequence input, int begin, int beginSkip, int endSkip, int end) {
            this.input = input;
            this.begin = begin;
            this.beginSkip = beginSkip;
            this.endSkip = endSkip;
            this.end = end;
            this.skipLength = endSkip - beginSkip;
            this.length = end - begin - this.skipLength;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public char charAt(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("index < 0");
            }
            if (index >= this.length) {
                throw new IndexOutOfBoundsException("index >= length");
            }
            if ((index += this.begin) >= this.beginSkip) {
                index += this.skipLength;
            }
            return this.input.charAt(index);
        }

        @Override
        public CharSequence subSequence(int beginIndex, int endIndex) {
            int newLength = CharSequences.regionLength(this.length, beginIndex, endIndex);
            if (newLength == 0) {
                return "";
            }
            if (newLength == this.length) {
                return this;
            }
            beginIndex += this.begin;
            if ((endIndex += this.begin) <= this.beginSkip) {
                return new SubSequence(this.input, beginIndex, endIndex);
            }
            endIndex += this.skipLength;
            if (beginIndex >= this.beginSkip) {
                return new SubSequence(this.input, beginIndex + this.skipLength, endIndex);
            }
            return new WithoutSubSequence(this.input, beginIndex, this.beginSkip, this.endSkip, endIndex);
        }

        @Override
        public String toString() {
            return new StringBuilder(this.length).append(this.input, this.begin, this.beginSkip).append(this.input, this.endSkip, this.end).toString();
        }
    }

    static final class SubSequence
    implements CharSequence {
        final CharSequence input;
        final int begin;
        final int end;
        final int length;

        SubSequence(CharSequence input, int begin, int end) {
            this.input = input;
            this.begin = begin;
            this.end = end;
            this.length = end - begin;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public char charAt(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("index < 0");
            }
            if (index >= this.length) {
                throw new IndexOutOfBoundsException("index >= length");
            }
            return this.input.charAt(this.begin + index);
        }

        @Override
        public CharSequence subSequence(int beginIndex, int endIndex) {
            int newLength = CharSequences.regionLength(this.length, beginIndex, endIndex);
            if (newLength == 0) {
                return "";
            }
            if (newLength == this.length) {
                return this;
            }
            return new SubSequence(this.input, beginIndex + this.begin, endIndex + this.begin);
        }

        @Override
        public String toString() {
            return new StringBuilder(this.length).append(this.input, this.begin, this.end).toString();
        }
    }
}

