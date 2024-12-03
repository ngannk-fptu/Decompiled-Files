/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionTools {
    private CompressionTools() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] compress(byte[] value, int offset, int length, int compressionLevel) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        Deflater compressor = new Deflater();
        try {
            compressor.setLevel(compressionLevel);
            compressor.setInput(value, offset, length);
            compressor.finish();
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        }
        finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

    public static byte[] compress(byte[] value, int offset, int length) {
        return CompressionTools.compress(value, offset, length, 9);
    }

    public static byte[] compress(byte[] value) {
        return CompressionTools.compress(value, 0, value.length, 9);
    }

    public static byte[] compressString(String value) {
        return CompressionTools.compressString(value, 9);
    }

    public static byte[] compressString(String value, int compressionLevel) {
        UnicodeUtil.UTF8Result result = new UnicodeUtil.UTF8Result();
        UnicodeUtil.UTF16toUTF8(value, 0, value.length(), result);
        return CompressionTools.compress(result.result, 0, result.length, compressionLevel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] value) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(value.length);
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(value);
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        }
        finally {
            decompressor.end();
        }
        return bos.toByteArray();
    }

    public static String decompressString(byte[] value) throws DataFormatException {
        UnicodeUtil.UTF16Result result = new UnicodeUtil.UTF16Result();
        byte[] bytes = CompressionTools.decompress(value);
        UnicodeUtil.UTF8toUTF16(bytes, 0, bytes.length, result);
        return new String(result.result, 0, result.length);
    }
}

