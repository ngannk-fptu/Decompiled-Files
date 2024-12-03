/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.itu_t4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.itu_t4.BitArrayOutputStream;
import org.apache.commons.imaging.common.itu_t4.BitInputStreamFlexible;
import org.apache.commons.imaging.common.itu_t4.HuffmanTree;
import org.apache.commons.imaging.common.itu_t4.HuffmanTreeException;
import org.apache.commons.imaging.common.itu_t4.T4_T6_Tables;

public final class T4AndT6Compression {
    private static final HuffmanTree<Integer> WHITE_RUN_LENGTHS = new HuffmanTree();
    private static final HuffmanTree<Integer> BLACK_RUN_LENGTHS = new HuffmanTree();
    private static final HuffmanTree<T4_T6_Tables.Entry> CONTROL_CODES = new HuffmanTree();
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    private T4AndT6Compression() {
    }

    private static void compress1DLine(BitInputStreamFlexible inputStream, BitArrayOutputStream outputStream, int[] referenceLine, int width) throws ImageWriteException {
        int color = 0;
        int runLength = 0;
        for (int x = 0; x < width; ++x) {
            try {
                int nextColor = inputStream.readBits(1);
                if (referenceLine != null) {
                    referenceLine[x] = nextColor;
                }
                if (color == nextColor) {
                    ++runLength;
                    continue;
                }
                T4AndT6Compression.writeRunLength(outputStream, runLength, color);
                color = nextColor;
                runLength = 1;
                continue;
            }
            catch (IOException ioException) {
                throw new ImageWriteException("Error reading image to compress", ioException);
            }
        }
        T4AndT6Compression.writeRunLength(outputStream, runLength, color);
    }

    public static byte[] compressModifiedHuffman(byte[] uncompressed, int width, int height) throws ImageWriteException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(uncompressed));
        try (BitArrayOutputStream outputStream = new BitArrayOutputStream();){
            for (int y = 0; y < height; ++y) {
                T4AndT6Compression.compress1DLine(inputStream, outputStream, null, width);
                inputStream.flushCache();
                outputStream.flush();
            }
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
    }

    /*
     * Exception decompiling
     */
    public static byte[] decompressModifiedHuffman(byte[] compressed, int width, int height) throws ImageReadException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static byte[] compressT4_1D(byte[] uncompressed, int width, int height, boolean hasFill) throws ImageWriteException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(uncompressed));
        try (BitArrayOutputStream outputStream = new BitArrayOutputStream();){
            if (hasFill) {
                T4_T6_Tables.EOL16.writeBits(outputStream);
            } else {
                T4_T6_Tables.EOL.writeBits(outputStream);
            }
            for (int y = 0; y < height; ++y) {
                T4AndT6Compression.compress1DLine(inputStream, outputStream, null, width);
                if (hasFill) {
                    int bitsAvailable = outputStream.getBitsAvailableInCurrentByte();
                    if (bitsAvailable < 4) {
                        outputStream.flush();
                        bitsAvailable = 8;
                    }
                    while (bitsAvailable > 4) {
                        outputStream.writeBit(0);
                        --bitsAvailable;
                    }
                }
                T4_T6_Tables.EOL.writeBits(outputStream);
                inputStream.flushCache();
            }
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
    }

    public static byte[] decompressT4_1D(byte[] compressed, int width, int height, boolean hasFill) throws ImageReadException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(compressed));
        try (BitArrayOutputStream outputStream = new BitArrayOutputStream();){
            byte[] ret;
            for (int y = 0; y < height; ++y) {
                int rowLength;
                try {
                    int runLength;
                    T4_T6_Tables.Entry entry = CONTROL_CODES.decode(inputStream);
                    if (!T4AndT6Compression.isEOL(entry, hasFill)) {
                        throw new ImageReadException("Expected EOL not found");
                    }
                    int color = 0;
                    for (rowLength = 0; rowLength < width; rowLength += runLength) {
                        runLength = T4AndT6Compression.readTotalRunLength(inputStream, color);
                        for (int i = 0; i < runLength; ++i) {
                            outputStream.writeBit(color);
                        }
                        color = 1 - color;
                    }
                }
                catch (HuffmanTreeException huffmanException) {
                    throw new ImageReadException("Decompression error", huffmanException);
                }
                if (rowLength == width) {
                    outputStream.flush();
                    continue;
                }
                if (rowLength <= width) continue;
                throw new ImageReadException("Unrecoverable row length error in image row " + y);
            }
            byte[] byArray = ret = outputStream.toByteArray();
            return byArray;
        }
    }

    private static int compressT(int a0, int a1, int b1, BitArrayOutputStream outputStream, int codingA0Color, int[] codingLine) {
        int a1b1 = a1 - b1;
        if (-3 <= a1b1 && a1b1 <= 3) {
            T4_T6_Tables.Entry entry = a1b1 == -3 ? T4_T6_Tables.VL3 : (a1b1 == -2 ? T4_T6_Tables.VL2 : (a1b1 == -1 ? T4_T6_Tables.VL1 : (a1b1 == 0 ? T4_T6_Tables.V0 : (a1b1 == 1 ? T4_T6_Tables.VR1 : (a1b1 == 2 ? T4_T6_Tables.VR2 : T4_T6_Tables.VR3)))));
            entry.writeBits(outputStream);
            return a1;
        }
        int a2 = T4AndT6Compression.nextChangingElement(codingLine, 1 - codingA0Color, a1 + 1);
        int a0a1 = a1 - a0;
        int a1a2 = a2 - a1;
        T4_T6_Tables.H.writeBits(outputStream);
        T4AndT6Compression.writeRunLength(outputStream, a0a1, codingA0Color);
        T4AndT6Compression.writeRunLength(outputStream, a1a2, 1 - codingA0Color);
        return a2;
    }

    public static byte[] compressT4_2D(byte[] uncompressed, int width, int height, boolean hasFill, int parameterK) throws ImageWriteException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(uncompressed));
        BitArrayOutputStream outputStream = new BitArrayOutputStream();
        int[] referenceLine = new int[width];
        int[] codingLine = new int[width];
        int kCounter = 0;
        if (hasFill) {
            T4_T6_Tables.EOL16.writeBits(outputStream);
        } else {
            T4_T6_Tables.EOL.writeBits(outputStream);
        }
        for (int y = 0; y < height; ++y) {
            if (kCounter > 0) {
                outputStream.writeBit(0);
                for (int i = 0; i < width; ++i) {
                    try {
                        codingLine[i] = inputStream.readBits(1);
                        continue;
                    }
                    catch (IOException ioException) {
                        throw new ImageWriteException("Error reading image to compress", ioException);
                    }
                }
                int codingA0Color = 0;
                int referenceA0Color = 0;
                int a1 = T4AndT6Compression.nextChangingElement(codingLine, codingA0Color, 0);
                int b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, 0);
                int b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                int a0 = 0;
                while (a0 < width) {
                    if (b2 < a1) {
                        T4_T6_Tables.P.writeBits(outputStream);
                        a0 = b2;
                    } else if ((a0 = T4AndT6Compression.compressT(a0, a1, b1, outputStream, codingA0Color, codingLine)) == a1) {
                        codingA0Color = 1 - codingA0Color;
                    }
                    referenceA0Color = T4AndT6Compression.changingElementAt(referenceLine, a0);
                    a1 = T4AndT6Compression.nextChangingElement(codingLine, codingA0Color, a0 + 1);
                    if (codingA0Color == referenceA0Color) {
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                    } else {
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                    }
                    b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - codingA0Color, b1 + 1);
                }
                int[] swap = referenceLine;
                referenceLine = codingLine;
                codingLine = swap;
            } else {
                outputStream.writeBit(1);
                T4AndT6Compression.compress1DLine(inputStream, outputStream, referenceLine, width);
            }
            if (hasFill) {
                int bitsAvailable = outputStream.getBitsAvailableInCurrentByte();
                if (bitsAvailable < 4) {
                    outputStream.flush();
                    bitsAvailable = 8;
                }
                while (bitsAvailable > 4) {
                    outputStream.writeBit(0);
                    --bitsAvailable;
                }
            }
            T4_T6_Tables.EOL.writeBits(outputStream);
            if (++kCounter == parameterK) {
                kCounter = 0;
            }
            inputStream.flushCache();
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressT4_2D(byte[] compressed, int width, int height, boolean hasFill) throws ImageReadException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(compressed));
        try (BitArrayOutputStream outputStream = new BitArrayOutputStream();){
            int[] referenceLine = new int[width];
            for (int y = 0; y < height; ++y) {
                int rowLength = 0;
                try {
                    T4_T6_Tables.Entry entry = CONTROL_CODES.decode(inputStream);
                    if (!T4AndT6Compression.isEOL(entry, hasFill)) {
                        throw new ImageReadException("Expected EOL not found");
                    }
                    int tagBit = inputStream.readBits(1);
                    if (tagBit == 0) {
                        int codingA0Color = 0;
                        int referenceA0Color = 0;
                        int b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, 0);
                        int b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                        int a0 = 0;
                        while (a0 < width) {
                            int a1;
                            entry = CONTROL_CODES.decode(inputStream);
                            if (entry == T4_T6_Tables.P) {
                                T4AndT6Compression.fillRange(outputStream, referenceLine, a0, b2, codingA0Color);
                                a0 = b2;
                            } else if (entry == T4_T6_Tables.H) {
                                int a0a1 = T4AndT6Compression.readTotalRunLength(inputStream, codingA0Color);
                                a1 = a0 + a0a1;
                                T4AndT6Compression.fillRange(outputStream, referenceLine, a0, a1, codingA0Color);
                                int a1a2 = T4AndT6Compression.readTotalRunLength(inputStream, 1 - codingA0Color);
                                int a2 = a1 + a1a2;
                                T4AndT6Compression.fillRange(outputStream, referenceLine, a1, a2, 1 - codingA0Color);
                                a0 = a2;
                            } else {
                                int a1b1;
                                if (entry == T4_T6_Tables.V0) {
                                    a1b1 = 0;
                                } else if (entry == T4_T6_Tables.VL1) {
                                    a1b1 = -1;
                                } else if (entry == T4_T6_Tables.VL2) {
                                    a1b1 = -2;
                                } else if (entry == T4_T6_Tables.VL3) {
                                    a1b1 = -3;
                                } else if (entry == T4_T6_Tables.VR1) {
                                    a1b1 = 1;
                                } else if (entry == T4_T6_Tables.VR2) {
                                    a1b1 = 2;
                                } else if (entry == T4_T6_Tables.VR3) {
                                    a1b1 = 3;
                                } else {
                                    throw new ImageReadException("Invalid/unknown T.4 control code " + entry.bitString);
                                }
                                a1 = b1 + a1b1;
                                T4AndT6Compression.fillRange(outputStream, referenceLine, a0, a1, codingA0Color);
                                a0 = a1;
                                codingA0Color = 1 - codingA0Color;
                            }
                            referenceA0Color = T4AndT6Compression.changingElementAt(referenceLine, a0);
                            if (codingA0Color == referenceA0Color) {
                                b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                            } else {
                                b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                                b1 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                            }
                            b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - codingA0Color, b1 + 1);
                            rowLength = a0;
                        }
                    } else {
                        int runLength;
                        int color = 0;
                        for (rowLength = 0; rowLength < width; rowLength += runLength) {
                            runLength = T4AndT6Compression.readTotalRunLength(inputStream, color);
                            for (int i = 0; i < runLength; ++i) {
                                outputStream.writeBit(color);
                                referenceLine[rowLength + i] = color;
                            }
                            color = 1 - color;
                        }
                    }
                }
                catch (IOException ioException) {
                    throw new ImageReadException("Decompression error", ioException);
                }
                catch (HuffmanTreeException huffmanException) {
                    throw new ImageReadException("Decompression error", huffmanException);
                }
                if (rowLength == width) {
                    outputStream.flush();
                    continue;
                }
                if (rowLength <= width) continue;
                throw new ImageReadException("Unrecoverable row length error in image row " + y);
            }
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
    }

    /*
     * Exception decompiling
     */
    public static byte[] compressT6(byte[] uncompressed, int width, int height) throws ImageWriteException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static byte[] decompressT6(byte[] compressed, int width, int height) throws ImageReadException {
        BitInputStreamFlexible inputStream = new BitInputStreamFlexible(new ByteArrayInputStream(compressed));
        BitArrayOutputStream outputStream = new BitArrayOutputStream();
        int[] referenceLine = new int[width];
        for (int y = 0; y < height; ++y) {
            int rowLength = 0;
            try {
                int codingA0Color = 0;
                int referenceA0Color = 0;
                int b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, 0);
                int b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                int a0 = 0;
                while (a0 < width) {
                    int a1;
                    T4_T6_Tables.Entry entry = CONTROL_CODES.decode(inputStream);
                    if (entry == T4_T6_Tables.P) {
                        T4AndT6Compression.fillRange(outputStream, referenceLine, a0, b2, codingA0Color);
                        a0 = b2;
                    } else if (entry == T4_T6_Tables.H) {
                        int a0a1 = T4AndT6Compression.readTotalRunLength(inputStream, codingA0Color);
                        a1 = a0 + a0a1;
                        T4AndT6Compression.fillRange(outputStream, referenceLine, a0, a1, codingA0Color);
                        int a1a2 = T4AndT6Compression.readTotalRunLength(inputStream, 1 - codingA0Color);
                        int a2 = a1 + a1a2;
                        T4AndT6Compression.fillRange(outputStream, referenceLine, a1, a2, 1 - codingA0Color);
                        a0 = a2;
                    } else {
                        int a1b1;
                        if (entry == T4_T6_Tables.V0) {
                            a1b1 = 0;
                        } else if (entry == T4_T6_Tables.VL1) {
                            a1b1 = -1;
                        } else if (entry == T4_T6_Tables.VL2) {
                            a1b1 = -2;
                        } else if (entry == T4_T6_Tables.VL3) {
                            a1b1 = -3;
                        } else if (entry == T4_T6_Tables.VR1) {
                            a1b1 = 1;
                        } else if (entry == T4_T6_Tables.VR2) {
                            a1b1 = 2;
                        } else if (entry == T4_T6_Tables.VR3) {
                            a1b1 = 3;
                        } else {
                            throw new ImageReadException("Invalid/unknown T.6 control code " + entry.bitString);
                        }
                        a1 = b1 + a1b1;
                        T4AndT6Compression.fillRange(outputStream, referenceLine, a0, a1, codingA0Color);
                        a0 = a1;
                        codingA0Color = 1 - codingA0Color;
                    }
                    referenceA0Color = T4AndT6Compression.changingElementAt(referenceLine, a0);
                    if (codingA0Color == referenceA0Color) {
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                    } else {
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, referenceA0Color, a0 + 1);
                        b1 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - referenceA0Color, b1 + 1);
                    }
                    b2 = T4AndT6Compression.nextChangingElement(referenceLine, 1 - codingA0Color, b1 + 1);
                    rowLength = a0;
                }
            }
            catch (HuffmanTreeException huffmanException) {
                throw new ImageReadException("Decompression error", huffmanException);
            }
            if (rowLength == width) {
                outputStream.flush();
                continue;
            }
            if (rowLength <= width) continue;
            throw new ImageReadException("Unrecoverable row length error in image row " + y);
        }
        return outputStream.toByteArray();
    }

    private static boolean isEOL(T4_T6_Tables.Entry entry, boolean hasFill) {
        if (entry == T4_T6_Tables.EOL) {
            return true;
        }
        if (hasFill) {
            return entry == T4_T6_Tables.EOL13 || entry == T4_T6_Tables.EOL14 || entry == T4_T6_Tables.EOL15 || entry == T4_T6_Tables.EOL16 || entry == T4_T6_Tables.EOL17 || entry == T4_T6_Tables.EOL18 || entry == T4_T6_Tables.EOL19;
        }
        return false;
    }

    private static void writeRunLength(BitArrayOutputStream bitStream, int runLength, int color) {
        T4_T6_Tables.Entry entry;
        T4_T6_Tables.Entry[] terminatingCodes;
        T4_T6_Tables.Entry[] makeUpCodes;
        if (color == 0) {
            makeUpCodes = T4_T6_Tables.WHITE_MAKE_UP_CODES;
            terminatingCodes = T4_T6_Tables.WHITE_TERMINATING_CODES;
        } else {
            makeUpCodes = T4_T6_Tables.BLACK_MAKE_UP_CODES;
            terminatingCodes = T4_T6_Tables.BLACK_TERMINATING_CODES;
        }
        while (runLength >= 1792) {
            entry = T4AndT6Compression.lowerBound(T4_T6_Tables.ADDITIONAL_MAKE_UP_CODES, runLength);
            entry.writeBits(bitStream);
            runLength -= entry.value.intValue();
        }
        while (runLength >= 64) {
            entry = T4AndT6Compression.lowerBound(makeUpCodes, runLength);
            entry.writeBits(bitStream);
            runLength -= entry.value.intValue();
        }
        T4_T6_Tables.Entry terminatingEntry = terminatingCodes[runLength];
        terminatingEntry.writeBits(bitStream);
    }

    private static T4_T6_Tables.Entry lowerBound(T4_T6_Tables.Entry[] entries, int value) {
        int first = 0;
        int last = entries.length - 1;
        do {
            int middle = first + last >>> 1;
            if (entries[middle].value <= value && (middle + 1 >= entries.length || value < entries[middle + 1].value)) {
                return entries[middle];
            }
            if (entries[middle].value > value) {
                last = middle - 1;
                continue;
            }
            first = middle + 1;
        } while (first < last);
        return entries[first];
    }

    private static int readTotalRunLength(BitInputStreamFlexible bitStream, int color) throws ImageReadException {
        try {
            Integer runLength;
            int totalLength = 0;
            do {
                runLength = color == 0 ? WHITE_RUN_LENGTHS.decode(bitStream) : BLACK_RUN_LENGTHS.decode(bitStream);
                totalLength += runLength.intValue();
            } while (runLength > 63);
            return totalLength;
        }
        catch (HuffmanTreeException huffmanException) {
            throw new ImageReadException("Decompression error", huffmanException);
        }
    }

    private static int changingElementAt(int[] line, int position) {
        if (position < 0 || position >= line.length) {
            return 0;
        }
        return line[position];
    }

    private static int nextChangingElement(int[] line, int currentColour, int start) {
        int position;
        for (position = start; position < line.length && line[position] == currentColour; ++position) {
        }
        return Math.min(position, line.length);
    }

    private static void fillRange(BitArrayOutputStream outputStream, int[] referenceRow, int a0, int end, int color) {
        for (int i = a0; i < end; ++i) {
            referenceRow[i] = color;
            outputStream.writeBit(color);
        }
    }

    static {
        try {
            for (T4_T6_Tables.Entry entry : T4_T6_Tables.WHITE_TERMINATING_CODES) {
                WHITE_RUN_LENGTHS.insert(entry.bitString, entry.value);
            }
            for (T4_T6_Tables.Entry entry : T4_T6_Tables.WHITE_MAKE_UP_CODES) {
                WHITE_RUN_LENGTHS.insert(entry.bitString, entry.value);
            }
            for (T4_T6_Tables.Entry entry : T4_T6_Tables.BLACK_TERMINATING_CODES) {
                BLACK_RUN_LENGTHS.insert(entry.bitString, entry.value);
            }
            for (T4_T6_Tables.Entry entry : T4_T6_Tables.BLACK_MAKE_UP_CODES) {
                BLACK_RUN_LENGTHS.insert(entry.bitString, entry.value);
            }
            for (T4_T6_Tables.Entry entry : T4_T6_Tables.ADDITIONAL_MAKE_UP_CODES) {
                WHITE_RUN_LENGTHS.insert(entry.bitString, entry.value);
                BLACK_RUN_LENGTHS.insert(entry.bitString, entry.value);
            }
            CONTROL_CODES.insert(T4_T6_Tables.EOL.bitString, T4_T6_Tables.EOL);
            CONTROL_CODES.insert(T4_T6_Tables.EOL13.bitString, T4_T6_Tables.EOL13);
            CONTROL_CODES.insert(T4_T6_Tables.EOL14.bitString, T4_T6_Tables.EOL14);
            CONTROL_CODES.insert(T4_T6_Tables.EOL15.bitString, T4_T6_Tables.EOL15);
            CONTROL_CODES.insert(T4_T6_Tables.EOL16.bitString, T4_T6_Tables.EOL16);
            CONTROL_CODES.insert(T4_T6_Tables.EOL17.bitString, T4_T6_Tables.EOL17);
            CONTROL_CODES.insert(T4_T6_Tables.EOL18.bitString, T4_T6_Tables.EOL18);
            CONTROL_CODES.insert(T4_T6_Tables.EOL19.bitString, T4_T6_Tables.EOL19);
            CONTROL_CODES.insert(T4_T6_Tables.P.bitString, T4_T6_Tables.P);
            CONTROL_CODES.insert(T4_T6_Tables.H.bitString, T4_T6_Tables.H);
            CONTROL_CODES.insert(T4_T6_Tables.V0.bitString, T4_T6_Tables.V0);
            CONTROL_CODES.insert(T4_T6_Tables.VL1.bitString, T4_T6_Tables.VL1);
            CONTROL_CODES.insert(T4_T6_Tables.VL2.bitString, T4_T6_Tables.VL2);
            CONTROL_CODES.insert(T4_T6_Tables.VL3.bitString, T4_T6_Tables.VL3);
            CONTROL_CODES.insert(T4_T6_Tables.VR1.bitString, T4_T6_Tables.VR1);
            CONTROL_CODES.insert(T4_T6_Tables.VR2.bitString, T4_T6_Tables.VR2);
            CONTROL_CODES.insert(T4_T6_Tables.VR3.bitString, T4_T6_Tables.VR3);
        }
        catch (HuffmanTreeException cannotHappen) {
            throw new Error(cannotHappen);
        }
    }
}

