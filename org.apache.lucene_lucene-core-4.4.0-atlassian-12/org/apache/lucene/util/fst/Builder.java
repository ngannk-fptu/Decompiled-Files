/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.NodeHash;
import org.apache.lucene.util.fst.Outputs;

public class Builder<T> {
    private final NodeHash<T> dedupHash;
    private final FST<T> fst;
    private final T NO_OUTPUT;
    private final int minSuffixCount1;
    private final int minSuffixCount2;
    private final boolean doShareNonSingletonNodes;
    private final int shareMaxTailLength;
    private final IntsRef lastInput = new IntsRef();
    private final boolean doPackFST;
    private final float acceptableOverheadRatio;
    private UnCompiledNode<T>[] frontier;
    private final FreezeTail<T> freezeTail;

    public Builder(FST.INPUT_TYPE inputType, Outputs<T> outputs) {
        this(inputType, 0, 0, true, true, Integer.MAX_VALUE, outputs, null, false, 0.0f, true, 15);
    }

    public Builder(FST.INPUT_TYPE inputType, int minSuffixCount1, int minSuffixCount2, boolean doShareSuffix, boolean doShareNonSingletonNodes, int shareMaxTailLength, Outputs<T> outputs, FreezeTail<T> freezeTail, boolean doPackFST, float acceptableOverheadRatio, boolean allowArrayArcs, int bytesPageBits) {
        this.minSuffixCount1 = minSuffixCount1;
        this.minSuffixCount2 = minSuffixCount2;
        this.freezeTail = freezeTail;
        this.doShareNonSingletonNodes = doShareNonSingletonNodes;
        this.shareMaxTailLength = shareMaxTailLength;
        this.doPackFST = doPackFST;
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        this.fst = new FST<T>(inputType, outputs, doPackFST, acceptableOverheadRatio, allowArrayArcs, bytesPageBits);
        this.dedupHash = doShareSuffix ? new NodeHash<T>(this.fst, this.fst.bytes.getReverseReader(false)) : null;
        this.NO_OUTPUT = outputs.getNoOutput();
        UnCompiledNode[] f = new UnCompiledNode[10];
        this.frontier = f;
        for (int idx = 0; idx < this.frontier.length; ++idx) {
            this.frontier[idx] = new UnCompiledNode(this, idx);
        }
    }

    public long getTotStateCount() {
        return this.fst.nodeCount;
    }

    public long getTermCount() {
        return this.frontier[0].inputCount;
    }

    public long getMappedStateCount() {
        return this.dedupHash == null ? 0L : this.fst.nodeCount;
    }

    private CompiledNode compileNode(UnCompiledNode<T> nodeIn, int tailLength) throws IOException {
        long node = this.dedupHash != null && (this.doShareNonSingletonNodes || nodeIn.numArcs <= 1) && tailLength <= this.shareMaxTailLength ? (nodeIn.numArcs == 0 ? this.fst.addNode(nodeIn) : this.dedupHash.add(nodeIn)) : this.fst.addNode(nodeIn);
        assert (node != -2L);
        nodeIn.clear();
        CompiledNode fn = new CompiledNode();
        fn.node = node;
        return fn;
    }

    private void freezeTail(int prefixLenPlus1) throws IOException {
        if (this.freezeTail != null) {
            this.freezeTail.freeze(this.frontier, prefixLenPlus1, this.lastInput);
        } else {
            int downTo = Math.max(1, prefixLenPlus1);
            for (int idx = this.lastInput.length; idx >= downTo; --idx) {
                boolean isFinal;
                boolean doPrune = false;
                boolean doCompile = false;
                UnCompiledNode<T> node = this.frontier[idx];
                UnCompiledNode parent = this.frontier[idx - 1];
                if (node.inputCount < (long)this.minSuffixCount1) {
                    doPrune = true;
                    doCompile = true;
                } else if (idx > prefixLenPlus1) {
                    doPrune = parent.inputCount < (long)this.minSuffixCount2 || this.minSuffixCount2 == 1 && parent.inputCount == 1L && idx > 1;
                    doCompile = true;
                } else {
                    boolean bl = doCompile = this.minSuffixCount2 == 0;
                }
                if (node.inputCount < (long)this.minSuffixCount2 || this.minSuffixCount2 == 1 && node.inputCount == 1L && idx > 1) {
                    for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
                        UnCompiledNode target = (UnCompiledNode)node.arcs[arcIdx].target;
                        target.clear();
                    }
                    node.numArcs = 0;
                }
                if (doPrune) {
                    node.clear();
                    parent.deleteLast(this.lastInput.ints[this.lastInput.offset + idx - 1], node);
                    continue;
                }
                if (this.minSuffixCount2 != 0) {
                    this.compileAllTargets(node, this.lastInput.length - idx);
                }
                Object nextFinalOutput = node.output;
                boolean bl = isFinal = node.isFinal || node.numArcs == 0;
                if (doCompile) {
                    parent.replaceLast(this.lastInput.ints[this.lastInput.offset + idx - 1], this.compileNode(node, 1 + this.lastInput.length - idx), nextFinalOutput, isFinal);
                    continue;
                }
                parent.replaceLast(this.lastInput.ints[this.lastInput.offset + idx - 1], node, nextFinalOutput, isFinal);
                this.frontier[idx] = new UnCompiledNode(this, idx);
            }
        }
    }

    public void add(IntsRef input, T output) throws IOException {
        int idx;
        if (output.equals(this.NO_OUTPUT)) {
            output = this.NO_OUTPUT;
        }
        assert (this.lastInput.length == 0 || input.compareTo(this.lastInput) >= 0) : "inputs are added out of order lastInput=" + this.lastInput + " vs input=" + input;
        assert (this.validOutput(output));
        if (input.length == 0) {
            ++this.frontier[0].inputCount;
            this.frontier[0].isFinal = true;
            this.fst.setEmptyOutput(output);
            return;
        }
        int pos1 = 0;
        int pos2 = input.offset;
        int pos1Stop = Math.min(this.lastInput.length, input.length);
        while (true) {
            ++this.frontier[pos1].inputCount;
            if (pos1 >= pos1Stop || this.lastInput.ints[pos1] != input.ints[pos2]) break;
            ++pos1;
            ++pos2;
        }
        int prefixLenPlus1 = pos1 + 1;
        if (this.frontier.length < input.length + 1) {
            UnCompiledNode[] next = new UnCompiledNode[ArrayUtil.oversize(input.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.frontier, 0, next, 0, this.frontier.length);
            for (idx = this.frontier.length; idx < next.length; ++idx) {
                next[idx] = new UnCompiledNode(this, idx);
            }
            this.frontier = next;
        }
        this.freezeTail(prefixLenPlus1);
        for (int idx2 = prefixLenPlus1; idx2 <= input.length; ++idx2) {
            this.frontier[idx2 - 1].addArc(input.ints[input.offset + idx2 - 1], this.frontier[idx2]);
            ++this.frontier[idx2].inputCount;
        }
        UnCompiledNode<T> lastNode = this.frontier[input.length];
        if (this.lastInput.length != input.length || prefixLenPlus1 != input.length + 1) {
            lastNode.isFinal = true;
            lastNode.output = this.NO_OUTPUT;
        }
        for (idx = 1; idx < prefixLenPlus1; ++idx) {
            T wordSuffix;
            T commonOutputPrefix;
            UnCompiledNode<T> node = this.frontier[idx];
            UnCompiledNode<T> parentNode = this.frontier[idx - 1];
            T lastOutput = parentNode.getLastOutput(input.ints[input.offset + idx - 1]);
            assert (this.validOutput(lastOutput));
            if (lastOutput != this.NO_OUTPUT) {
                commonOutputPrefix = this.fst.outputs.common(output, lastOutput);
                assert (this.validOutput(commonOutputPrefix));
                wordSuffix = this.fst.outputs.subtract(lastOutput, commonOutputPrefix);
                assert (this.validOutput(wordSuffix));
                parentNode.setLastOutput(input.ints[input.offset + idx - 1], commonOutputPrefix);
                node.prependOutput(wordSuffix);
            } else {
                commonOutputPrefix = wordSuffix = this.NO_OUTPUT;
            }
            output = this.fst.outputs.subtract(output, commonOutputPrefix);
            assert (this.validOutput(output));
        }
        if (this.lastInput.length == input.length && prefixLenPlus1 == 1 + input.length) {
            lastNode.output = this.fst.outputs.merge(lastNode.output, output);
        } else {
            this.frontier[prefixLenPlus1 - 1].setLastOutput(input.ints[input.offset + prefixLenPlus1 - 1], output);
        }
        this.lastInput.copyInts(input);
    }

    private boolean validOutput(T output) {
        return output == this.NO_OUTPUT || !output.equals(this.NO_OUTPUT);
    }

    public FST<T> finish() throws IOException {
        UnCompiledNode<T> root = this.frontier[0];
        this.freezeTail(0);
        if (root.inputCount < (long)this.minSuffixCount1 || root.inputCount < (long)this.minSuffixCount2 || root.numArcs == 0) {
            if (this.fst.emptyOutput == null) {
                return null;
            }
            if (this.minSuffixCount1 > 0 || this.minSuffixCount2 > 0) {
                return null;
            }
        } else if (this.minSuffixCount2 != 0) {
            this.compileAllTargets(root, this.lastInput.length);
        }
        this.fst.finish(this.compileNode(root, (int)this.lastInput.length).node);
        if (this.doPackFST) {
            return this.fst.pack(3, Math.max(10, (int)(this.fst.getNodeCount() / 4L)), this.acceptableOverheadRatio);
        }
        return this.fst;
    }

    private void compileAllTargets(UnCompiledNode<T> node, int tailLength) throws IOException {
        for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
            Arc arc = node.arcs[arcIdx];
            if (arc.target.isCompiled()) continue;
            UnCompiledNode n = (UnCompiledNode)arc.target;
            if (n.numArcs == 0) {
                n.isFinal = true;
                arc.isFinal = true;
            }
            arc.target = this.compileNode(n, tailLength - 1);
        }
    }

    public long fstSizeInBytes() {
        return this.fst.sizeInBytes();
    }

    public static final class UnCompiledNode<T>
    implements Node {
        final Builder<T> owner;
        public int numArcs;
        public Arc<T>[] arcs;
        public T output;
        public boolean isFinal;
        public long inputCount;
        public final int depth;

        public UnCompiledNode(Builder<T> owner, int depth) {
            this.owner = owner;
            this.arcs = new Arc[1];
            this.arcs[0] = new Arc();
            this.output = ((Builder)owner).NO_OUTPUT;
            this.depth = depth;
        }

        @Override
        public boolean isCompiled() {
            return false;
        }

        public void clear() {
            this.numArcs = 0;
            this.isFinal = false;
            this.output = ((Builder)this.owner).NO_OUTPUT;
            this.inputCount = 0L;
        }

        public T getLastOutput(int labelToMatch) {
            assert (this.numArcs > 0);
            assert (this.arcs[this.numArcs - 1].label == labelToMatch);
            return this.arcs[this.numArcs - 1].output;
        }

        public void addArc(int label, Node target) {
            assert (label >= 0);
            assert (this.numArcs == 0 || label > this.arcs[this.numArcs - 1].label) : "arc[-1].label=" + this.arcs[this.numArcs - 1].label + " new label=" + label + " numArcs=" + this.numArcs;
            if (this.numArcs == this.arcs.length) {
                Arc[] newArcs = new Arc[ArrayUtil.oversize(this.numArcs + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.arcs, 0, newArcs, 0, this.arcs.length);
                for (int arcIdx = this.numArcs; arcIdx < newArcs.length; ++arcIdx) {
                    newArcs[arcIdx] = new Arc();
                }
                this.arcs = newArcs;
            }
            Arc<T> arc = this.arcs[this.numArcs++];
            arc.label = label;
            arc.target = target;
            arc.nextFinalOutput = ((Builder)this.owner).NO_OUTPUT;
            arc.output = arc.nextFinalOutput;
            arc.isFinal = false;
        }

        public void replaceLast(int labelToMatch, Node target, T nextFinalOutput, boolean isFinal) {
            assert (this.numArcs > 0);
            Arc<T> arc = this.arcs[this.numArcs - 1];
            assert (arc.label == labelToMatch) : "arc.label=" + arc.label + " vs " + labelToMatch;
            arc.target = target;
            arc.nextFinalOutput = nextFinalOutput;
            arc.isFinal = isFinal;
        }

        public void deleteLast(int label, Node target) {
            assert (this.numArcs > 0);
            assert (label == this.arcs[this.numArcs - 1].label);
            assert (target == this.arcs[this.numArcs - 1].target);
            --this.numArcs;
        }

        public void setLastOutput(int labelToMatch, T newOutput) {
            assert (((Builder)this.owner).validOutput(newOutput));
            assert (this.numArcs > 0);
            Arc<T> arc = this.arcs[this.numArcs - 1];
            assert (arc.label == labelToMatch);
            arc.output = newOutput;
        }

        public void prependOutput(T outputPrefix) {
            assert (((Builder)this.owner).validOutput(outputPrefix));
            for (int arcIdx = 0; arcIdx < this.numArcs; ++arcIdx) {
                this.arcs[arcIdx].output = ((Builder)this.owner).fst.outputs.add(outputPrefix, this.arcs[arcIdx].output);
                assert (((Builder)this.owner).validOutput(this.arcs[arcIdx].output));
            }
            if (this.isFinal) {
                this.output = ((Builder)this.owner).fst.outputs.add(outputPrefix, this.output);
                assert (((Builder)this.owner).validOutput(this.output));
            }
        }
    }

    static final class CompiledNode
    implements Node {
        long node;

        CompiledNode() {
        }

        @Override
        public boolean isCompiled() {
            return true;
        }
    }

    static interface Node {
        public boolean isCompiled();
    }

    public static class Arc<T> {
        public int label;
        public Node target;
        public boolean isFinal;
        public T output;
        public T nextFinalOutput;
    }

    public static abstract class FreezeTail<T> {
        public abstract void freeze(UnCompiledNode<T>[] var1, int var2, IntsRef var3) throws IOException;
    }
}

