/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import org.apache.xml.serializer.Encodings;

public final class EncodingInfo {
    private final char m_highCharInContiguousGroup;
    final String name;
    final String javaName;
    private InEncoding m_encoding;

    public boolean isInEncoding(char ch) {
        if (this.m_encoding == null) {
            this.m_encoding = new EncodingImpl();
        }
        return this.m_encoding.isInEncoding(ch);
    }

    public boolean isInEncoding(char high, char low) {
        if (this.m_encoding == null) {
            this.m_encoding = new EncodingImpl();
        }
        return this.m_encoding.isInEncoding(high, low);
    }

    public EncodingInfo(String name, String javaName, char highChar) {
        this.name = name;
        this.javaName = javaName;
        this.m_highCharInContiguousGroup = highChar;
    }

    private static boolean inEncoding(char ch, String encoding) {
        boolean isInEncoding;
        block2: {
            try {
                char[] cArray = new char[]{ch};
                String s = new String(cArray);
                byte[] bArray = s.getBytes(encoding);
                isInEncoding = EncodingInfo.inEncoding(ch, bArray);
            }
            catch (Exception e) {
                isInEncoding = false;
                if (encoding != null) break block2;
                isInEncoding = true;
            }
        }
        return isInEncoding;
    }

    private static boolean inEncoding(char high, char low, String encoding) {
        boolean isInEncoding;
        try {
            char[] cArray = new char[]{high, low};
            String s = new String(cArray);
            byte[] bArray = s.getBytes(encoding);
            isInEncoding = EncodingInfo.inEncoding(high, bArray);
        }
        catch (Exception e) {
            isInEncoding = false;
        }
        return isInEncoding;
    }

    private static boolean inEncoding(char ch, byte[] data) {
        boolean isInEncoding = data == null || data.length == 0 ? false : (data[0] == 0 ? false : data[0] != 63 || ch == '?');
        return isInEncoding;
    }

    public final char getHighChar() {
        return this.m_highCharInContiguousGroup;
    }

    private class EncodingImpl
    implements InEncoding {
        private final String m_encoding;
        private final int m_first;
        private final int m_explFirst;
        private final int m_explLast;
        private final int m_last;
        private InEncoding m_before;
        private InEncoding m_after;
        private static final int RANGE = 128;
        private final boolean[] m_alreadyKnown = new boolean[128];
        private final boolean[] m_isInEncoding = new boolean[128];

        @Override
        public boolean isInEncoding(char ch1) {
            boolean ret;
            int codePoint = Encodings.toCodePoint(ch1);
            if (codePoint < this.m_explFirst) {
                if (this.m_before == null) {
                    this.m_before = new EncodingImpl(this.m_encoding, this.m_first, this.m_explFirst - 1, codePoint);
                }
                ret = this.m_before.isInEncoding(ch1);
            } else if (this.m_explLast < codePoint) {
                if (this.m_after == null) {
                    this.m_after = new EncodingImpl(this.m_encoding, this.m_explLast + 1, this.m_last, codePoint);
                }
                ret = this.m_after.isInEncoding(ch1);
            } else {
                int idx = codePoint - this.m_explFirst;
                if (this.m_alreadyKnown[idx]) {
                    ret = this.m_isInEncoding[idx];
                } else {
                    ret = EncodingInfo.inEncoding(ch1, this.m_encoding);
                    this.m_alreadyKnown[idx] = true;
                    this.m_isInEncoding[idx] = ret;
                }
            }
            return ret;
        }

        @Override
        public boolean isInEncoding(char high, char low) {
            boolean ret;
            int codePoint = Encodings.toCodePoint(high, low);
            if (codePoint < this.m_explFirst) {
                if (this.m_before == null) {
                    this.m_before = new EncodingImpl(this.m_encoding, this.m_first, this.m_explFirst - 1, codePoint);
                }
                ret = this.m_before.isInEncoding(high, low);
            } else if (this.m_explLast < codePoint) {
                if (this.m_after == null) {
                    this.m_after = new EncodingImpl(this.m_encoding, this.m_explLast + 1, this.m_last, codePoint);
                }
                ret = this.m_after.isInEncoding(high, low);
            } else {
                int idx = codePoint - this.m_explFirst;
                if (this.m_alreadyKnown[idx]) {
                    ret = this.m_isInEncoding[idx];
                } else {
                    ret = EncodingInfo.inEncoding(high, low, this.m_encoding);
                    this.m_alreadyKnown[idx] = true;
                    this.m_isInEncoding[idx] = ret;
                }
            }
            return ret;
        }

        private EncodingImpl() {
            this(encodingInfo.javaName, 0, Integer.MAX_VALUE, 0);
        }

        private EncodingImpl(String encoding, int first, int last, int codePoint) {
            this.m_first = first;
            this.m_last = last;
            this.m_explFirst = codePoint;
            this.m_explLast = codePoint + 127;
            this.m_encoding = encoding;
            if (EncodingInfo.this.javaName != null) {
                if (0 <= this.m_explFirst && this.m_explFirst <= 127 && ("UTF8".equals(EncodingInfo.this.javaName) || "UTF-16".equals(EncodingInfo.this.javaName) || "ASCII".equals(EncodingInfo.this.javaName) || "US-ASCII".equals(EncodingInfo.this.javaName) || "Unicode".equals(EncodingInfo.this.javaName) || "UNICODE".equals(EncodingInfo.this.javaName) || EncodingInfo.this.javaName.startsWith("ISO8859"))) {
                    for (int unicode = 1; unicode < 127; ++unicode) {
                        int idx = unicode - this.m_explFirst;
                        if (0 > idx || idx >= 128) continue;
                        this.m_alreadyKnown[idx] = true;
                        this.m_isInEncoding[idx] = true;
                    }
                }
                if (EncodingInfo.this.javaName == null) {
                    for (int idx = 0; idx < this.m_alreadyKnown.length; ++idx) {
                        this.m_alreadyKnown[idx] = true;
                        this.m_isInEncoding[idx] = true;
                    }
                }
            }
        }
    }

    private static interface InEncoding {
        public boolean isInEncoding(char var1);

        public boolean isInEncoding(char var1, char var2);
    }
}

