/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class ScannerHelper {
    public static final long[] Bits = new long[]{1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 0x100000L, 0x200000L, 0x400000L, 0x800000L, 0x1000000L, 0x2000000L, 0x4000000L, 0x8000000L, 0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L, 0x100000000L, 0x200000000L, 0x400000000L, 0x800000000L, 0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L, 0x10000000000L, 0x20000000000L, 0x40000000000L, 0x80000000000L, 0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L, 0x1000000000000L, 0x2000000000000L, 0x4000000000000L, 0x8000000000000L, 0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L, 0x100000000000000L, 0x200000000000000L, 0x400000000000000L, 0x800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE};
    private static final int START_INDEX = 0;
    private static final int PART_INDEX = 1;
    private static long[][][] Tables;
    private static long[][][] Tables7;
    private static long[][][] Tables8;
    private static long[][][] Tables9;
    private static long[][][] Tables11;
    private static long[][][] Tables12;
    private static long[][][] Tables13;
    private static long[][][] Tables15;
    public static final int MAX_OBVIOUS = 128;
    public static final int[] OBVIOUS_IDENT_CHAR_NATURES;
    public static final int C_JLS_SPACE = 256;
    public static final int C_SPECIAL = 128;
    public static final int C_IDENT_START = 64;
    public static final int C_UPPER_LETTER = 32;
    public static final int C_LOWER_LETTER = 16;
    public static final int C_IDENT_PART = 8;
    public static final int C_DIGIT = 4;
    public static final int C_SEPARATOR = 2;
    public static final int C_SPACE = 1;

    static {
        OBVIOUS_IDENT_CHAR_NATURES = new int[128];
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[0] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[1] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[2] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[3] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[4] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[5] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[6] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[7] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[8] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[14] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[15] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[16] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[17] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[18] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[19] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[20] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[21] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[22] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[23] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[24] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[25] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[26] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[27] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[127] = 8;
        int i = 48;
        while (i <= 57) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 12;
            ++i;
        }
        i = 97;
        while (i <= 122) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 88;
            ++i;
        }
        i = 65;
        while (i <= 90) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 104;
            ++i;
        }
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[95] = 200;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[36] = 200;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[9] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[10] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[11] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[12] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[13] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[28] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[29] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[30] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[31] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[32] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[46] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[58] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[59] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[44] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[91] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[93] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[40] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[41] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[123] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[125] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[43] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[45] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[42] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[47] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[61] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[38] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[124] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[63] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[60] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[62] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[33] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[37] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[94] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[126] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[34] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[39] = 2;
    }

    static void initializeTable() {
        Tables = ScannerHelper.initializeTables("unicode");
    }

    static void initializeTable17() {
        Tables7 = ScannerHelper.initializeTables("unicode6");
    }

    static void initializeTable18() {
        Tables8 = ScannerHelper.initializeTables("unicode6_2");
    }

    static void initializeTable19() {
        Tables9 = ScannerHelper.initializeTables("unicode8");
    }

    static void initializeTableJava11() {
        Tables11 = ScannerHelper.initializeTables("unicode10");
    }

    static void initializeTableJava12() {
        Tables12 = ScannerHelper.initializeTables("unicode11");
    }

    static void initializeTableJava13() {
        Tables13 = ScannerHelper.initializeTables("unicode12_1");
    }

    static void initializeTableJava15() {
        Tables15 = ScannerHelper.initializeTables13andPlus("unicode13");
    }

    static long[][][] initializeTables(String unicode_path) {
        Throwable e2;
        int i;
        long[] readValues;
        DataInputStream inputStream;
        Object var3_17;
        long[][][] tempTable = new long[][][]{new long[3][], new long[4][]};
        try {
            Throwable throwable = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start0.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][0] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start1.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][1] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start2.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][2] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part0.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][0] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e5) {
            e5.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part1.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][1] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e6) {
            e6.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part2.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][2] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e7) {
            e7.printStackTrace();
        }
        try {
            e2 = null;
            var3_17 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part14.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][3] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e8) {
            e8.printStackTrace();
        }
        return tempTable;
    }

    static long[][][] initializeTables13andPlus(String unicode_path) {
        Throwable e2;
        int i;
        long[] readValues;
        DataInputStream inputStream;
        Object var3_21;
        long[][][] tempTable = new long[][][]{new long[4][], new long[5][]};
        try {
            Throwable throwable = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start0.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][0] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start1.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][1] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start2.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][2] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start3.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[0][3] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e5) {
            e5.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part0.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][0] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e6) {
            e6.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part1.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][1] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e7) {
            e7.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part2.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][2] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e8) {
            e8.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part3.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][3] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e9) {
            e9.printStackTrace();
        }
        try {
            e2 = null;
            var3_21 = null;
            try {
                inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part14.rsc")));
                try {
                    readValues = new long[1024];
                    i = 0;
                    while (i < 1024) {
                        readValues[i] = inputStream.readLong();
                        ++i;
                    }
                    tempTable[1][4] = readValues;
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (Throwable throwable) {
                if (e2 == null) {
                    e2 = throwable;
                } else if (e2 != throwable) {
                    e2.addSuppressed(throwable);
                }
                throw e2;
            }
        }
        catch (IOException e10) {
            e10.printStackTrace();
        }
        return tempTable;
    }

    private static final boolean isBitSet(long[] values, int i) {
        try {
            return (values[i / 64] & Bits[i % 64]) != 0L;
        }
        catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public static boolean isJavaIdentifierPart(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 8) != 0;
        }
        return Character.isJavaIdentifierPart(c);
    }

    public static boolean isJavaIdentifierPart(long complianceLevel, char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 8) != 0;
        }
        return ScannerHelper.isJavaIdentifierPart(complianceLevel, (int)c);
    }

    private static boolean isJavaIdentifierPart0(int codePoint, long[][][] tables) {
        return ScannerHelper.isJavaIdentifierPart0(codePoint, tables, false);
    }

    private static boolean isJavaIdentifierPart0(int codePoint, long[][][] tables, boolean isJava15orAbove) {
        switch ((codePoint & 0x1F0000) >> 16) {
            case 0: {
                return ScannerHelper.isBitSet(tables[1][0], codePoint & 0xFFFF);
            }
            case 1: {
                return ScannerHelper.isBitSet(tables[1][1], codePoint & 0xFFFF);
            }
            case 2: {
                return ScannerHelper.isBitSet(tables[1][2], codePoint & 0xFFFF);
            }
            case 3: {
                if (isJava15orAbove) {
                    return ScannerHelper.isBitSet(tables[1][3], codePoint & 0xFFFF);
                }
                return false;
            }
            case 14: {
                if (isJava15orAbove) {
                    return ScannerHelper.isBitSet(tables[1][4], codePoint & 0xFFFF);
                }
                return ScannerHelper.isBitSet(tables[1][3], codePoint & 0xFFFF);
            }
        }
        return false;
    }

    public static boolean isJavaIdentifierPart(long complianceLevel, int codePoint) {
        if (complianceLevel <= 0x320000L) {
            if (Tables == null) {
                ScannerHelper.initializeTable();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables);
        }
        if (complianceLevel <= 0x330000L) {
            if (Tables7 == null) {
                ScannerHelper.initializeTable17();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables7);
        }
        if (complianceLevel <= 0x340000L) {
            if (Tables8 == null) {
                ScannerHelper.initializeTable18();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables8);
        }
        if (complianceLevel <= 0x360000L) {
            if (Tables9 == null) {
                ScannerHelper.initializeTable19();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables9);
        }
        if (complianceLevel <= 0x370000L) {
            if (Tables11 == null) {
                ScannerHelper.initializeTableJava11();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables11);
        }
        if (complianceLevel <= 0x380000L) {
            if (Tables12 == null) {
                ScannerHelper.initializeTableJava12();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables12);
        }
        if (complianceLevel <= 0x3A0000L) {
            if (Tables13 == null) {
                ScannerHelper.initializeTableJava13();
            }
            return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables13);
        }
        if (Tables15 == null) {
            ScannerHelper.initializeTableJava15();
        }
        return ScannerHelper.isJavaIdentifierPart0(codePoint, Tables15, true);
    }

    public static boolean isJavaIdentifierPart(long complianceLevel, char high, char low) {
        return ScannerHelper.isJavaIdentifierPart(complianceLevel, ScannerHelper.toCodePoint(high, low));
    }

    public static boolean isJavaIdentifierStart(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0;
        }
        return Character.isJavaIdentifierStart(c);
    }

    public static boolean isJavaIdentifierStart(long complianceLevel, char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0;
        }
        return ScannerHelper.isJavaIdentifierStart(complianceLevel, (int)c);
    }

    public static boolean isJavaIdentifierStart(long complianceLevel, char high, char low) {
        return ScannerHelper.isJavaIdentifierStart(complianceLevel, ScannerHelper.toCodePoint(high, low));
    }

    private static boolean isJavaIdentifierStart0(int codePoint, long[][][] tables) {
        return ScannerHelper.isJavaIdentifierStart0(codePoint, tables, false);
    }

    private static boolean isJavaIdentifierStart0(int codePoint, long[][][] tables, boolean isJava15orAbove) {
        switch ((codePoint & 0x1F0000) >> 16) {
            case 0: {
                return ScannerHelper.isBitSet(tables[0][0], codePoint & 0xFFFF);
            }
            case 1: {
                return ScannerHelper.isBitSet(tables[0][1], codePoint & 0xFFFF);
            }
            case 2: {
                return ScannerHelper.isBitSet(tables[0][2], codePoint & 0xFFFF);
            }
            case 3: {
                if (isJava15orAbove) {
                    return ScannerHelper.isBitSet(tables[0][3], codePoint & 0xFFFF);
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isJavaIdentifierStart(long complianceLevel, int codePoint) {
        if (complianceLevel <= 0x320000L) {
            if (Tables == null) {
                ScannerHelper.initializeTable();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables);
        }
        if (complianceLevel <= 0x330000L) {
            if (Tables7 == null) {
                ScannerHelper.initializeTable17();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables7);
        }
        if (complianceLevel <= 0x340000L) {
            if (Tables8 == null) {
                ScannerHelper.initializeTable18();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables8);
        }
        if (complianceLevel <= 0x360000L) {
            if (Tables9 == null) {
                ScannerHelper.initializeTable19();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables9);
        }
        if (complianceLevel <= 0x370000L) {
            if (Tables11 == null) {
                ScannerHelper.initializeTableJava11();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables11);
        }
        if (complianceLevel <= 0x380000L) {
            if (Tables12 == null) {
                ScannerHelper.initializeTableJava12();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables12);
        }
        if (complianceLevel <= 0x3A0000L) {
            if (Tables13 == null) {
                ScannerHelper.initializeTableJava13();
            }
            return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables13);
        }
        if (Tables15 == null) {
            ScannerHelper.initializeTableJava15();
        }
        return ScannerHelper.isJavaIdentifierStart0(codePoint, Tables15, true);
    }

    private static int toCodePoint(char high, char low) {
        return (high - 55296) * 1024 + (low - 56320) + 65536;
    }

    public static boolean isDigit(char c) throws InvalidInputException {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 4) != 0;
        }
        return Character.isDigit(c);
    }

    public static int digit(char c, int radix) {
        if (c < '\u0080') {
            switch (radix) {
                case 8: {
                    if (c >= '0' && c <= '7') {
                        return c - 48;
                    }
                    return -1;
                }
                case 10: {
                    if (c >= '0' && c <= '9') {
                        return c - 48;
                    }
                    return -1;
                }
                case 16: {
                    if (c >= '0' && c <= '9') {
                        return c - 48;
                    }
                    if (c >= 'A' && c <= 'F') {
                        return c - 65 + 10;
                    }
                    if (c >= 'a' && c <= 'f') {
                        return c - 97 + 10;
                    }
                    return -1;
                }
            }
        }
        return Character.digit(c, radix);
    }

    public static int getNumericValue(char c) {
        if (c < '\u0080') {
            switch (OBVIOUS_IDENT_CHAR_NATURES[c]) {
                case 4: {
                    return c - 48;
                }
                case 16: {
                    return 10 + c - 97;
                }
                case 32: {
                    return 10 + c - 65;
                }
            }
        }
        return Character.getNumericValue(c);
    }

    public static int getHexadecimalValue(char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
            case 'A': 
            case 'a': {
                return 10;
            }
            case 'B': 
            case 'b': {
                return 11;
            }
            case 'C': 
            case 'c': {
                return 12;
            }
            case 'D': 
            case 'd': {
                return 13;
            }
            case 'E': 
            case 'e': {
                return 14;
            }
            case 'F': 
            case 'f': {
                return 15;
            }
        }
        return -1;
    }

    public static char toUpperCase(char c) {
        if (c < '\u0080') {
            if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0) {
                return c;
            }
            if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0) {
                return (char)(c - 32);
            }
        }
        return Character.toUpperCase(c);
    }

    public static char toLowerCase(char c) {
        if (c < '\u0080') {
            if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0) {
                return c;
            }
            if ((OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0) {
                return (char)(32 + c);
            }
        }
        return Character.toLowerCase(c);
    }

    public static boolean isLowerCase(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0;
        }
        return Character.isLowerCase(c);
    }

    public static boolean isUpperCase(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0;
        }
        return Character.isUpperCase(c);
    }

    public static boolean isWhitespace(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 1) != 0;
        }
        return Character.isWhitespace(c);
    }

    public static boolean isLetter(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x30) != 0;
        }
        return Character.isLetter(c);
    }

    public static boolean isLetterOrDigit(char c) {
        if (c < '\u0080') {
            return (OBVIOUS_IDENT_CHAR_NATURES[c] & 0x34) != 0;
        }
        return Character.isLetterOrDigit(c);
    }
}

