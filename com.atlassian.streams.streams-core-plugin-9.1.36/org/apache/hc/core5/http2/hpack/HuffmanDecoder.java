/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import java.nio.ByteBuffer;
import org.apache.hc.core5.http2.hpack.HPackException;
import org.apache.hc.core5.http2.hpack.HuffmanNode;
import org.apache.hc.core5.util.ByteArrayBuffer;

final class HuffmanDecoder {
    private final HuffmanNode root;

    HuffmanDecoder(int[] codes, byte[] lengths) {
        this.root = HuffmanDecoder.buildTree(codes, lengths);
    }

    void decode(ByteArrayBuffer out, ByteBuffer src) throws HPackException {
        int c;
        HuffmanNode node = this.root;
        int current = 0;
        int bits = 0;
        while (src.hasRemaining()) {
            int b = src.get() & 0xFF;
            current = current << 8 | b;
            bits += 8;
            while (bits >= 8) {
                int c2 = current >>> bits - 8 & 0xFF;
                node = node.getChild(c2);
                bits -= node.getBits();
                if (!node.isTerminal()) continue;
                if (node.getSymbol() == 256) {
                    throw new HPackException("EOS decoded");
                }
                out.append(node.getSymbol());
                node = this.root;
            }
        }
        while (bits > 0 && (node = node.getChild(c = current << 8 - bits & 0xFF)).isTerminal() && node.getBits() <= bits) {
            bits -= node.getBits();
            out.append(node.getSymbol());
            node = this.root;
        }
        int mask = (1 << bits) - 1;
        if ((current & mask) != mask) {
            throw new HPackException("Invalid padding");
        }
    }

    private static HuffmanNode buildTree(int[] codes, byte[] lengths) {
        HuffmanNode root = new HuffmanNode();
        for (int symbol = 0; symbol < codes.length; ++symbol) {
            int code = codes[symbol];
            int length = lengths[symbol];
            HuffmanNode current = root;
            while (length > 8) {
                if (current.isTerminal()) {
                    throw new IllegalStateException("Invalid Huffman code: prefix not unique");
                }
                int i = code >>> (length -= 8) & 0xFF;
                if (!current.hasChild(i)) {
                    current.setChild(i, new HuffmanNode());
                }
                current = current.getChild(i);
            }
            HuffmanNode terminal = new HuffmanNode(symbol, length);
            int shift = 8 - length;
            int start = code << shift & 0xFF;
            int end = 1 << shift;
            for (int i = start; i < start + end; ++i) {
                current.setChild(i, terminal);
            }
        }
        return root;
    }
}

