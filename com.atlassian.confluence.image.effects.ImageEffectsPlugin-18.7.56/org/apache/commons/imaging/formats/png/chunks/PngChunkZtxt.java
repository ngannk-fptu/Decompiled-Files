/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.InflaterInputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.PngText;
import org.apache.commons.imaging.formats.png.chunks.PngTextChunk;

public class PngChunkZtxt
extends PngTextChunk {
    public final String keyword;
    public final String text;

    public PngChunkZtxt(int length, int chunkType, int crc, byte[] bytes) throws ImageReadException, IOException {
        super(length, chunkType, crc, bytes);
        int index = BinaryFunctions.findNull(bytes);
        if (index < 0) {
            throw new ImageReadException("PNG zTXt chunk keyword is unterminated.");
        }
        this.keyword = new String(bytes, 0, index, StandardCharsets.ISO_8859_1);
        int n = ++index;
        ++index;
        byte compressionMethod = bytes[n];
        if (compressionMethod != 0) {
            throw new ImageReadException("PNG zTXt chunk has unexpected compression method: " + compressionMethod);
        }
        int compressedTextLength = bytes.length - index;
        byte[] compressedText = new byte[compressedTextLength];
        System.arraycopy(bytes, index, compressedText, 0, compressedTextLength);
        this.text = new String(BinaryFunctions.getStreamBytes(new InflaterInputStream(new ByteArrayInputStream(compressedText))), StandardCharsets.ISO_8859_1);
    }

    @Override
    public String getKeyword() {
        return this.keyword;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public PngText getContents() {
        return new PngText.Ztxt(this.keyword, this.text);
    }
}

