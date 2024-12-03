/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.util.fst.Builder;
import com.atlassian.lucene36.util.fst.FST;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class NodeHash<T> {
    private int[] table;
    private int count;
    private int mask = 15;
    private final FST<T> fst;
    private final FST.Arc<T> scratchArc = new FST.Arc();

    public NodeHash(FST<T> fst) {
        this.table = new int[16];
        this.fst = fst;
    }

    private boolean nodesEqual(Builder.UnCompiledNode<T> node, int address, FST.BytesReader in) throws IOException {
        this.fst.readFirstRealTargetArc(address, this.scratchArc, in);
        if (this.scratchArc.bytesPerArc != 0 && node.numArcs != this.scratchArc.numArcs) {
            return false;
        }
        for (int arcUpto = 0; arcUpto < node.numArcs; ++arcUpto) {
            Builder.Arc arc = node.arcs[arcUpto];
            if (arc.label != this.scratchArc.label || !arc.output.equals(this.scratchArc.output) || ((Builder.CompiledNode)arc.target).node != this.scratchArc.target || !arc.nextFinalOutput.equals(this.scratchArc.nextFinalOutput) || arc.isFinal != this.scratchArc.isFinal()) {
                return false;
            }
            if (this.scratchArc.isLast()) {
                return arcUpto == node.numArcs - 1;
            }
            this.fst.readNextRealArc(this.scratchArc, in);
        }
        return false;
    }

    private int hash(Builder.UnCompiledNode<T> node) {
        int PRIME = 31;
        int h = 0;
        for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
            Builder.Arc arc = node.arcs[arcIdx];
            h = 31 * h + arc.label;
            h = 31 * h + ((Builder.CompiledNode)arc.target).node;
            h = 31 * h + arc.output.hashCode();
            h = 31 * h + arc.nextFinalOutput.hashCode();
            if (!arc.isFinal) continue;
            h += 17;
        }
        return h & Integer.MAX_VALUE;
    }

    private int hash(int node) throws IOException {
        int PRIME = 31;
        FST.BytesReader in = this.fst.getBytesReader(0);
        int h = 0;
        this.fst.readFirstRealTargetArc(node, this.scratchArc, in);
        while (true) {
            h = 31 * h + this.scratchArc.label;
            h = 31 * h + this.scratchArc.target;
            h = 31 * h + this.scratchArc.output.hashCode();
            h = 31 * h + this.scratchArc.nextFinalOutput.hashCode();
            if (this.scratchArc.isFinal()) {
                h += 17;
            }
            if (this.scratchArc.isLast()) break;
            this.fst.readNextRealArc(this.scratchArc, in);
        }
        return h & Integer.MAX_VALUE;
    }

    public int add(Builder.UnCompiledNode<T> nodeIn) throws IOException {
        FST.BytesReader in = this.fst.getBytesReader(0);
        int h = this.hash(nodeIn);
        int pos = h & this.mask;
        int c = 0;
        while (true) {
            int v;
            if ((v = this.table[pos]) == 0) {
                int node = this.fst.addNode(nodeIn);
                assert (this.hash(node) == h) : "frozenHash=" + this.hash(node) + " vs h=" + h;
                ++this.count;
                this.table[pos] = node;
                if (this.table.length < 2 * this.count) {
                    this.rehash();
                }
                return node;
            }
            if (this.nodesEqual(nodeIn, v, in)) {
                return v;
            }
            pos = pos + ++c & this.mask;
        }
    }

    private void addNew(int address) throws IOException {
        int pos = this.hash(address) & this.mask;
        int c = 0;
        while (true) {
            if (this.table[pos] == 0) break;
            pos = pos + ++c & this.mask;
        }
        this.table[pos] = address;
    }

    private void rehash() throws IOException {
        int[] oldTable = this.table;
        this.table = new int[2 * this.table.length];
        this.mask = this.table.length - 1;
        for (int idx = 0; idx < oldTable.length; ++idx) {
            int address = oldTable[idx];
            if (address == 0) continue;
            this.addNew(address);
        }
    }

    public int count() {
        return this.count;
    }
}

