/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.FST;

public final class Util {
    private Util() {
    }

    public static <T> T get(FST<T> fst, IntsRef input) throws IOException {
        FST.Arc<T> arc = fst.getFirstArc(new FST.Arc());
        FST.BytesReader fstReader = fst.getBytesReader();
        Object output = fst.outputs.getNoOutput();
        for (int i = 0; i < input.length; ++i) {
            if (fst.findTargetArc(input.ints[input.offset + i], arc, arc, fstReader) == null) {
                return null;
            }
            output = fst.outputs.add(output, arc.output);
        }
        if (arc.isFinal()) {
            return fst.outputs.add(output, arc.nextFinalOutput);
        }
        return null;
    }

    public static <T> T get(FST<T> fst, BytesRef input) throws IOException {
        assert (fst.inputType == FST.INPUT_TYPE.BYTE1);
        FST.BytesReader fstReader = fst.getBytesReader();
        FST.Arc<T> arc = fst.getFirstArc(new FST.Arc());
        Object output = fst.outputs.getNoOutput();
        for (int i = 0; i < input.length; ++i) {
            if (fst.findTargetArc(input.bytes[i + input.offset] & 0xFF, arc, arc, fstReader) == null) {
                return null;
            }
            output = fst.outputs.add(output, arc.output);
        }
        if (arc.isFinal()) {
            return fst.outputs.add(output, arc.nextFinalOutput);
        }
        return null;
    }

    public static IntsRef getByOutput(FST<Long> fst, long targetOutput) throws IOException {
        FST.BytesReader in = fst.getBytesReader();
        FST.Arc<Long> arc = fst.getFirstArc(new FST.Arc());
        FST.Arc<Long> scratchArc = new FST.Arc<Long>();
        IntsRef result = new IntsRef();
        return Util.getByOutput(fst, targetOutput, in, arc, scratchArc, result);
    }

    public static IntsRef getByOutput(FST<Long> fst, long targetOutput, FST.BytesReader in, FST.Arc<Long> arc, FST.Arc<Long> scratchArc, IntsRef result) throws IOException {
        long output = (Long)arc.output;
        int upto = 0;
        block0: while (true) {
            if (arc.isFinal()) {
                long finalOutput = output + (Long)arc.nextFinalOutput;
                if (finalOutput == targetOutput) {
                    result.length = upto;
                    return result;
                }
                if (finalOutput > targetOutput) {
                    return null;
                }
            }
            if (!FST.targetHasArcs(arc)) break;
            if (result.ints.length == upto) {
                result.grow(1 + upto);
            }
            fst.readFirstRealTargetArc(arc.target, arc, in);
            if (arc.bytesPerArc != 0) {
                int low = 0;
                int high = arc.numArcs - 1;
                int mid = 0;
                boolean exact = false;
                while (low <= high) {
                    long minArcOutput;
                    mid = low + high >>> 1;
                    in.setPosition(arc.posArcsStart);
                    in.skipBytes(arc.bytesPerArc * mid);
                    byte flags = in.readByte();
                    fst.readLabel(in);
                    if ((flags & 0x10) != 0) {
                        long arcOutput = (Long)fst.outputs.read(in);
                        minArcOutput = output + arcOutput;
                    } else {
                        minArcOutput = output;
                    }
                    if (minArcOutput == targetOutput) {
                        exact = true;
                        break;
                    }
                    if (minArcOutput < targetOutput) {
                        low = mid + 1;
                        continue;
                    }
                    high = mid - 1;
                }
                if (high == -1) {
                    return null;
                }
                arc.arcIdx = exact ? mid - 1 : low - 2;
                fst.readNextRealArc(arc, in);
                result.ints[upto++] = arc.label;
                output += ((Long)arc.output).longValue();
                continue;
            }
            FST.Arc<Long> prevArc = null;
            while (true) {
                long minArcOutput;
                if ((minArcOutput = output + (Long)arc.output) == targetOutput) {
                    output = minArcOutput;
                    result.ints[upto++] = arc.label;
                    continue block0;
                }
                if (minArcOutput > targetOutput) {
                    if (prevArc == null) {
                        return null;
                    }
                    arc.copyFrom(prevArc);
                    result.ints[upto++] = arc.label;
                    output += ((Long)arc.output).longValue();
                    continue block0;
                }
                if (arc.isLast()) {
                    output = minArcOutput;
                    result.ints[upto++] = arc.label;
                    continue block0;
                }
                prevArc = scratchArc;
                prevArc.copyFrom(arc);
                fst.readNextRealArc(arc, in);
            }
            break;
        }
        return null;
    }

    public static <T> MinResult<T>[] shortestPaths(FST<T> fst, FST.Arc<T> fromNode, T startOutput, Comparator<T> comparator, int topN, boolean allowEmptyString) throws IOException {
        TopNSearcher<T> searcher = new TopNSearcher<T>(fst, topN, topN, comparator);
        searcher.addStartPaths(fromNode, startOutput, allowEmptyString, new IntsRef());
        return searcher.search();
    }

    public static <T> void toDot(FST<T> fst, Writer out, boolean sameRank, boolean labelStates) throws IOException {
        Object finalOutput;
        boolean isFinal;
        String expandedNodeColor = "blue";
        FST.Arc<T> startArc = fst.getFirstArc(new FST.Arc());
        ArrayList thisLevelQueue = new ArrayList();
        ArrayList nextLevelQueue = new ArrayList();
        nextLevelQueue.add(startArc);
        ArrayList<Integer> sameLevelStates = new ArrayList<Integer>();
        BitSet seen = new BitSet();
        seen.set((int)startArc.target);
        String stateShape = "circle";
        String finalStateShape = "doublecircle";
        out.write("digraph FST {\n");
        out.write("  rankdir = LR; splines=true; concentrate=true; ordering=out; ranksep=2.5; \n");
        if (!labelStates) {
            out.write("  node [shape=circle, width=.2, height=.2, style=filled]\n");
        }
        Util.emitDotState(out, "initial", "point", "white", "");
        Object NO_OUTPUT = fst.outputs.getNoOutput();
        FST.BytesReader r = fst.getBytesReader();
        String stateColor = fst.isExpandedTarget(startArc, r) ? "blue" : null;
        if (startArc.isFinal()) {
            isFinal = true;
            finalOutput = startArc.nextFinalOutput == NO_OUTPUT ? null : (Object)startArc.nextFinalOutput;
        } else {
            isFinal = false;
            finalOutput = null;
        }
        Util.emitDotState(out, Long.toString(startArc.target), isFinal ? "doublecircle" : "circle", stateColor, finalOutput == null ? "" : fst.outputs.outputToString(finalOutput));
        out.write("  initial -> " + startArc.target + "\n");
        int level = 0;
        while (!nextLevelQueue.isEmpty()) {
            thisLevelQueue.addAll(nextLevelQueue);
            nextLevelQueue.clear();
            out.write("\n  // Transitions and states at level: " + ++level + "\n");
            block1: while (!thisLevelQueue.isEmpty()) {
                FST.Arc arc = (FST.Arc)thisLevelQueue.remove(thisLevelQueue.size() - 1);
                if (!FST.targetHasArcs(arc)) continue;
                long node = arc.target;
                fst.readFirstRealTargetArc(arc.target, arc, r);
                while (true) {
                    if (arc.target >= 0L && !seen.get((int)arc.target)) {
                        String stateColor2 = fst.isExpandedTarget(arc, r) ? "blue" : null;
                        String finalOutput2 = arc.nextFinalOutput != null && arc.nextFinalOutput != NO_OUTPUT ? fst.outputs.outputToString(arc.nextFinalOutput) : "";
                        Util.emitDotState(out, Long.toString(arc.target), "circle", stateColor2, finalOutput2);
                        seen.set((int)arc.target);
                        nextLevelQueue.add(new FST.Arc().copyFrom(arc));
                        sameLevelStates.add((int)arc.target);
                    }
                    String outs = arc.output != NO_OUTPUT ? "/" + fst.outputs.outputToString(arc.output) : "";
                    if (!FST.targetHasArcs(arc) && arc.isFinal() && arc.nextFinalOutput != NO_OUTPUT) {
                        outs = outs + "/[" + fst.outputs.outputToString(arc.nextFinalOutput) + "]";
                    }
                    String arcColor = arc.flag(4) ? "red" : "black";
                    assert (arc.label != -1);
                    out.write("  " + node + " -> " + arc.target + " [label=\"" + Util.printableLabel(arc.label) + outs + "\"" + (arc.isFinal() ? " style=\"bold\"" : "") + " color=\"" + arcColor + "\"]\n");
                    if (arc.isLast()) continue block1;
                    fst.readNextRealArc(arc, r);
                }
            }
            if (sameRank && sameLevelStates.size() > 1) {
                out.write("  {rank=same; ");
                Iterator iterator = sameLevelStates.iterator();
                while (iterator.hasNext()) {
                    int state = (Integer)iterator.next();
                    out.write(state + "; ");
                }
                out.write(" }\n");
            }
            sameLevelStates.clear();
        }
        out.write("  -1 [style=filled, color=black, shape=doublecircle, label=\"\"]\n\n");
        out.write("  {rank=sink; -1 }\n");
        out.write("}\n");
        out.flush();
    }

    private static void emitDotState(Writer out, String name, String shape, String color, String label) throws IOException {
        out.write("  " + name + " [" + (shape != null ? "shape=" + shape : "") + " " + (color != null ? "color=" + color : "") + " " + (label != null ? "label=\"" + label + "\"" : "label=\"\"") + " ]\n");
    }

    private static String printableLabel(int label) {
        if (label >= 32 && label <= 125) {
            return Character.toString((char)label);
        }
        return "0x" + Integer.toHexString(label);
    }

    public static IntsRef toUTF16(CharSequence s, IntsRef scratch) {
        int charLimit = s.length();
        scratch.offset = 0;
        scratch.length = charLimit;
        scratch.grow(charLimit);
        for (int idx = 0; idx < charLimit; ++idx) {
            scratch.ints[idx] = s.charAt(idx);
        }
        return scratch;
    }

    public static IntsRef toUTF32(CharSequence s, IntsRef scratch) {
        int charIdx = 0;
        int intIdx = 0;
        int charLimit = s.length();
        while (charIdx < charLimit) {
            int utf32;
            scratch.grow(intIdx + 1);
            scratch.ints[intIdx] = utf32 = Character.codePointAt(s, charIdx);
            charIdx += Character.charCount(utf32);
            ++intIdx;
        }
        scratch.length = intIdx;
        return scratch;
    }

    public static IntsRef toUTF32(char[] s, int offset, int length, IntsRef scratch) {
        int charIdx = offset;
        int intIdx = 0;
        int charLimit = offset + length;
        while (charIdx < charLimit) {
            int utf32;
            scratch.grow(intIdx + 1);
            scratch.ints[intIdx] = utf32 = Character.codePointAt(s, charIdx, charLimit);
            charIdx += Character.charCount(utf32);
            ++intIdx;
        }
        scratch.length = intIdx;
        return scratch;
    }

    public static IntsRef toIntsRef(BytesRef input, IntsRef scratch) {
        scratch.grow(input.length);
        for (int i = 0; i < input.length; ++i) {
            scratch.ints[i] = input.bytes[i + input.offset] & 0xFF;
        }
        scratch.length = input.length;
        return scratch;
    }

    public static BytesRef toBytesRef(IntsRef input, BytesRef scratch) {
        scratch.grow(input.length);
        for (int i = 0; i < input.length; ++i) {
            int value = input.ints[i + input.offset];
            assert (value >= -128 && value <= 255) : "value " + value + " doesn't fit into byte";
            scratch.bytes[i] = (byte)value;
        }
        scratch.length = input.length;
        return scratch;
    }

    public static <T> FST.Arc<T> readCeilArc(int label, FST<T> fst, FST.Arc<T> follow, FST.Arc<T> arc, FST.BytesReader in) throws IOException {
        if (label == -1) {
            if (follow.isFinal()) {
                if (follow.target <= 0L) {
                    arc.flags = (byte)2;
                } else {
                    arc.flags = 0;
                    arc.nextArc = follow.target;
                    arc.node = follow.target;
                }
                arc.output = follow.nextFinalOutput;
                arc.label = -1;
                return arc;
            }
            return null;
        }
        if (!FST.targetHasArcs(follow)) {
            return null;
        }
        fst.readFirstTargetArc(follow, arc, in);
        if (arc.bytesPerArc != 0 && arc.label != -1) {
            int low = arc.arcIdx;
            int high = arc.numArcs - 1;
            int mid = 0;
            while (low <= high) {
                mid = low + high >>> 1;
                in.setPosition(arc.posArcsStart);
                in.skipBytes(arc.bytesPerArc * mid + 1);
                int midLabel = fst.readLabel(in);
                int cmp = midLabel - label;
                if (cmp < 0) {
                    low = mid + 1;
                    continue;
                }
                if (cmp > 0) {
                    high = mid - 1;
                    continue;
                }
                arc.arcIdx = mid - 1;
                return fst.readNextRealArc(arc, in);
            }
            if (low == arc.numArcs) {
                return null;
            }
            arc.arcIdx = low > high ? high : low;
            return fst.readNextRealArc(arc, in);
        }
        fst.readFirstRealTargetArc(follow.target, arc, in);
        while (arc.label < label) {
            if (arc.isLast()) {
                return null;
            }
            fst.readNextRealArc(arc, in);
        }
        return arc;
    }

    public static final class MinResult<T> {
        public final IntsRef input;
        public final T output;

        public MinResult(IntsRef input, T output) {
            this.input = input;
            this.output = output;
        }
    }

    public static class TopNSearcher<T> {
        private final FST<T> fst;
        private final FST.BytesReader bytesReader;
        private final int topN;
        private final int maxQueueDepth;
        private final FST.Arc<T> scratchArc = new FST.Arc();
        final Comparator<T> comparator;
        TreeSet<FSTPath<T>> queue = null;

        public TopNSearcher(FST<T> fst, int topN, int maxQueueDepth, Comparator<T> comparator) {
            this.fst = fst;
            this.bytesReader = fst.getBytesReader();
            this.topN = topN;
            this.maxQueueDepth = maxQueueDepth;
            this.comparator = comparator;
            this.queue = new TreeSet<T>(new TieBreakByInputComparator<T>(comparator));
        }

        private void addIfCompetitive(FSTPath<T> path) {
            assert (this.queue != null);
            Object cost = this.fst.outputs.add(path.cost, path.arc.output);
            if (this.queue.size() == this.maxQueueDepth) {
                FSTPath<T> bottom = this.queue.last();
                int comp = this.comparator.compare(cost, bottom.cost);
                if (comp > 0) {
                    return;
                }
                if (comp == 0) {
                    path.input.grow(path.input.length + 1);
                    path.input.ints[path.input.length++] = path.arc.label;
                    int cmp = bottom.input.compareTo(path.input);
                    --path.input.length;
                    assert (cmp != 0);
                    if (cmp < 0) {
                        return;
                    }
                }
            }
            IntsRef newInput = new IntsRef(path.input.length + 1);
            System.arraycopy(path.input.ints, 0, newInput.ints, 0, path.input.length);
            newInput.ints[path.input.length] = path.arc.label;
            newInput.length = path.input.length + 1;
            FSTPath newPath = new FSTPath(cost, path.arc, newInput);
            this.queue.add(newPath);
            if (this.queue.size() == this.maxQueueDepth + 1) {
                this.queue.pollLast();
            }
        }

        public void addStartPaths(FST.Arc<T> node, T startOutput, boolean allowEmptyString, IntsRef input) throws IOException {
            if (startOutput.equals(this.fst.outputs.getNoOutput())) {
                startOutput = this.fst.outputs.getNoOutput();
            }
            FSTPath<T> path = new FSTPath<T>(startOutput, node, input);
            this.fst.readFirstTargetArc(node, path.arc, this.bytesReader);
            while (true) {
                if (allowEmptyString || path.arc.label != -1) {
                    this.addIfCompetitive(path);
                }
                if (path.arc.isLast()) break;
                this.fst.readNextArc(path.arc, this.bytesReader);
            }
        }

        public MinResult<T>[] search() throws IOException {
            FSTPath<T> path;
            ArrayList results = new ArrayList();
            FST.BytesReader fstReader = this.fst.getBytesReader();
            Object NO_OUTPUT = this.fst.outputs.getNoOutput();
            int rejectCount = 0;
            block0: while (results.size() < this.topN && this.queue != null && (path = this.queue.pollFirst()) != null) {
                if (path.arc.label == -1) {
                    --path.input.length;
                    results.add(new MinResult(path.input, path.cost));
                    continue;
                }
                if (results.size() == this.topN - 1 && this.maxQueueDepth == this.topN) {
                    this.queue = null;
                }
                while (true) {
                    this.fst.readFirstTargetArc(path.arc, path.arc, fstReader);
                    boolean foundZero = false;
                    while (true) {
                        if (this.comparator.compare(NO_OUTPUT, path.arc.output) == 0) {
                            if (this.queue == null) {
                                foundZero = true;
                                break;
                            }
                            if (!foundZero) {
                                this.scratchArc.copyFrom(path.arc);
                                foundZero = true;
                            } else {
                                this.addIfCompetitive(path);
                            }
                        } else if (this.queue != null) {
                            this.addIfCompetitive(path);
                        }
                        if (path.arc.isLast()) break;
                        this.fst.readNextArc(path.arc, fstReader);
                    }
                    assert (foundZero);
                    if (this.queue != null) {
                        path.arc.copyFrom(this.scratchArc);
                    }
                    if (path.arc.label == -1) {
                        Object finalOutput = this.fst.outputs.add(path.cost, path.arc.output);
                        if (this.acceptResult(path.input, finalOutput)) {
                            results.add(new MinResult(path.input, finalOutput));
                            continue block0;
                        }
                        assert (++rejectCount + this.topN <= this.maxQueueDepth) : "maxQueueDepth (" + this.maxQueueDepth + ") is too small for topN (" + this.topN + "): rejected " + rejectCount + " paths";
                        continue block0;
                    }
                    path.input.grow(1 + path.input.length);
                    path.input.ints[path.input.length] = path.arc.label;
                    ++path.input.length;
                    path.cost = this.fst.outputs.add(path.cost, path.arc.output);
                }
            }
            MinResult[] arr = new MinResult[results.size()];
            return results.toArray(arr);
        }

        protected boolean acceptResult(IntsRef input, T output) {
            return true;
        }
    }

    private static class TieBreakByInputComparator<T>
    implements Comparator<FSTPath<T>> {
        private final Comparator<T> comparator;

        public TieBreakByInputComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(FSTPath<T> a, FSTPath<T> b) {
            int cmp = this.comparator.compare(a.cost, b.cost);
            if (cmp == 0) {
                return a.input.compareTo(b.input);
            }
            return cmp;
        }
    }

    private static class FSTPath<T> {
        public FST.Arc<T> arc;
        public T cost;
        public final IntsRef input;

        public FSTPath(T cost, FST.Arc<T> arc, IntsRef input) {
            this.arc = new FST.Arc<T>().copyFrom(arc);
            this.cost = cost;
            this.input = input;
        }

        public String toString() {
            return "input=" + this.input + " cost=" + this.cost;
        }
    }
}

