/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.packed.PagedGrowableWriter;

final class NodeHash<T> {
    private PagedGrowableWriter table;
    private long count;
    private long mask = 15L;
    private final FST<T> fst;
    private final FST.Arc<T> scratchArc = new FST.Arc();
    private final FST.BytesReader in;

    public NodeHash(FST<T> fst, FST.BytesReader in) {
        this.table = new PagedGrowableWriter(16L, 0x40000000, 8, 0.0f);
        this.fst = fst;
        this.in = in;
    }

    private boolean nodesEqual(Builder.UnCompiledNode<T> node, long address) throws IOException {
        this.fst.readFirstRealTargetArc(address, this.scratchArc, this.in);
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
            this.fst.readNextRealArc(this.scratchArc, this.in);
        }
        return false;
    }

    private long hash(Builder.UnCompiledNode<T> node) {
        int PRIME = 31;
        long h = 0L;
        for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
            Builder.Arc arc = node.arcs[arcIdx];
            h = 31L * h + (long)arc.label;
            long n = ((Builder.CompiledNode)arc.target).node;
            h = 31L * h + (long)((int)(n ^ n >> 32));
            h = 31L * h + (long)arc.output.hashCode();
            h = 31L * h + (long)arc.nextFinalOutput.hashCode();
            if (!arc.isFinal) continue;
            h += 17L;
        }
        return h & Long.MAX_VALUE;
    }

    private long hash(long node) throws IOException {
        int PRIME = 31;
        long h = 0L;
        this.fst.readFirstRealTargetArc(node, this.scratchArc, this.in);
        while (true) {
            h = 31L * h + (long)this.scratchArc.label;
            h = 31L * h + (long)((int)(this.scratchArc.target ^ this.scratchArc.target >> 32));
            h = 31L * h + (long)this.scratchArc.output.hashCode();
            h = 31L * h + (long)this.scratchArc.nextFinalOutput.hashCode();
            if (this.scratchArc.isFinal()) {
                h += 17L;
            }
            if (this.scratchArc.isLast()) break;
            this.fst.readNextRealArc(this.scratchArc, this.in);
        }
        return h & Long.MAX_VALUE;
    }

    public long add(Builder.UnCompiledNode<T> nodeIn) throws IOException {
        long h = this.hash(nodeIn);
        long pos = h & this.mask;
        int c = 0;
        while (true) {
            long v;
            if ((v = this.table.get(pos)) == 0L) {
                long node = this.fst.addNode(nodeIn);
                assert (this.hash(node) == h) : "frozenHash=" + this.hash(node) + " vs h=" + h;
                ++this.count;
                this.table.set(pos, node);
                if (this.count > 2L * this.table.size() / 3L) {
                    this.rehash();
                }
                return node;
            }
            if (this.nodesEqual(nodeIn, v)) {
                return v;
            }
            pos = pos + (long)(++c) & this.mask;
        }
    }

    private void addNew(long address) throws IOException {
        long pos = this.hash(address) & this.mask;
        int c = 0;
        while (true) {
            if (this.table.get(pos) == 0L) break;
            pos = pos + (long)(++c) & this.mask;
        }
        this.table.set(pos, address);
    }

    private void rehash() throws IOException {
        PagedGrowableWriter oldTable = this.table;
        this.table = new PagedGrowableWriter(2L * oldTable.size(), 0x40000000, PackedInts.bitsRequired(this.count), 0.0f);
        this.mask = this.table.size() - 1L;
        for (long idx = 0L; idx < oldTable.size(); ++idx) {
            long address = oldTable.get(idx);
            if (address == 0L) continue;
            this.addNew(address);
        }
    }
}

