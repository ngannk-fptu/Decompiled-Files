/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;

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
        BytesRef result = new BytesRef();
        UnicodeUtil.UTF16toUTF8(value, 0, value.length(), result);
        return CompressionTools.compress(result.bytes, 0, result.length, compressionLevel);
    }

    public static byte[] decompress(BytesRef bytes) throws DataFormatException {
        return CompressionTools.decompress(bytes.bytes, bytes.offset, bytes.length);
    }

    public static byte[] decompress(byte[] value) throws DataFormatException {
        return CompressionTools.decompress(value, 0, value.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] decompress(byte[] value, int offset, int length) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(value, offset, length);
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
        return CompressionTools.decompressString(value, 0, value.length);
    }

    public static String decompressString(byte[] value, int offset, int length) throws DataFormatException {
        byte[] bytes = CompressionTools.decompress(value, offset, length);
        CharsRef result = new CharsRef(bytes.length);
        UnicodeUtil.UTF8toUTF16(bytes, 0, bytes.length, result);
        return new String(result.chars, 0, result.length);
    }

    public static String decompressString(BytesRef bytes) throws DataFormatException {
        return CompressionTools.decompressString(bytes.bytes, bytes.offset, bytes.length);
    }
}

