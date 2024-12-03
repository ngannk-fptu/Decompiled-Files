/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUBinary;
import com.ibm.icu.impl.Trie2;
import com.ibm.icu.impl.Trie2_16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ICUUncheckedIOException;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class UBiDiProps {
    private int[] indexes;
    private int[] mirrors;
    private byte[] jgArray;
    private byte[] jgArray2;
    private Trie2_16 trie;
    private static final String DATA_NAME = "ubidi";
    private static final String DATA_TYPE = "icu";
    private static final String DATA_FILE_NAME = "ubidi.icu";
    private static final int FMT = 1114195049;
    private static final int IX_TRIE_SIZE = 2;
    private static final int IX_MIRROR_LENGTH = 3;
    private static final int IX_JG_START = 4;
    private static final int IX_JG_LIMIT = 5;
    private static final int IX_JG_START2 = 6;
    private static final int IX_JG_LIMIT2 = 7;
    private static final int IX_MAX_VALUES = 15;
    private static final int IX_TOP = 16;
    private static final int JT_SHIFT = 5;
    private static final int BPT_SHIFT = 8;
    private static final int JOIN_CONTROL_SHIFT = 10;
    private static final int BIDI_CONTROL_SHIFT = 11;
    private static final int IS_MIRRORED_SHIFT = 12;
    private static final int MIRROR_DELTA_SHIFT = 13;
    private static final int MAX_JG_SHIFT = 16;
    private static final int CLASS_MASK = 31;
    private static final int JT_MASK = 224;
    private static final int BPT_MASK = 768;
    private static final int MAX_JG_MASK = 0xFF0000;
    private static final int ESC_MIRROR_DELTA = -4;
    private static final int MIRROR_INDEX_SHIFT = 21;
    public static final UBiDiProps INSTANCE;

    private UBiDiProps() throws IOException {
        ByteBuffer bytes = ICUBinary.getData(DATA_FILE_NAME);
        this.readData(bytes);
    }

    private void readData(ByteBuffer bytes) throws IOException {
        ICUBinary.readHeader(bytes, 1114195049, new IsAcceptable());
        int count = bytes.getInt();
        if (count < 16) {
            throw new IOException("indexes[0] too small in ubidi.icu");
        }
        this.indexes = new int[count];
        this.indexes[0] = count;
        for (int i = 1; i < count; ++i) {
            this.indexes[i] = bytes.getInt();
        }
        this.trie = Trie2_16.createFromSerialized(bytes);
        int expectedTrieLength = this.indexes[2];
        int trieLength = this.trie.getSerializedLength();
        if (trieLength > expectedTrieLength) {
            throw new IOException("ubidi.icu: not enough bytes for the trie");
        }
        ICUBinary.skipBytes(bytes, expectedTrieLength - trieLength);
        count = this.indexes[3];
        if (count > 0) {
            this.mirrors = ICUBinary.getInts(bytes, count, 0);
        }
        count = this.indexes[5] - this.indexes[4];
        this.jgArray = new byte[count];
        bytes.get(this.jgArray);
        count = this.indexes[7] - this.indexes[6];
        this.jgArray2 = new byte[count];
        bytes.get(this.jgArray2);
    }

    public final void addPropertyStarts(UnicodeSet set) {
        int i;
        for (Trie2.Range range : this.trie) {
            if (range.leadSurrogate) break;
            set.add(range.startCodePoint);
        }
        int length = this.indexes[3];
        for (i = 0; i < length; ++i) {
            int c = UBiDiProps.getMirrorCodePoint(this.mirrors[i]);
            set.add(c, c + 1);
        }
        int start = this.indexes[4];
        int limit = this.indexes[5];
        byte[] jga = this.jgArray;
        while (true) {
            length = limit - start;
            byte prev = 0;
            for (i = 0; i < length; ++i) {
                byte jg = jga[i];
                if (jg != prev) {
                    set.add(start);
                    prev = jg;
                }
                ++start;
            }
            if (prev != 0) {
                set.add(limit);
            }
            if (limit != this.indexes[5]) break;
            start = this.indexes[6];
            limit = this.indexes[7];
            jga = this.jgArray2;
        }
    }

    public final int getMaxValue(int which) {
        int max = this.indexes[15];
        switch (which) {
            case 4096: {
                return max & 0x1F;
            }
            case 4102: {
                return (max & 0xFF0000) >> 16;
            }
            case 4103: {
                return (max & 0xE0) >> 5;
            }
            case 4117: {
                return (max & 0x300) >> 8;
            }
        }
        return -1;
    }

    public final int getClass(int c) {
        return UBiDiProps.getClassFromProps(this.trie.get(c));
    }

    public final boolean isMirrored(int c) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c), 12);
    }

    private final int getMirror(int c, int props) {
        int delta = UBiDiProps.getMirrorDeltaFromProps(props);
        if (delta != -4) {
            return c + delta;
        }
        int length = this.indexes[3];
        for (int i = 0; i < length; ++i) {
            int m = this.mirrors[i];
            int c2 = UBiDiProps.getMirrorCodePoint(m);
            if (c == c2) {
                return UBiDiProps.getMirrorCodePoint(this.mirrors[UBiDiProps.getMirrorIndex(m)]);
            }
            if (c < c2) break;
        }
        return c;
    }

    public final int getMirror(int c) {
        int props = this.trie.get(c);
        return this.getMirror(c, props);
    }

    public final boolean isBidiControl(int c) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c), 11);
    }

    public final boolean isJoinControl(int c) {
        return UBiDiProps.getFlagFromProps(this.trie.get(c), 10);
    }

    public final int getJoiningType(int c) {
        return (this.trie.get(c) & 0xE0) >> 5;
    }

    public final int getJoiningGroup(int c) {
        int start = this.indexes[4];
        int limit = this.indexes[5];
        if (start <= c && c < limit) {
            return this.jgArray[c - start] & 0xFF;
        }
        start = this.indexes[6];
        limit = this.indexes[7];
        if (start <= c && c < limit) {
            return this.jgArray2[c - start] & 0xFF;
        }
        return 0;
    }

    public final int getPairedBracketType(int c) {
        return (this.trie.get(c) & 0x300) >> 8;
    }

    public final int getPairedBracket(int c) {
        int props = this.trie.get(c);
        if ((props & 0x300) == 0) {
            return c;
        }
        return this.getMirror(c, props);
    }

    private static final int getClassFromProps(int props) {
        return props & 0x1F;
    }

    private static final boolean getFlagFromProps(int props, int shift) {
        return (props >> shift & 1) != 0;
    }

    private static final int getMirrorDeltaFromProps(int props) {
        return (short)props >> 13;
    }

    private static final int getMirrorCodePoint(int m) {
        return m & 0x1FFFFF;
    }

    private static final int getMirrorIndex(int m) {
        return m >>> 21;
    }

    static {
        try {
            INSTANCE = new UBiDiProps();
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException(e);
        }
    }

    private static final class IsAcceptable
    implements ICUBinary.Authenticate {
        private IsAcceptable() {
        }

        @Override
        public boolean isDataVersionAcceptable(byte[] version) {
            return version[0] == 2;
        }
    }
}

