/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LZWDecompresser;
import org.apache.poi.util.LittleEndian;

public final class CompressedRTF
extends LZWDecompresser {
    static final byte[] COMPRESSED_SIGNATURE = new byte[]{76, 90, 70, 117};
    static final byte[] UNCOMPRESSED_SIGNATURE = new byte[]{77, 69, 76, 65};
    public static final int COMPRESSED_SIGNATURE_INT = LittleEndian.getInt(COMPRESSED_SIGNATURE);
    public static final int UNCOMPRESSED_SIGNATURE_INT = LittleEndian.getInt(UNCOMPRESSED_SIGNATURE);
    public static final String LZW_RTF_PRELOAD = "{\\rtf1\\ansi\\mac\\deff0\\deftab720{\\fonttbl;}{\\f0\\fnil \\froman \\fswiss \\fmodern \\fscript \\fdecor MS Sans SerifSymbolArialTimes New RomanCourier{\\colortbl\\red0\\green0\\blue0\n\r\\par \\pard\\plain\\f0\\fs20\\b\\i\\u\\tab\\tx";
    private int compressedSize;
    private int decompressedSize;

    public CompressedRTF() {
        super(true, 2, true);
    }

    @Override
    public void decompress(InputStream src, OutputStream res) throws IOException {
        this.compressedSize = LittleEndian.readInt(src);
        this.decompressedSize = LittleEndian.readInt(src);
        int compressionType = LittleEndian.readInt(src);
        LittleEndian.readInt(src);
        if (compressionType == UNCOMPRESSED_SIGNATURE_INT) {
            IOUtils.copy(src, res);
        } else if (compressionType != COMPRESSED_SIGNATURE_INT) {
            throw new IllegalArgumentException("Invalid compression signature " + compressionType);
        }
        super.decompress(src, res);
    }

    public int getCompressedSize() {
        return this.compressedSize - 12;
    }

    public int getDeCompressedSize() {
        return this.decompressedSize;
    }

    @Override
    protected int adjustDictionaryOffset(int offset) {
        return offset;
    }

    @Override
    protected int populateDictionary(byte[] dict) {
        byte[] preload = LZW_RTF_PRELOAD.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(preload, 0, dict, 0, preload.length);
        return preload.length;
    }
}

