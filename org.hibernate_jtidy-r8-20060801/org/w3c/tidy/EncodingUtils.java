/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.tidy.TidyUtils;
import org.w3c.tidy.ValidUTF8Sequence;

public final class EncodingUtils {
    public static final int UNICODE_BOM_BE = 65279;
    public static final int UNICODE_BOM = 65279;
    public static final int UNICODE_BOM_LE = 65534;
    public static final int UNICODE_BOM_UTF8 = 0xEFBBBF;
    public static final int FSM_ASCII = 0;
    public static final int FSM_ESC = 1;
    public static final int FSM_ESCD = 2;
    public static final int FSM_ESCDP = 3;
    public static final int FSM_ESCP = 4;
    public static final int FSM_NONASCII = 5;
    public static final int MAX_UTF8_FROM_UCS4 = 0x10FFFF;
    public static final int MAX_UTF16_FROM_UCS4 = 0x10FFFF;
    public static final int LOW_UTF16_SURROGATE = 55296;
    public static final int UTF16_SURROGATES_BEGIN = 65536;
    public static final int UTF16_LOW_SURROGATE_BEGIN = 55296;
    public static final int UTF16_LOW_SURROGATE_END = 56319;
    public static final int UTF16_HIGH_SURROGATE_BEGIN = 56320;
    public static final int UTF16_HIGH_SURROGATE_END = 57343;
    public static final int HIGH_UTF16_SURROGATE = 57343;
    private static final int UTF8_BYTE_SWAP_NOT_A_CHAR = 65534;
    private static final int UTF8_NOT_A_CHAR = 65535;
    private static final int[] WIN2UNICODE = new int[]{8364, 0, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 0, 381, 0, 0, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 0, 382, 376};
    private static final int[] MAC2UNICODE = new int[]{196, 197, 199, 201, 209, 214, 220, 225, 224, 226, 228, 227, 229, 231, 233, 232, 234, 235, 237, 236, 238, 239, 241, 243, 242, 244, 246, 245, 250, 249, 251, 252, 8224, 176, 162, 163, 167, 8226, 182, 223, 174, 169, 8482, 180, 168, 8800, 198, 216, 8734, 177, 8804, 8805, 165, 181, 8706, 8721, 8719, 960, 8747, 170, 186, 937, 230, 248, 191, 161, 172, 8730, 402, 8776, 8710, 171, 187, 8230, 160, 192, 195, 213, 338, 339, 8211, 8212, 8220, 8221, 8216, 8217, 247, 9674, 255, 376, 8260, 8364, 8249, 8250, 64257, 64258, 8225, 183, 8218, 8222, 8240, 194, 202, 193, 203, 200, 205, 206, 207, 204, 211, 212, 63743, 210, 218, 219, 217, 305, 710, 732, 175, 728, 729, 730, 184, 733, 731, 711};
    private static final int[] SYMBOL2UNICODE = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 8704, 35, 8707, 37, 38, 8717, 40, 41, 8727, 43, 44, 8722, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 8773, 913, 914, 935, 916, 917, 934, 915, 919, 921, 977, 922, 923, 924, 925, 927, 928, 920, 929, 931, 932, 933, 962, 937, 926, 936, 918, 91, 8756, 93, 8869, 95, 175, 945, 946, 967, 948, 949, 966, 947, 951, 953, 981, 954, 955, 956, 957, 959, 960, 952, 961, 963, 964, 965, 982, 969, 958, 968, 950, 123, 124, 125, 8764, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 160, 978, 8242, 8804, 8260, 8734, 402, 9827, 9830, 9829, 9824, 8596, 8592, 8593, 8594, 8595, 176, 177, 8243, 8805, 215, 8733, 8706, 183, 247, 8800, 8801, 8776, 8230, 63, 63, 8629, 8501, 8465, 8476, 8472, 8855, 8853, 8709, 8745, 8746, 8835, 8839, 8836, 8834, 8838, 8712, 8713, 8736, 8711, 174, 169, 8482, 8719, 8730, 8901, 172, 8743, 8744, 8660, 8656, 8657, 8658, 8659, 9674, 9001, 174, 169, 8482, 8721, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 8364, 9002, 8747, 8992, 63, 8993, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63};
    private static final ValidUTF8Sequence[] VALID_UTF8 = new ValidUTF8Sequence[]{new ValidUTF8Sequence(0, 127, 1, new char[]{'\u0000', '\u007f', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'}), new ValidUTF8Sequence(128, 2047, 2, new char[]{'\u00c2', '\u00df', '\u0080', '\u00bf', '\u0000', '\u0000', '\u0000', '\u0000'}), new ValidUTF8Sequence(2048, 4095, 3, new char[]{'\u00e0', '\u00e0', '\u00a0', '\u00bf', '\u0080', '\u00bf', '\u0000', '\u0000'}), new ValidUTF8Sequence(4096, 65535, 3, new char[]{'\u00e1', '\u00ef', '\u0080', '\u00bf', '\u0080', '\u00bf', '\u0000', '\u0000'}), new ValidUTF8Sequence(65536, 262143, 4, new char[]{'\u00f0', '\u00f0', '\u0090', '\u00bf', '\u0080', '\u00bf', '\u0080', '\u00bf'}), new ValidUTF8Sequence(262144, 1048575, 4, new char[]{'\u00f1', '\u00f3', '\u0080', '\u00bf', '\u0080', '\u00bf', '\u0080', '\u00bf'}), new ValidUTF8Sequence(0x100000, 0x10FFFF, 4, new char[]{'\u00f4', '\u00f4', '\u0080', '\u008f', '\u0080', '\u00bf', '\u0080', '\u00bf'})};
    private static final int NUM_UTF8_SEQUENCES = VALID_UTF8.length;
    private static final int[] OFFSET_UTF8_SEQUENCES = new int[]{0, 1, 2, 4, NUM_UTF8_SEQUENCES};

    private EncodingUtils() {
    }

    protected static int decodeWin1252(int c) {
        return WIN2UNICODE[c - 128];
    }

    protected static int decodeMacRoman(int c) {
        if (127 < c) {
            c = MAC2UNICODE[c - 128];
        }
        return c;
    }

    static int decodeSymbolFont(int c) {
        if (c > 255) {
            return c;
        }
        return SYMBOL2UNICODE[c];
    }

    static boolean decodeUTF8BytesToChar(int[] c, int firstByte, byte[] successorBytes, GetBytes getter, int[] count, int startInSuccessorBytesArray) {
        int i;
        byte[] buf = new byte[10];
        int ch = 0;
        int n = 0;
        int bytes = 0;
        boolean hasError = false;
        if (successorBytes.length != 0) {
            buf = successorBytes;
        }
        if (firstByte == -1) {
            c[0] = firstByte;
            count[0] = 1;
            return false;
        }
        ch = TidyUtils.toUnsigned(firstByte);
        if (ch <= 127) {
            n = ch;
            bytes = 1;
        } else if ((ch & 0xE0) == 192) {
            n = ch & 0x1F;
            bytes = 2;
        } else if ((ch & 0xF0) == 224) {
            n = ch & 0xF;
            bytes = 3;
        } else if ((ch & 0xF8) == 240) {
            n = ch & 7;
            bytes = 4;
        } else if ((ch & 0xFC) == 248) {
            n = ch & 3;
            bytes = 5;
            hasError = true;
        } else if ((ch & 0xFE) == 252) {
            n = ch & 1;
            bytes = 6;
            hasError = true;
        } else {
            n = ch;
            bytes = 1;
            hasError = true;
        }
        for (i = 1; i < bytes; ++i) {
            int[] buftocopy;
            int[] tempCount = new int[1];
            if (getter != null && bytes - i > 0) {
                tempCount[0] = 1;
                buftocopy = new int[]{buf[startInSuccessorBytesArray + i - 1]};
                getter.doGet(buftocopy, tempCount, false);
                if (tempCount[0] <= 0) {
                    hasError = true;
                    bytes = i;
                    break;
                }
            }
            if ((buf[startInSuccessorBytesArray + i - 1] & 0xC0) != 128) {
                hasError = true;
                bytes = i;
                if (getter == null) break;
                buftocopy = new int[]{buf[startInSuccessorBytesArray + i - 1]};
                tempCount[0] = 1;
                getter.doGet(buftocopy, tempCount, true);
                break;
            }
            n = n << 6 | buf[startInSuccessorBytesArray + i - 1] & 0x3F;
        }
        if (!(hasError || n != 65534 && n != 65535)) {
            hasError = true;
        }
        if (!hasError && n > 0x10FFFF) {
            hasError = true;
        }
        if (!hasError && n >= 55296 && n <= 57343) {
            hasError = true;
        }
        if (!hasError) {
            int lo = OFFSET_UTF8_SEQUENCES[bytes - 1];
            int hi = OFFSET_UTF8_SEQUENCES[bytes] - 1;
            if (n < EncodingUtils.VALID_UTF8[lo].lowChar || n > EncodingUtils.VALID_UTF8[hi].highChar) {
                hasError = true;
            } else {
                hasError = true;
                block1: for (i = lo; i <= hi; ++i) {
                    for (int tempCount = 0; tempCount < bytes; ++tempCount) {
                        char theByte = !TidyUtils.toBoolean(tempCount) ? (char)firstByte : (char)buf[startInSuccessorBytesArray + tempCount - 1];
                        if (theByte >= EncodingUtils.VALID_UTF8[i].validBytes[tempCount * 2] && theByte <= EncodingUtils.VALID_UTF8[i].validBytes[tempCount * 2 + 1]) {
                            hasError = false;
                        }
                        if (hasError) continue block1;
                    }
                }
            }
        }
        count[0] = bytes;
        c[0] = n;
        return hasError;
    }

    static boolean encodeCharToUTF8Bytes(int c, byte[] encodebuf, PutBytes putter, int[] count) {
        int bytes = 0;
        byte[] buf = new byte[10];
        if (encodebuf != null) {
            buf = encodebuf;
        }
        boolean hasError = false;
        if (c <= 127) {
            buf[0] = (byte)c;
            bytes = 1;
        } else if (c <= 2047) {
            buf[0] = (byte)(0xC0 | c >> 6);
            buf[1] = (byte)(0x80 | c & 0x3F);
            bytes = 2;
        } else if (c <= 65535) {
            buf[0] = (byte)(0xE0 | c >> 12);
            buf[1] = (byte)(0x80 | c >> 6 & 0x3F);
            buf[2] = (byte)(0x80 | c & 0x3F);
            bytes = 3;
            if (c == 65534 || c == 65535) {
                hasError = true;
            } else if (c >= 55296 && c <= 57343) {
                hasError = true;
            }
        } else if (c <= 0x1FFFFF) {
            buf[0] = (byte)(0xF0 | c >> 18);
            buf[1] = (byte)(0x80 | c >> 12 & 0x3F);
            buf[2] = (byte)(0x80 | c >> 6 & 0x3F);
            buf[3] = (byte)(0x80 | c & 0x3F);
            bytes = 4;
            if (c > 0x10FFFF) {
                hasError = true;
            }
        } else if (c <= 0x3FFFFFF) {
            buf[0] = (byte)(0xF8 | c >> 24);
            buf[1] = (byte)(0x80 | c >> 18);
            buf[2] = (byte)(0x80 | c >> 12 & 0x3F);
            buf[3] = (byte)(0x80 | c >> 6 & 0x3F);
            buf[4] = (byte)(0x80 | c & 0x3F);
            bytes = 5;
            hasError = true;
        } else if (c <= Integer.MAX_VALUE) {
            buf[0] = (byte)(0xFC | c >> 30);
            buf[1] = (byte)(0x80 | c >> 24 & 0x3F);
            buf[2] = (byte)(0x80 | c >> 18 & 0x3F);
            buf[3] = (byte)(0x80 | c >> 12 & 0x3F);
            buf[4] = (byte)(0x80 | c >> 6 & 0x3F);
            buf[5] = (byte)(0x80 | c & 0x3F);
            bytes = 6;
            hasError = true;
        } else {
            hasError = true;
        }
        if (!hasError && putter != null) {
            int[] tempCount = new int[]{bytes};
            putter.doPut(buf, tempCount);
            if (tempCount[0] < bytes) {
                hasError = true;
            }
        }
        count[0] = bytes;
        return hasError;
    }

    static interface PutBytes {
        public void doPut(byte[] var1, int[] var2);
    }

    static interface GetBytes {
        public void doGet(int[] var1, int[] var2, boolean var3);
    }
}

