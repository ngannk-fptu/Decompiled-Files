/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class UTF8 {
    private static final byte C_ILL = 0;
    private static final byte C_CR1 = 1;
    private static final byte C_CR2 = 2;
    private static final byte C_CR3 = 3;
    private static final byte C_L2A = 4;
    private static final byte C_L3A = 5;
    private static final byte C_L3B = 6;
    private static final byte C_L3C = 7;
    private static final byte C_L4A = 8;
    private static final byte C_L4B = 9;
    private static final byte C_L4C = 10;
    private static final byte S_ERR = -2;
    private static final byte S_END = -1;
    private static final byte S_CS1 = 0;
    private static final byte S_CS2 = 16;
    private static final byte S_CS3 = 32;
    private static final byte S_P3A = 48;
    private static final byte S_P3B = 64;
    private static final byte S_P4A = 80;
    private static final byte S_P4B = 96;
    private static final short[] firstUnitTable = new short[128];
    private static final byte[] transitionTable = new byte[112];

    private static void fill(byte[] table, int first, int last, byte b) {
        for (int i = first; i <= last; ++i) {
            table[i] = b;
        }
    }

    public static int transcodeToUTF16(byte[] utf8, char[] utf16) {
        return UTF8.transcodeToUTF16(utf8, 0, utf8.length, utf16);
    }

    public static int transcodeToUTF16(byte[] utf8, int utf8Off, int utf8Length, char[] utf16) {
        int i = utf8Off;
        int j = 0;
        int maxI = utf8Off + utf8Length;
        while (i < maxI) {
            byte codeUnit;
            if ((codeUnit = utf8[i++]) >= 0) {
                if (j >= utf16.length) {
                    return -1;
                }
                utf16[j++] = (char)codeUnit;
                continue;
            }
            short first = firstUnitTable[codeUnit & 0x7F];
            int codePoint = first >>> 8;
            byte state = (byte)first;
            while (state >= 0) {
                if (i >= maxI) {
                    return -1;
                }
                codeUnit = utf8[i++];
                codePoint = codePoint << 6 | codeUnit & 0x3F;
                state = transitionTable[state + ((codeUnit & 0xFF) >>> 4)];
            }
            if (state == -2) {
                return -1;
            }
            if (codePoint <= 65535) {
                if (j >= utf16.length) {
                    return -1;
                }
                utf16[j++] = (char)codePoint;
                continue;
            }
            if (j >= utf16.length - 1) {
                return -1;
            }
            utf16[j++] = (char)(55232 + (codePoint >>> 10));
            utf16[j++] = (char)(0xDC00 | codePoint & 0x3FF);
        }
        return j;
    }

    static {
        byte[] categories = new byte[128];
        UTF8.fill(categories, 0, 15, (byte)1);
        UTF8.fill(categories, 16, 31, (byte)2);
        UTF8.fill(categories, 32, 63, (byte)3);
        UTF8.fill(categories, 64, 65, (byte)0);
        UTF8.fill(categories, 66, 95, (byte)4);
        UTF8.fill(categories, 96, 96, (byte)5);
        UTF8.fill(categories, 97, 108, (byte)6);
        UTF8.fill(categories, 109, 109, (byte)7);
        UTF8.fill(categories, 110, 111, (byte)6);
        UTF8.fill(categories, 112, 112, (byte)8);
        UTF8.fill(categories, 113, 115, (byte)9);
        UTF8.fill(categories, 116, 116, (byte)10);
        UTF8.fill(categories, 117, 127, (byte)0);
        UTF8.fill(transitionTable, 0, transitionTable.length - 1, (byte)-2);
        UTF8.fill(transitionTable, 8, 11, (byte)-1);
        UTF8.fill(transitionTable, 24, 27, (byte)0);
        UTF8.fill(transitionTable, 40, 43, (byte)16);
        UTF8.fill(transitionTable, 58, 59, (byte)0);
        UTF8.fill(transitionTable, 72, 73, (byte)0);
        UTF8.fill(transitionTable, 89, 91, (byte)16);
        UTF8.fill(transitionTable, 104, 104, (byte)16);
        byte[] firstUnitMasks = new byte[]{0, 0, 0, 0, 31, 15, 15, 15, 7, 7, 7};
        byte[] firstUnitTransitions = new byte[]{-2, -2, -2, -2, 0, 48, 16, 64, 80, 32, 96};
        for (int i = 0; i < 128; ++i) {
            byte category = categories[i];
            int codePoint = i & firstUnitMasks[category];
            byte state = firstUnitTransitions[category];
            UTF8.firstUnitTable[i] = (short)(codePoint << 8 | state);
        }
    }
}

