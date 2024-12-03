/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.itu_t4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.common.itu_t4.BitInputStreamFlexible;
import org.apache.commons.imaging.common.itu_t4.HuffmanTreeException;

class HuffmanTree<T> {
    private final List<Node<T>> nodes = new ArrayList<Node<T>>();

    HuffmanTree() {
    }

    public final void insert(String pattern, T value) throws HuffmanTreeException {
        int position = 0;
        Node<T> node = this.growAndGetNode(position);
        if (node.value != null) {
            throw new HuffmanTreeException("Can't add child to a leaf");
        }
        for (int patternPosition = 0; patternPosition < pattern.length(); ++patternPosition) {
            char nextChar = pattern.charAt(patternPosition);
            position = nextChar == '0' ? (position << 1) + 1 : position + 1 << 1;
            node = this.growAndGetNode(position);
            if (node.value == null) continue;
            throw new HuffmanTreeException("Can't add child to a leaf");
        }
        node.value = value;
    }

    private Node<T> growAndGetNode(int position) {
        while (position >= this.nodes.size()) {
            this.nodes.add(new Node());
        }
        Node<T> node = this.nodes.get(position);
        node.empty = false;
        return node;
    }

    public final T decode(BitInputStreamFlexible bitStream) throws HuffmanTreeException {
        int position = 0;
        Node<T> node = this.nodes.get(0);
        while (node.value == null) {
            int nextBit;
            try {
                nextBit = bitStream.readBits(1);
            }
            catch (IOException ioEx) {
                throw new HuffmanTreeException("Error reading stream for huffman tree", ioEx);
            }
            position = nextBit == 0 ? (position << 1) + 1 : position + 1 << 1;
            if (position >= this.nodes.size()) {
                throw new HuffmanTreeException("Invalid bit pattern");
            }
            node = this.nodes.get(position);
            if (!node.empty) continue;
            throw new HuffmanTreeException("Invalid bit pattern");
        }
        return node.value;
    }

    private static final class Node<T> {
        boolean empty = true;
        T value;

        private Node() {
        }
    }
}

