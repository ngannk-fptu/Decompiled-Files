/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Asserts
 */
package org.apache.hc.core5.http2.hpack;

import java.util.Arrays;
import org.apache.hc.core5.util.Asserts;

final class HuffmanNode {
    private final int symbol;
    private final int bits;
    private final HuffmanNode[] children;

    HuffmanNode() {
        this.symbol = 0;
        this.bits = 8;
        this.children = new HuffmanNode[256];
    }

    HuffmanNode(int symbol, int bits) {
        this.symbol = symbol;
        this.bits = bits;
        this.children = null;
    }

    public int getBits() {
        return this.bits;
    }

    public int getSymbol() {
        return this.symbol;
    }

    public boolean hasChild(int index) {
        return this.children != null && this.children[index] != null;
    }

    public HuffmanNode getChild(int index) {
        return this.children != null ? this.children[index] : null;
    }

    void setChild(int index, HuffmanNode child) {
        Asserts.notNull((Object)this.children, (String)"Children nodes");
        this.children[index] = child;
    }

    public boolean isTerminal() {
        return this.children == null;
    }

    public String toString() {
        return "[symbol=" + this.symbol + ", bits=" + this.bits + ", children=" + Arrays.toString(this.children) + ']';
    }
}

