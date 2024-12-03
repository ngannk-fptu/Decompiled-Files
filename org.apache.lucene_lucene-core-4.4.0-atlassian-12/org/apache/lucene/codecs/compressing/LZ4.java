/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.PackedInts;

final class LZ4 {
    static final int MEMORY_USAGE = 14;
    static final int MIN_MATCH = 4;
    static final int MAX_DISTANCE = 65536;
    static final int LAST_LITERALS = 5;
    static final int HASH_LOG_HC = 15;
    static final int HASH_TABLE_SIZE_HC = 32768;
    static final int OPTIMAL_ML = 18;

    private LZ4() {
    }

    private static int hash(int i, int hashBits) {
        return i * -1640531535 >>> 32 - hashBits;
    }

    private static int hashHC(int i) {
        return LZ4.hash(i, 15);
    }

    private static int readInt(byte[] buf, int i) {
        return (buf[i] & 0xFF) << 24 | (buf[i + 1] & 0xFF) << 16 | (buf[i + 2] & 0xFF) << 8 | buf[i + 3] & 0xFF;
    }

    private static boolean readIntEquals(byte[] buf, int i, int j) {
        return LZ4.readInt(buf, i) == LZ4.readInt(buf, j);
    }

    private static int commonBytes(byte[] b, int o1, int o2, int limit) {
        assert (o1 < o2);
        int count = 0;
        while (o2 < limit && b[o1++] == b[o2++]) {
            ++count;
        }
        return count;
    }

    private static int commonBytesBackward(byte[] b, int o1, int o2, int l1, int l2) {
        int count = 0;
        while (o1 > l1 && o2 > l2 && b[--o1] == b[--o2]) {
            ++count;
        }
        return count;
    }

    public static int decompress(DataInput compressed, int decompressedLen, byte[] dest, int dOff) throws IOException {
        int destEnd = dest.length;
        do {
            int token;
            int literalLen;
            if ((literalLen = (token = compressed.readByte() & 0xFF) >>> 4) != 0) {
                if (literalLen == 15) {
                    byte len;
                    while ((len = compressed.readByte()) == -1) {
                        literalLen += 255;
                    }
                    literalLen += len & 0xFF;
                }
                compressed.readBytes(dest, dOff, literalLen);
                dOff += literalLen;
            }
            if (dOff >= decompressedLen) break;
            int matchDec = compressed.readByte() & 0xFF | (compressed.readByte() & 0xFF) << 8;
            assert (matchDec > 0);
            int matchLen = token & 0xF;
            if (matchLen == 15) {
                byte len;
                while ((len = compressed.readByte()) == -1) {
                    matchLen += 255;
                }
                matchLen += len & 0xFF;
            }
            int fastLen = (matchLen += 4) + 7 & 0xFFFFFFF8;
            if (matchDec < matchLen || dOff + fastLen > destEnd) {
                int ref = dOff - matchDec;
                int end = dOff + matchLen;
                while (dOff < end) {
                    dest[dOff] = dest[ref];
                    ++ref;
                    ++dOff;
                }
            } else {
                System.arraycopy(dest, dOff - matchDec, dest, dOff, fastLen);
                dOff += matchLen;
            }
        } while (dOff < decompressedLen);
        return dOff;
    }

    private static void encodeLen(int l, DataOutput out) throws IOException {
        while (l >= 255) {
            out.writeByte((byte)-1);
            l -= 255;
        }
        out.writeByte((byte)l);
    }

    private static void encodeLiterals(byte[] bytes, int token, int anchor, int literalLen, DataOutput out) throws IOException {
        out.writeByte((byte)token);
        if (literalLen >= 15) {
            LZ4.encodeLen(literalLen - 15, out);
        }
        out.writeBytes(bytes, anchor, literalLen);
    }

    private static void encodeLastLiterals(byte[] bytes, int anchor, int literalLen, DataOutput out) throws IOException {
        int token = Math.min(literalLen, 15) << 4;
        LZ4.encodeLiterals(bytes, token, anchor, literalLen, out);
    }

    private static void encodeSequence(byte[] bytes, int anchor, int matchRef, int matchOff, int matchLen, DataOutput out) throws IOException {
        int literalLen = matchOff - anchor;
        assert (matchLen >= 4);
        int token = Math.min(literalLen, 15) << 4 | Math.min(matchLen - 4, 15);
        LZ4.encodeLiterals(bytes, token, anchor, literalLen, out);
        int matchDec = matchOff - matchRef;
        assert (matchDec > 0 && matchDec < 65536);
        out.writeByte((byte)matchDec);
        out.writeByte((byte)(matchDec >>> 8));
        if (matchLen >= 19) {
            LZ4.encodeLen(matchLen - 15 - 4, out);
        }
    }

    public static void compress(byte[] bytes, int off, int len, DataOutput out, HashTable ht) throws IOException {
        int base = off;
        int end = off + len;
        int anchor = off++;
        if (len > 9) {
            int limit = end - 5;
            int matchLimit = limit - 4;
            ht.reset(len);
            int hashLog = ht.hashLog;
            PackedInts.Mutable hashTable = ht.hashTable;
            block0: while (off < limit) {
                while (off < matchLimit) {
                    int v = LZ4.readInt(bytes, off);
                    int h = LZ4.hash(v, hashLog);
                    int ref = base + (int)hashTable.get(h);
                    assert (PackedInts.bitsRequired(off - base) <= hashTable.getBitsPerValue());
                    hashTable.set(h, off - base);
                    if (off - ref >= 65536 || LZ4.readInt(bytes, ref) != v) {
                        ++off;
                        continue;
                    }
                    int matchLen = 4 + LZ4.commonBytes(bytes, ref + 4, off + 4, limit);
                    LZ4.encodeSequence(bytes, anchor, ref, off, matchLen, out);
                    anchor = off += matchLen;
                    continue block0;
                }
                break block0;
            }
        }
        int literalLen = end - anchor;
        assert (literalLen >= 5 || literalLen == len);
        LZ4.encodeLastLiterals(bytes, anchor, end - anchor, out);
    }

    private static void copyTo(Match m1, Match m2) {
        m2.len = m1.len;
        m2.start = m1.start;
        m2.ref = m1.ref;
    }

    public static void compressHC(byte[] src, int srcOff, int srcLen, DataOutput out, HCHashTable ht) throws IOException {
        int srcEnd = srcOff + srcLen;
        int matchLimit = srcEnd - 5;
        int sOff = srcOff;
        int anchor = sOff++;
        ht.reset(srcOff);
        Match match0 = new Match();
        Match match1 = new Match();
        Match match2 = new Match();
        Match match3 = new Match();
        block0: while (sOff < matchLimit) {
            if (!ht.insertAndFindBestMatch(src, sOff, matchLimit, match1)) {
                ++sOff;
                continue;
            }
            LZ4.copyTo(match1, match0);
            block1: while (true) {
                assert (match1.start >= anchor);
                if (match1.end() >= matchLimit || !ht.insertAndFindWiderMatch(src, match1.end() - 2, match1.start + 1, matchLimit, match1.len, match2)) {
                    LZ4.encodeSequence(src, anchor, match1.ref, match1.start, match1.len, out);
                    anchor = sOff = match1.end();
                    continue block0;
                }
                if (match0.start < match1.start && match2.start < match1.start + match0.len) {
                    LZ4.copyTo(match0, match1);
                }
                assert (match2.start > match1.start);
                if (match2.start - match1.start < 3) {
                    LZ4.copyTo(match2, match1);
                    continue;
                }
                while (true) {
                    int correction;
                    if (match2.start - match1.start < 18) {
                        int correction2;
                        int newMatchLen = match1.len;
                        if (newMatchLen > 18) {
                            newMatchLen = 18;
                        }
                        if (match1.start + newMatchLen > match2.end() - 4) {
                            newMatchLen = match2.start - match1.start + match2.len - 4;
                        }
                        if ((correction2 = newMatchLen - (match2.start - match1.start)) > 0) {
                            match2.fix(correction2);
                        }
                    }
                    if (match2.start + match2.len >= matchLimit || !ht.insertAndFindWiderMatch(src, match2.end() - 3, match2.start, matchLimit, match2.len, match3)) {
                        if (match2.start < match1.end()) {
                            if (match2.start - match1.start < 18) {
                                if (match1.len > 18) {
                                    match1.len = 18;
                                }
                                if (match1.end() > match2.end() - 4) {
                                    match1.len = match2.end() - match1.start - 4;
                                }
                                if ((correction = match1.len - (match2.start - match1.start)) > 0) {
                                    match2.fix(correction);
                                }
                            } else {
                                match1.len = match2.start - match1.start;
                            }
                        }
                        LZ4.encodeSequence(src, anchor, match1.ref, match1.start, match1.len, out);
                        anchor = sOff = match1.end();
                        LZ4.encodeSequence(src, anchor, match2.ref, match2.start, match2.len, out);
                        anchor = sOff = match2.end();
                        continue block0;
                    }
                    if (match3.start < match1.end() + 3) {
                        if (match3.start >= match1.end()) {
                            if (match2.start < match1.end()) {
                                correction = match1.end() - match2.start;
                                match2.fix(correction);
                                if (match2.len < 4) {
                                    LZ4.copyTo(match3, match2);
                                }
                            }
                            LZ4.encodeSequence(src, anchor, match1.ref, match1.start, match1.len, out);
                            anchor = sOff = match1.end();
                            LZ4.copyTo(match3, match1);
                            LZ4.copyTo(match2, match0);
                            continue block1;
                        }
                        LZ4.copyTo(match3, match2);
                        continue;
                    }
                    if (match2.start < match1.end()) {
                        if (match2.start - match1.start < 15) {
                            if (match1.len > 18) {
                                match1.len = 18;
                            }
                            if (match1.end() > match2.end() - 4) {
                                match1.len = match2.end() - match1.start - 4;
                            }
                            correction = match1.end() - match2.start;
                            match2.fix(correction);
                        } else {
                            match1.len = match2.start - match1.start;
                        }
                    }
                    LZ4.encodeSequence(src, anchor, match1.ref, match1.start, match1.len, out);
                    anchor = sOff = match1.end();
                    LZ4.copyTo(match2, match1);
                    LZ4.copyTo(match3, match2);
                }
                break;
            }
        }
        LZ4.encodeLastLiterals(src, anchor, srcEnd - anchor, out);
    }

    static final class HCHashTable {
        static final int MAX_ATTEMPTS = 256;
        static final int MASK = 65535;
        int nextToUpdate;
        private int base;
        private final int[] hashTable = new int[32768];
        private final short[] chainTable = new short[65536];

        HCHashTable() {
        }

        private void reset(int base) {
            this.base = base;
            this.nextToUpdate = base;
            Arrays.fill(this.hashTable, -1);
            Arrays.fill(this.chainTable, (short)0);
        }

        private int hashPointer(byte[] bytes, int off) {
            int v = LZ4.readInt(bytes, off);
            int h = LZ4.hashHC(v);
            return this.base + this.hashTable[h];
        }

        private int next(int off) {
            return this.base + off - (this.chainTable[off & 0xFFFF] & 0xFFFF);
        }

        private void addHash(byte[] bytes, int off) {
            int v = LZ4.readInt(bytes, off);
            int h = LZ4.hashHC(v);
            int delta = off - this.hashTable[h];
            if (delta >= 65536) {
                delta = 65535;
            }
            this.chainTable[off & 0xFFFF] = (short)delta;
            this.hashTable[h] = off - this.base;
        }

        void insert(int off, byte[] bytes) {
            while (this.nextToUpdate < off) {
                this.addHash(bytes, this.nextToUpdate);
                ++this.nextToUpdate;
            }
        }

        boolean insertAndFindBestMatch(byte[] buf, int off, int matchLimit, Match match) {
            match.start = off;
            match.len = 0;
            this.insert(off, buf);
            int ref = this.hashPointer(buf, off);
            for (int i = 0; i < 256 && ref >= Math.max(this.base, off - 65536 + 1); ++i) {
                int matchLen;
                if (buf[ref + match.len] == buf[off + match.len] && LZ4.readIntEquals(buf, ref, off) && (matchLen = 4 + LZ4.commonBytes(buf, ref + 4, off + 4, matchLimit)) > match.len) {
                    match.ref = ref;
                    match.len = matchLen;
                }
                ref = this.next(ref);
            }
            return match.len != 0;
        }

        boolean insertAndFindWiderMatch(byte[] buf, int off, int startLimit, int matchLimit, int minLen, Match match) {
            match.len = minLen;
            this.insert(off, buf);
            int delta = off - startLimit;
            int ref = this.hashPointer(buf, off);
            for (int i = 0; i < 256 && ref >= Math.max(this.base, off - 65536 + 1); ++i) {
                if (buf[ref - delta + match.len] == buf[startLimit + match.len] && LZ4.readIntEquals(buf, ref, off)) {
                    int matchLenForward = 4 + LZ4.commonBytes(buf, ref + 4, off + 4, matchLimit);
                    int matchLenBackward = LZ4.commonBytesBackward(buf, ref, off, this.base, startLimit);
                    int matchLen = matchLenBackward + matchLenForward;
                    if (matchLen > match.len) {
                        match.len = matchLen;
                        match.ref = ref - matchLenBackward;
                        match.start = off - matchLenBackward;
                    }
                }
                ref = this.next(ref);
            }
            return match.len > minLen;
        }
    }

    private static class Match {
        int start;
        int ref;
        int len;

        private Match() {
        }

        void fix(int correction) {
            this.start += correction;
            this.ref += correction;
            this.len -= correction;
        }

        int end() {
            return this.start + this.len;
        }
    }

    static final class HashTable {
        private int hashLog;
        private PackedInts.Mutable hashTable;

        HashTable() {
        }

        void reset(int len) {
            int bitsPerOffset = PackedInts.bitsRequired(len - 5);
            int bitsPerOffsetLog = 32 - Integer.numberOfLeadingZeros(bitsPerOffset - 1);
            this.hashLog = 17 - bitsPerOffsetLog;
            if (this.hashTable == null || this.hashTable.size() < 1 << this.hashLog || this.hashTable.getBitsPerValue() < bitsPerOffset) {
                this.hashTable = PackedInts.getMutable(1 << this.hashLog, bitsPerOffset, 0.2f);
            } else {
                this.hashTable.clear();
            }
        }
    }
}

