/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.PngText;
import org.apache.commons.imaging.formats.png.chunks.PngTextChunk;

public class PngChunkText
extends PngTextChunk {
    private static final Logger LOGGER = Logger.getLogger(PngChunkText.class.getName());
    public final String keyword;
    public final String text;

    public PngChunkText(int length, int chunkType, int crc, byte[] bytes) throws ImageReadException, IOException {
        super(length, chunkType, crc, bytes);
        int index = BinaryFunctions.findNull(bytes);
        if (index < 0) {
            throw new ImageReadException("PNG tEXt chunk keyword is not terminated.");
        }
        this.keyword = new String(bytes, 0, index, StandardCharsets.ISO_8859_1);
        int textLength = bytes.length - (index + 1);
        this.text = new String(bytes, index + 1, textLength, StandardCharsets.ISO_8859_1);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Keyword: " + this.keyword);
            LOGGER.finest("Text: " + this.text);
        }
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
        return new PngText.Text(this.keyword, this.text);
    }
}

