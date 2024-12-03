/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.PolyNull
 */
package org.postgresql.util;

import java.sql.SQLException;
import org.checkerframework.checker.nullness.qual.PolyNull;

public class PGbytea {
    private static final int MAX_3_BUFF_SIZE = 0x200000;
    private static final int[] HEX_VALS;

    public static byte @PolyNull [] toBytes(byte @PolyNull [] s) throws SQLException {
        if (s == null) {
            return null;
        }
        if (s.length < 2 || s[0] != 92 || s[1] != 120) {
            return PGbytea.toBytesOctalEscaped(s);
        }
        return PGbytea.toBytesHexEscaped(s);
    }

    private static byte[] toBytesHexEscaped(byte[] s) {
        int realLength = s.length - 2;
        byte[] output = new byte[realLength >>> 1];
        for (int i = 0; i < realLength; i += 2) {
            int val = PGbytea.getHex(s[2 + i]) << 4;
            output[i >>> 1] = (byte)(val |= PGbytea.getHex(s[3 + i]));
        }
        return output;
    }

    private static int getHex(byte b) {
        return HEX_VALS[b - 48];
    }

    private static byte[] toBytesOctalEscaped(byte[] s) {
        int slength = s.length;
        byte[] buf = null;
        int correctSize = slength;
        if (slength > 0x200000) {
            for (int i = 0; i < slength; ++i) {
                byte next;
                byte current = s[i];
                if (current != 92) continue;
                if ((next = s[++i]) == 92) {
                    --correctSize;
                    continue;
                }
                correctSize -= 3;
            }
            buf = new byte[correctSize];
        } else {
            buf = new byte[slength];
        }
        int bufpos = 0;
        for (int i = 0; i < slength; ++i) {
            byte nextbyte = s[i];
            if (nextbyte == 92) {
                int thebyte;
                byte secondbyte;
                if ((secondbyte = s[++i]) == 92) {
                    buf[bufpos++] = 92;
                    continue;
                }
                if ((thebyte = (secondbyte - 48) * 64 + (s[++i] - 48) * 8 + (s[++i] - 48)) > 127) {
                    thebyte -= 256;
                }
                buf[bufpos++] = (byte)thebyte;
                continue;
            }
            buf[bufpos++] = nextbyte;
        }
        if (bufpos == correctSize) {
            return buf;
        }
        byte[] result = new byte[bufpos];
        System.arraycopy(buf, 0, result, 0, bufpos);
        return result;
    }

    public static @PolyNull String toPGString(byte @PolyNull [] buf) {
        if (buf == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(2 * buf.length);
        for (int n : buf) {
            int elementAsInt = n;
            if (elementAsInt < 0) {
                elementAsInt = 256 + elementAsInt;
            }
            if (elementAsInt < 32 || elementAsInt > 126) {
                stringBuilder.append("\\");
                stringBuilder.append((char)((elementAsInt >> 6 & 3) + 48));
                stringBuilder.append((char)((elementAsInt >> 3 & 7) + 48));
                stringBuilder.append((char)((elementAsInt & 7) + 48));
                continue;
            }
            if (n == 92) {
                stringBuilder.append("\\\\");
                continue;
            }
            stringBuilder.append((char)n);
        }
        return stringBuilder.toString();
    }

    static {
        int i;
        HEX_VALS = new int[55];
        for (i = 0; i < 10; ++i) {
            PGbytea.HEX_VALS[i] = (byte)i;
        }
        for (i = 0; i < 6; ++i) {
            PGbytea.HEX_VALS[65 + i - 48] = (byte)(10 + i);
            PGbytea.HEX_VALS[97 + i - 48] = (byte)(10 + i);
        }
    }
}

