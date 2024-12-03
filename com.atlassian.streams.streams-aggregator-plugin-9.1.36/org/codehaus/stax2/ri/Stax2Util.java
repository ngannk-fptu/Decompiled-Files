/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamConstants;

public final class Stax2Util
implements XMLStreamConstants {
    private Stax2Util() {
    }

    public static String eventTypeDesc(int n) {
        switch (n) {
            case 1: {
                return "START_ELEMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 12: {
                return "CDATA";
            }
            case 6: {
                return "SPACE";
            }
            case 5: {
                return "COMMENT";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 11: {
                return "DTD";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
        }
        return "[" + n + "]";
    }

    public static String trimSpaces(String string) {
        int n = string.length();
        int n2 = 0;
        while (true) {
            if (n2 >= n) {
                return null;
            }
            if (!Stax2Util._isSpace(string.charAt(n2))) break;
            ++n2;
        }
        if (!Stax2Util._isSpace(string.charAt(--n))) {
            return n2 == 0 ? string : string.substring(n2);
        }
        while (--n > n2 && Stax2Util._isSpace(string.charAt(n))) {
        }
        return string.substring(n2, n + 1);
    }

    private static final boolean _isSpace(char c) {
        return c <= ' ';
    }

    public static final class ByteAggregator {
        private static final byte[] NO_BYTES = new byte[0];
        private static final int INITIAL_BLOCK_SIZE = 500;
        static final int DEFAULT_BLOCK_ARRAY_SIZE = 100;
        private byte[][] mBlocks;
        private int mBlockCount;
        private int mTotalLen;
        private byte[] mSpareBlock;

        public byte[] startAggregation() {
            this.mTotalLen = 0;
            this.mBlockCount = 0;
            byte[] byArray = this.mSpareBlock;
            if (byArray == null) {
                byArray = new byte[500];
            } else {
                this.mSpareBlock = null;
            }
            return byArray;
        }

        public byte[] addFullBlock(byte[] byArray) {
            int n;
            int n2 = byArray.length;
            if (this.mBlocks == null) {
                this.mBlocks = new byte[100][];
            } else {
                n = this.mBlocks.length;
                if (this.mBlockCount >= n) {
                    byte[][] byArray2 = this.mBlocks;
                    this.mBlocks = new byte[n + n][];
                    System.arraycopy(byArray2, 0, this.mBlocks, 0, n);
                }
            }
            this.mBlocks[this.mBlockCount] = byArray;
            ++this.mBlockCount;
            this.mTotalLen += n2;
            n = Math.max(this.mTotalLen >> 1, 1000);
            return new byte[n];
        }

        public byte[] aggregateAll(byte[] byArray, int n) {
            int n2 = this.mTotalLen + n;
            if (n2 == 0) {
                return NO_BYTES;
            }
            byte[] byArray2 = new byte[n2];
            int n3 = 0;
            if (this.mBlocks != null) {
                for (int i = 0; i < this.mBlockCount; ++i) {
                    byte[] byArray3 = this.mBlocks[i];
                    int n4 = byArray3.length;
                    System.arraycopy(byArray3, 0, byArray2, n3, n4);
                    n3 += n4;
                }
            }
            System.arraycopy(byArray, 0, byArray2, n3, n);
            this.mSpareBlock = byArray;
            if ((n3 += n) != n2) {
                throw new RuntimeException("Internal error: total len assumed to be " + n2 + ", copied " + n3 + " bytes");
            }
            return byArray2;
        }
    }

    public static final class TextBuffer {
        private String mText = null;
        private StringBuffer mBuilder = null;

        public void reset() {
            this.mText = null;
            this.mBuilder = null;
        }

        public void append(String string) {
            int n = string.length();
            if (n > 0) {
                if (this.mText != null) {
                    this.mBuilder = new StringBuffer(this.mText.length() + n);
                    this.mBuilder.append(this.mText);
                    this.mText = null;
                }
                if (this.mBuilder != null) {
                    this.mBuilder.append(string);
                } else {
                    this.mText = string;
                }
            }
        }

        public String get() {
            if (this.mText != null) {
                return this.mText;
            }
            if (this.mBuilder != null) {
                return this.mBuilder.toString();
            }
            return "";
        }

        public boolean isEmpty() {
            return this.mText == null && this.mBuilder == null;
        }
    }
}

