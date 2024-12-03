/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util.filetypedetector;

import java.util.HashMap;
import java.util.Map;

class ByteTrie<T> {
    private final ByteTrieNode<T> root = new ByteTrieNode();
    private int maxDepth;

    ByteTrie() {
    }

    public T find(byte[] bytes) {
        ByteTrieNode node = this.root;
        T val = node.getValue();
        for (byte b : bytes) {
            ByteTrieNode child = (ByteTrieNode)node.children.get(b);
            if (child == null) break;
            node = child;
            if (node.getValue() == null) continue;
            val = node.getValue();
        }
        return val;
    }

    public void addPath(T value, byte[] ... parts) {
        int depth = 0;
        ByteTrieNode<T> node = this.root;
        byte[][] byArray = parts;
        int n = byArray.length;
        for (int i = 0; i < n; ++i) {
            byte[] part;
            for (byte b : part = byArray[i]) {
                ByteTrieNode<T> child = (ByteTrieNode<T>)((ByteTrieNode)node).children.get(b);
                if (child == null) {
                    child = new ByteTrieNode<T>();
                    ((ByteTrieNode)node).children.put(b, child);
                }
                node = child;
                ++depth;
            }
        }
        node.setValue(value);
        this.maxDepth = Math.max(this.maxDepth, depth);
    }

    public void setDefaultValue(T defaultValue) {
        this.root.setValue(defaultValue);
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    static class ByteTrieNode<T> {
        private final Map<Byte, ByteTrieNode<T>> children = new HashMap<Byte, ByteTrieNode<T>>();
        private T value = null;

        ByteTrieNode() {
        }

        public void setValue(T value) {
            if (this.value != null) {
                throw new IllegalStateException("Value already set for this trie node");
            }
            this.value = value;
        }

        public T getValue() {
            return this.value;
        }
    }
}

