/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.coyote.http2.Hpack;
import org.apache.coyote.http2.HpackException;
import org.apache.tomcat.util.res.StringManager;

public class HPackHuffman {
    protected static final StringManager sm = StringManager.getManager(HPackHuffman.class);
    private static final HuffmanCode[] HUFFMAN_CODES;
    private static final int[] DECODING_TABLE;
    private static final int LOW_TERMINAL_BIT = 32768;
    private static final int HIGH_TERMINAL_BIT = Integer.MIN_VALUE;
    private static final int LOW_MASK = Short.MAX_VALUE;

    public static void decode(ByteBuffer data, int length, StringBuilder target) throws HpackException {
        assert (data.remaining() >= length);
        int treePos = 0;
        boolean eosBits = true;
        int eosBitCount = 0;
        for (int i = 0; i < length; ++i) {
            byte b = data.get();
            for (int bitPos = 7; bitPos >= 0; --bitPos) {
                int val = DECODING_TABLE[treePos];
                if ((1 << bitPos & b) == 0) {
                    if ((val & 0x8000) == 0) {
                        treePos = val & Short.MAX_VALUE;
                        eosBits = false;
                        eosBitCount = 0;
                        continue;
                    }
                    target.append((char)(val & Short.MAX_VALUE));
                    treePos = 0;
                    eosBits = true;
                    continue;
                }
                if (eosBits) {
                    ++eosBitCount;
                }
                if ((val & Integer.MIN_VALUE) == 0) {
                    treePos = val >> 16 & Short.MAX_VALUE;
                    continue;
                }
                if (eosBitCount != 0) {
                    throw new HpackException(sm.getString("hpackhuffman.stringLiteralEOS"));
                }
                target.append((char)(val >> 16 & Short.MAX_VALUE));
                treePos = 0;
                eosBits = true;
            }
        }
        if (eosBitCount > 7) {
            throw new HpackException(sm.getString("hpackhuffman.stringLiteralTooMuchPadding"));
        }
        if (!eosBits) {
            throw new HpackException(sm.getString("hpackhuffman.huffmanEncodedHpackValueDidNotEndWithEOS"));
        }
    }

    public static boolean encode(ByteBuffer buffer, String toEncode, boolean forceLowercase) {
        if (buffer.remaining() <= toEncode.length()) {
            return false;
        }
        int start = buffer.position();
        int length = 0;
        for (int i = 0; i < toEncode.length(); ++i) {
            char c = toEncode.charAt(i);
            if (c > '\u00ff') {
                throw new IllegalArgumentException(sm.getString("hpack.invalidCharacter", new Object[]{Character.toString(c), (int)c}));
            }
            if (forceLowercase) {
                c = Hpack.toLower(c);
            }
            HuffmanCode code = HUFFMAN_CODES[c];
            length += code.length;
        }
        int byteLength = length / 8 + (length % 8 == 0 ? 0 : 1);
        buffer.put((byte)-128);
        Hpack.encodeInteger(buffer, byteLength, 7);
        int bytePos = 0;
        byte currentBufferByte = 0;
        for (int i = 0; i < toEncode.length(); ++i) {
            char c = toEncode.charAt(i);
            if (forceLowercase) {
                c = Hpack.toLower(c);
            }
            HuffmanCode code = HUFFMAN_CODES[c];
            if (code.length + bytePos <= 8) {
                currentBufferByte = (byte)(currentBufferByte | (code.value & 0xFF) << 8 - (code.length + bytePos));
                bytePos += code.length;
            } else {
                int remainingInByte;
                int val = code.value;
                for (int rem = code.length; rem > 0; rem -= remainingInByte) {
                    if (!buffer.hasRemaining()) {
                        buffer.position(start);
                        return false;
                    }
                    remainingInByte = 8 - bytePos;
                    currentBufferByte = rem > remainingInByte ? (byte)(currentBufferByte | val >> rem - remainingInByte) : (byte)(currentBufferByte | val << remainingInByte - rem);
                    if (rem > remainingInByte) {
                        buffer.put(currentBufferByte);
                        currentBufferByte = 0;
                        bytePos = 0;
                        continue;
                    }
                    bytePos = rem;
                }
            }
            if (bytePos == 8) {
                if (!buffer.hasRemaining()) {
                    buffer.position(start);
                    return false;
                }
                buffer.put(currentBufferByte);
                currentBufferByte = 0;
                bytePos = 0;
            }
            if (buffer.position() - start <= toEncode.length()) continue;
            buffer.position(start);
            return false;
        }
        if (bytePos > 0) {
            if (!buffer.hasRemaining()) {
                buffer.position(start);
                return false;
            }
            buffer.put((byte)(currentBufferByte | 255 >> bytePos));
        }
        return true;
    }

    static {
        HuffmanCode[] codes = new HuffmanCode[]{new HuffmanCode(8184, 13), new HuffmanCode(8388568, 23), new HuffmanCode(0xFFFFFE2, 28), new HuffmanCode(0xFFFFFE3, 28), new HuffmanCode(0xFFFFFE4, 28), new HuffmanCode(0xFFFFFE5, 28), new HuffmanCode(0xFFFFFE6, 28), new HuffmanCode(0xFFFFFE7, 28), new HuffmanCode(0xFFFFFE8, 28), new HuffmanCode(0xFFFFEA, 24), new HuffmanCode(0x3FFFFFFC, 30), new HuffmanCode(0xFFFFFE9, 28), new HuffmanCode(0xFFFFFEA, 28), new HuffmanCode(0x3FFFFFFD, 30), new HuffmanCode(0xFFFFFEB, 28), new HuffmanCode(0xFFFFFEC, 28), new HuffmanCode(0xFFFFFED, 28), new HuffmanCode(0xFFFFFEE, 28), new HuffmanCode(0xFFFFFEF, 28), new HuffmanCode(0xFFFFFF0, 28), new HuffmanCode(0xFFFFFF1, 28), new HuffmanCode(0xFFFFFF2, 28), new HuffmanCode(0x3FFFFFFE, 30), new HuffmanCode(0xFFFFFF3, 28), new HuffmanCode(0xFFFFFF4, 28), new HuffmanCode(0xFFFFFF5, 28), new HuffmanCode(0xFFFFFF6, 28), new HuffmanCode(0xFFFFFF7, 28), new HuffmanCode(0xFFFFFF8, 28), new HuffmanCode(0xFFFFFF9, 28), new HuffmanCode(0xFFFFFFA, 28), new HuffmanCode(0xFFFFFFB, 28), new HuffmanCode(20, 6), new HuffmanCode(1016, 10), new HuffmanCode(1017, 10), new HuffmanCode(4090, 12), new HuffmanCode(8185, 13), new HuffmanCode(21, 6), new HuffmanCode(248, 8), new HuffmanCode(2042, 11), new HuffmanCode(1018, 10), new HuffmanCode(1019, 10), new HuffmanCode(249, 8), new HuffmanCode(2043, 11), new HuffmanCode(250, 8), new HuffmanCode(22, 6), new HuffmanCode(23, 6), new HuffmanCode(24, 6), new HuffmanCode(0, 5), new HuffmanCode(1, 5), new HuffmanCode(2, 5), new HuffmanCode(25, 6), new HuffmanCode(26, 6), new HuffmanCode(27, 6), new HuffmanCode(28, 6), new HuffmanCode(29, 6), new HuffmanCode(30, 6), new HuffmanCode(31, 6), new HuffmanCode(92, 7), new HuffmanCode(251, 8), new HuffmanCode(32764, 15), new HuffmanCode(32, 6), new HuffmanCode(4091, 12), new HuffmanCode(1020, 10), new HuffmanCode(8186, 13), new HuffmanCode(33, 6), new HuffmanCode(93, 7), new HuffmanCode(94, 7), new HuffmanCode(95, 7), new HuffmanCode(96, 7), new HuffmanCode(97, 7), new HuffmanCode(98, 7), new HuffmanCode(99, 7), new HuffmanCode(100, 7), new HuffmanCode(101, 7), new HuffmanCode(102, 7), new HuffmanCode(103, 7), new HuffmanCode(104, 7), new HuffmanCode(105, 7), new HuffmanCode(106, 7), new HuffmanCode(107, 7), new HuffmanCode(108, 7), new HuffmanCode(109, 7), new HuffmanCode(110, 7), new HuffmanCode(111, 7), new HuffmanCode(112, 7), new HuffmanCode(113, 7), new HuffmanCode(114, 7), new HuffmanCode(252, 8), new HuffmanCode(115, 7), new HuffmanCode(253, 8), new HuffmanCode(8187, 13), new HuffmanCode(524272, 19), new HuffmanCode(8188, 13), new HuffmanCode(16380, 14), new HuffmanCode(34, 6), new HuffmanCode(32765, 15), new HuffmanCode(3, 5), new HuffmanCode(35, 6), new HuffmanCode(4, 5), new HuffmanCode(36, 6), new HuffmanCode(5, 5), new HuffmanCode(37, 6), new HuffmanCode(38, 6), new HuffmanCode(39, 6), new HuffmanCode(6, 5), new HuffmanCode(116, 7), new HuffmanCode(117, 7), new HuffmanCode(40, 6), new HuffmanCode(41, 6), new HuffmanCode(42, 6), new HuffmanCode(7, 5), new HuffmanCode(43, 6), new HuffmanCode(118, 7), new HuffmanCode(44, 6), new HuffmanCode(8, 5), new HuffmanCode(9, 5), new HuffmanCode(45, 6), new HuffmanCode(119, 7), new HuffmanCode(120, 7), new HuffmanCode(121, 7), new HuffmanCode(122, 7), new HuffmanCode(123, 7), new HuffmanCode(32766, 15), new HuffmanCode(2044, 11), new HuffmanCode(16381, 14), new HuffmanCode(8189, 13), new HuffmanCode(0xFFFFFFC, 28), new HuffmanCode(1048550, 20), new HuffmanCode(4194258, 22), new HuffmanCode(1048551, 20), new HuffmanCode(1048552, 20), new HuffmanCode(0x3FFFD3, 22), new HuffmanCode(4194260, 22), new HuffmanCode(4194261, 22), new HuffmanCode(8388569, 23), new HuffmanCode(4194262, 22), new HuffmanCode(8388570, 23), new HuffmanCode(8388571, 23), new HuffmanCode(8388572, 23), new HuffmanCode(0x7FFFDD, 23), new HuffmanCode(8388574, 23), new HuffmanCode(0xFFFFEB, 24), new HuffmanCode(0x7FFFDF, 23), new HuffmanCode(0xFFFFEC, 24), new HuffmanCode(0xFFFFED, 24), new HuffmanCode(4194263, 22), new HuffmanCode(8388576, 23), new HuffmanCode(0xFFFFEE, 24), new HuffmanCode(8388577, 23), new HuffmanCode(8388578, 23), new HuffmanCode(8388579, 23), new HuffmanCode(8388580, 23), new HuffmanCode(2097116, 21), new HuffmanCode(4194264, 22), new HuffmanCode(8388581, 23), new HuffmanCode(4194265, 22), new HuffmanCode(8388582, 23), new HuffmanCode(0x7FFFE7, 23), new HuffmanCode(0xFFFFEF, 24), new HuffmanCode(4194266, 22), new HuffmanCode(0x1FFFDD, 21), new HuffmanCode(1048553, 20), new HuffmanCode(4194267, 22), new HuffmanCode(4194268, 22), new HuffmanCode(8388584, 23), new HuffmanCode(8388585, 23), new HuffmanCode(2097118, 21), new HuffmanCode(8388586, 23), new HuffmanCode(0x3FFFDD, 22), new HuffmanCode(4194270, 22), new HuffmanCode(0xFFFFF0, 24), new HuffmanCode(0x1FFFDF, 21), new HuffmanCode(0x3FFFDF, 22), new HuffmanCode(8388587, 23), new HuffmanCode(8388588, 23), new HuffmanCode(2097120, 21), new HuffmanCode(0x1FFFE1, 21), new HuffmanCode(4194272, 22), new HuffmanCode(2097122, 21), new HuffmanCode(8388589, 23), new HuffmanCode(4194273, 22), new HuffmanCode(0x7FFFEE, 23), new HuffmanCode(0x7FFFEF, 23), new HuffmanCode(1048554, 20), new HuffmanCode(4194274, 22), new HuffmanCode(0x3FFFE3, 22), new HuffmanCode(4194276, 22), new HuffmanCode(0x7FFFF0, 23), new HuffmanCode(4194277, 22), new HuffmanCode(4194278, 22), new HuffmanCode(0x7FFFF1, 23), new HuffmanCode(67108832, 26), new HuffmanCode(67108833, 26), new HuffmanCode(1048555, 20), new HuffmanCode(524273, 19), new HuffmanCode(4194279, 22), new HuffmanCode(0x7FFFF2, 23), new HuffmanCode(4194280, 22), new HuffmanCode(33554412, 25), new HuffmanCode(67108834, 26), new HuffmanCode(0x3FFFFE3, 26), new HuffmanCode(67108836, 26), new HuffmanCode(134217694, 27), new HuffmanCode(0x7FFFFDF, 27), new HuffmanCode(67108837, 26), new HuffmanCode(0xFFFFF1, 24), new HuffmanCode(33554413, 25), new HuffmanCode(524274, 19), new HuffmanCode(2097123, 21), new HuffmanCode(67108838, 26), new HuffmanCode(134217696, 27), new HuffmanCode(134217697, 27), new HuffmanCode(67108839, 26), new HuffmanCode(134217698, 27), new HuffmanCode(0xFFFFF2, 24), new HuffmanCode(2097124, 21), new HuffmanCode(2097125, 21), new HuffmanCode(67108840, 26), new HuffmanCode(67108841, 26), new HuffmanCode(0xFFFFFFD, 28), new HuffmanCode(134217699, 27), new HuffmanCode(134217700, 27), new HuffmanCode(134217701, 27), new HuffmanCode(1048556, 20), new HuffmanCode(0xFFFFF3, 24), new HuffmanCode(1048557, 20), new HuffmanCode(2097126, 21), new HuffmanCode(4194281, 22), new HuffmanCode(2097127, 21), new HuffmanCode(2097128, 21), new HuffmanCode(0x7FFFF3, 23), new HuffmanCode(4194282, 22), new HuffmanCode(4194283, 22), new HuffmanCode(0x1FFFFEE, 25), new HuffmanCode(0x1FFFFEF, 25), new HuffmanCode(0xFFFFF4, 24), new HuffmanCode(0xFFFFF5, 24), new HuffmanCode(67108842, 26), new HuffmanCode(0x7FFFF4, 23), new HuffmanCode(67108843, 26), new HuffmanCode(134217702, 27), new HuffmanCode(67108844, 26), new HuffmanCode(67108845, 26), new HuffmanCode(0x7FFFFE7, 27), new HuffmanCode(134217704, 27), new HuffmanCode(134217705, 27), new HuffmanCode(134217706, 27), new HuffmanCode(134217707, 27), new HuffmanCode(0xFFFFFFE, 28), new HuffmanCode(134217708, 27), new HuffmanCode(134217709, 27), new HuffmanCode(0x7FFFFEE, 27), new HuffmanCode(0x7FFFFEF, 27), new HuffmanCode(0x7FFFFF0, 27), new HuffmanCode(0x3FFFFEE, 26), new HuffmanCode(0x3FFFFFFF, 30)};
        HUFFMAN_CODES = codes;
        int[] codingTree = new int[256];
        int pos = 0;
        int allocated = 1;
        HuffmanCode[] currentCode = new HuffmanCode[256];
        currentCode[0] = new HuffmanCode(0, 0);
        HashSet<HuffmanCode> allCodes = new HashSet<HuffmanCode>(Arrays.asList(HUFFMAN_CODES));
        while (!allCodes.isEmpty()) {
            int length = currentCode[pos].length;
            int code = currentCode[pos].value;
            int newLength = length + 1;
            HuffmanCode high = new HuffmanCode(code << 1 | 1, newLength);
            HuffmanCode low = new HuffmanCode(code << 1, newLength);
            int newVal = 0;
            boolean highTerminal = allCodes.remove(high);
            if (highTerminal) {
                int i = 0;
                for (i = 0; i < codes.length && !codes[i].equals(high); ++i) {
                }
                newVal = 0x8000 | i;
            } else {
                int highPos = allocated++;
                currentCode[highPos] = high;
                newVal = highPos;
            }
            newVal <<= 16;
            boolean lowTerminal = allCodes.remove(low);
            if (lowTerminal) {
                int i = 0;
                for (i = 0; i < codes.length && !codes[i].equals(low); ++i) {
                }
                newVal |= 0x8000 | i;
            } else {
                int lowPos = allocated++;
                currentCode[lowPos] = low;
                newVal |= lowPos;
            }
            codingTree[pos] = newVal;
            ++pos;
        }
        DECODING_TABLE = codingTree;
    }

    protected static class HuffmanCode {
        int value;
        int length;

        public HuffmanCode(int value, int length) {
            this.value = value;
            this.length = length;
        }

        public int getValue() {
            return this.value;
        }

        public int getLength() {
            return this.length;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            HuffmanCode that = (HuffmanCode)o;
            if (this.length != that.length) {
                return false;
            }
            return this.value == that.value;
        }

        public int hashCode() {
            int result = this.value;
            result = 31 * result + this.length;
            return result;
        }

        public String toString() {
            return "HuffmanCode{value=" + this.value + ", length=" + this.length + '}';
        }
    }
}

