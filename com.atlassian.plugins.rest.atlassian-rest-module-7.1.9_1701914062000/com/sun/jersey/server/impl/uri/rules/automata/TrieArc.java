/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules.automata;

import com.sun.jersey.server.impl.uri.rules.automata.TrieNode;

public class TrieArc<T> {
    protected char[] code;
    protected TrieNode<T> target;
    protected TrieArc<T> next;

    public TrieArc(TrieNode<T> target, char code) {
        this.target = target;
        this.code = new char[]{code};
    }

    private void merge(TrieArc<T> arc) {
        int p = this.code.length;
        this.code = TrieArc.copyOf(this.code, this.code.length + arc.code.length);
        System.arraycopy(arc.code, 0, this.code, p, arc.code.length);
        this.target = arc.target;
        if (this.target.getArcs() == 1 && !this.target.hasValue() && !this.target.isWildcard()) {
            this.merge(this.target.getFirstArc());
        }
    }

    public void pack() {
        if (this.target.getArcs() == 1 && !this.target.hasValue() && !this.target.isWildcard()) {
            this.merge(this.target.getFirstArc());
        }
        this.target.pack();
    }

    public int length() {
        return this.code.length;
    }

    public int match(CharSequence seq, int i) {
        if (i + this.code.length > seq.length()) {
            return 0;
        }
        for (int j = 0; j < this.code.length; ++j) {
            if (this.code[j] == seq.charAt(i++)) continue;
            return 0;
        }
        return this.code.length;
    }

    public String toString() {
        if (this.target.hasValue()) {
            return "ARC(" + new String(this.code) + ") --> " + this.target.getPattern().getRegex();
        }
        return "ARC(" + new String(this.code) + ") --> null";
    }

    private static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
}

