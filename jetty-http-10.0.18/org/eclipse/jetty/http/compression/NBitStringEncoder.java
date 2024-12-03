/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.http.compression.HuffmanEncoder;
import org.eclipse.jetty.http.compression.NBitIntegerEncoder;

public class NBitStringEncoder {
    private NBitStringEncoder() {
    }

    public static int octetsNeeded(int prefix, String value, boolean huffman) {
        if (prefix <= 0 || prefix > 8) {
            throw new IllegalArgumentException();
        }
        int contentPrefix = prefix == 1 ? 8 : prefix - 1;
        int encodedValueSize = huffman ? HuffmanEncoder.octetsNeeded(value) : value.length();
        int encodedLengthSize = NBitIntegerEncoder.octetsNeeded(contentPrefix, encodedValueSize);
        return encodedLengthSize + encodedValueSize + (prefix == 1 ? 1 : 0);
    }

    public static void encode(ByteBuffer buffer, int prefix, String value, boolean huffman) {
        int encodedValueSize;
        byte huffmanFlag;
        if (prefix <= 0 || prefix > 8) {
            throw new IllegalArgumentException();
        }
        byte by = huffmanFlag = huffman ? (byte)(1 << prefix - 1) : (byte)0;
        if (prefix == 8) {
            buffer.put(huffmanFlag);
        } else {
            int p = buffer.position() - 1;
            buffer.put(p, (byte)(buffer.get(p) | huffmanFlag));
        }
        int n = prefix = prefix == 1 ? 8 : prefix - 1;
        if (huffman) {
            encodedValueSize = HuffmanEncoder.octetsNeeded(value);
            NBitIntegerEncoder.encode(buffer, prefix, encodedValueSize);
            HuffmanEncoder.encode(buffer, value);
        } else {
            encodedValueSize = value.length();
            NBitIntegerEncoder.encode(buffer, prefix, encodedValueSize);
            for (int i = 0; i < encodedValueSize; ++i) {
                char c = value.charAt(i);
                c = HttpTokens.sanitizeFieldVchar(c);
                buffer.put((byte)c);
            }
        }
    }
}

