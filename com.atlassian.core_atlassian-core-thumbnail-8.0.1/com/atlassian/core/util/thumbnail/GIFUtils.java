/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.thumbnail;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;

final class GIFUtils {
    private static final ByteOrder GIF_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final int EXTENSION_BLOCK = 33;
    private static final int IMAGE_BLOCK = 44;
    private static final int GIF_END = 59;
    private static final int GRAPHICS_CONTROL_EXTENSION = 249;
    private static final int PLAIN_TEXT_EXTENSION = 1;
    private static final int APPLICATION_EXTENSION = 255;

    private GIFUtils() {
        throw new UnsupportedOperationException("Illegal instantiation of GIFUtils class.");
    }

    static boolean isGif(ImageInputStream stream) throws IOException {
        ByteOrder originalOrder = stream.getByteOrder();
        stream.setByteOrder(GIF_BYTE_ORDER);
        stream.mark();
        byte[] descriptor = GIFUtils.readBlock(stream, 6);
        stream.reset();
        stream.setByteOrder(originalOrder);
        return descriptor[0] == 71 && descriptor[1] == 73 && descriptor[2] == 70 && descriptor[3] == 56 && (descriptor[4] == 55 || descriptor[4] == 57) && descriptor[5] == 97;
    }

    /*
     * Exception decompiling
     */
    static ImageInputStream sanitize(ImageInputStream imageStream) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
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

    private static void readHeader(ImageInputStream stream, OutputStream output) throws IOException {
        byte[] header = GIFUtils.readBlock(stream, 13);
        output.write(header);
        output.write(GIFUtils.readColorTable(header[10], stream));
    }

    private static void readExtension(ImageInputStream stream, OutputStream output) throws IOException {
        int label = stream.readUnsignedByte();
        switch (label) {
            case 249: {
                output.write(33);
                output.write(249);
                output.write(GIFUtils.readBlock(stream, 6));
                return;
            }
            case 1: {
                stream.skipBytes(13);
                GIFUtils.skipUntilTerminate(stream);
                return;
            }
            case 255: {
                stream.skipBytes(12);
                GIFUtils.skipUntilTerminate(stream);
                return;
            }
        }
        GIFUtils.skipUntilTerminate(stream);
    }

    private static void skipUntilTerminate(ImageInputStream stream) throws IOException {
        int length = stream.readUnsignedByte();
        while (length != 0) {
            stream.skipBytes(length);
            length = stream.readUnsignedByte();
        }
    }

    private static void readImage(ImageInputStream stream, OutputStream output) throws IOException {
        output.write(44);
        byte[] imageInfo = GIFUtils.readBlock(stream, 9);
        output.write(imageInfo);
        output.write(GIFUtils.readColorTable(imageInfo[8], stream));
        output.write(stream.readByte());
        byte[] block = GIFUtils.readBlock(stream);
        while (block.length != 0) {
            output.write(block.length);
            output.write(block);
            block = GIFUtils.readBlock(stream);
        }
        output.write(0);
    }

    private static byte[] readColorTable(byte descriptor, ImageInputStream stream) throws IOException {
        boolean hasColorTable;
        boolean bl = hasColorTable = (descriptor & 0x80) != 0;
        if (hasColorTable) {
            int count = 1 << (descriptor & 7) + 1;
            return GIFUtils.readBlock(stream, 3 * count);
        }
        return new byte[0];
    }

    private static byte[] readBlock(ImageInputStream stream) throws IOException {
        int length = stream.readUnsignedByte();
        if (length == 0) {
            return new byte[0];
        }
        return GIFUtils.readBlock(stream, length);
    }

    private static byte[] readBlock(ImageInputStream stream, int length) throws IOException {
        byte[] content = new byte[length];
        stream.readFully(content);
        return content;
    }
}

