/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

public class PrefixTrie {
    private static final int SIZE = 128;
    Node root = new Node();

    public void put(String prefix, Object value) {
        Node current = this.root;
        for (int i = 0; i < prefix.length(); ++i) {
            char c = prefix.charAt(i);
            if (c > '\u0080') {
                throw new IllegalArgumentException("'" + c + "' is too big.");
            }
            if (current.next[c] == null) {
                current.next[c] = new Node();
            }
            current = current.next[c];
        }
        current.value = value;
    }

    public Object get(String key) {
        Node current = this.root;
        for (int i = 0; i < key.length(); ++i) {
            char c = key.charAt(i);
            if (c > '\u0080') {
                return null;
            }
            current = current.next[c];
            if (current == null) {
                return null;
            }
            if (current.value == null) continue;
            return current.value;
        }
        return null;
    }

    static class Node {
        Object value;
        Node[] next = new Node[128];

        Node() {
        }
    }
}

