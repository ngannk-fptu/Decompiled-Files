/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.IntsRef;
import com.atlassian.lucene36.util.fst.FST;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Util {
    private Util() {
    }

    public static <T> T get(FST<T> fst, IntsRef input) throws IOException {
        FST.Arc<T> arc = fst.getFirstArc(new FST.Arc());
        FST.BytesReader fstReader = fst.getBytesReader(0);
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
        FST.BytesReader fstReader = fst.getBytesReader(0);
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
        FST.BytesReader in = fst.getBytesReader(0);
        FST.Arc<Long> arc = fst.getFirstArc(new FST.Arc());
        FST.Arc<Long> scratchArc = new FST.Arc<Long>();
        IntsRef result = new IntsRef();
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
                    in.pos = arc.posArcsStart;
                    in.skip(arc.bytesPerArc * mid);
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

    public static <T> MinResult<T>[] shortestPaths(FST<T> fst, FST.Arc<T> fromNode, Comparator<T> comparator, int topN) throws IOException {
        return new TopNSearcher<T>(fst, fromNode, topN, comparator).search();
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
        seen.set(startArc.target);
        String stateShape = "circle";
        String finalStateShape = "doublecircle";
        out.write("digraph FST {\n");
        out.write("  rankdir = LR; splines=true; concentrate=true; ordering=out; ranksep=2.5; \n");
        if (!labelStates) {
            out.write("  node [shape=circle, width=.2, height=.2, style=filled]\n");
        }
        Util.emitDotState(out, "initial", "point", "white", "");
        Object NO_OUTPUT = fst.outputs.getNoOutput();
        String stateColor = fst.isExpandedTarget(startArc) ? "blue" : null;
        if (startArc.isFinal()) {
            isFinal = true;
            finalOutput = startArc.nextFinalOutput == NO_OUTPUT ? null : (Object)startArc.nextFinalOutput;
        } else {
            isFinal = false;
            finalOutput = null;
        }
        Util.emitDotState(out, Integer.toString(startArc.target), isFinal ? "doublecircle" : "circle", stateColor, finalOutput == null ? "" : fst.outputs.outputToString(finalOutput));
        out.write("  initial -> " + startArc.target + "\n");
        int level = 0;
        FST.BytesReader r = fst.getBytesReader(0);
        while (!nextLevelQueue.isEmpty()) {
            thisLevelQueue.addAll(nextLevelQueue);
            nextLevelQueue.clear();
            out.write("\n  // Transitions and states at level: " + ++level + "\n");
            block1: while (!thisLevelQueue.isEmpty()) {
                FST.Arc arc = (FST.Arc)thisLevelQueue.remove(thisLevelQueue.size() - 1);
                if (!FST.targetHasArcs(arc)) continue;
                int node = arc.target;
                fst.readFirstRealTargetArc(arc.target, arc, r);
                while (true) {
                    if (arc.target >= 0 && !seen.get(arc.target)) {
                        String stateColor2 = fst.isExpandedTarget(arc) ? "blue" : null;
                        String finalOutput2 = arc.nextFinalOutput != null && arc.nextFinalOutput != NO_OUTPUT ? fst.outputs.outputToString(arc.nextFinalOutput) : "";
                        Util.emitDotState(out, Integer.toString(arc.target), "circle", stateColor2, finalOutput2);
                        seen.set(arc.target);
                        nextLevelQueue.add(new FST.Arc().copyFrom(arc));
                        sameLevelStates.add(arc.target);
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
                Iterator i$ = sameLevelStates.iterator();
                while (i$.hasNext()) {
                    int state = (Integer)i$.next();
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
        out.write("  " + name + " [" + (shape != null ? "shape=" + shape : "") + " " + (color != null ? "color=" + color : "") + " " + (label != null ? "label=\"" + label + "\"" : "label=\"\"") + " " + "]\n");
    }

    private static String printableLabel(int label) {
        if (label >= 32 && label <= 125) {
            return Character.toString((char)label);
        }
        return "0x" + Integer.toHexString(label);
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
            scratch.ints[intIdx] = utf32 = Character.codePointAt(s, charIdx);
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
            scratch.bytes[i] = (byte)input.ints[i + input.offset];
        }
        scratch.length = input.length;
        return scratch;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class MinResult<T>
    implements Comparable<MinResult<T>> {
        public final IntsRef input;
        public final T output;
        final Comparator<T> comparator;

        public MinResult(IntsRef input, T output, Comparator<T> comparator) {
            this.input = input;
            this.output = output;
            this.comparator = comparator;
        }

        @Override
        public int compareTo(MinResult<T> other) {
            int cmp = this.comparator.compare(this.output, other.output);
            if (cmp == 0) {
                return this.input.compareTo(other.input);
            }
            return cmp;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class TopNSearcher<T> {
        private final FST<T> fst;
        private final FST.Arc<T> fromNode;
        private final int topN;
        final Comparator<T> comparator;
        FSTPath<T> bottom = null;
        TreeSet<FSTPath<T>> queue = null;

        public TopNSearcher(FST<T> fst, FST.Arc<T> fromNode, int topN, Comparator<T> comparator) {
            this.fst = fst;
            this.topN = topN;
            this.fromNode = fromNode;
            this.comparator = comparator;
        }

        private void addIfCompetitive(FSTPath<T> path) {
            assert (this.queue != null);
            Object cost = this.fst.outputs.add(path.cost, path.arc.output);
            if (this.bottom != null) {
                int comp = this.comparator.compare(cost, this.bottom.cost);
                if (comp > 0) {
                    return;
                }
                if (comp == 0) {
                    path.input.grow(path.input.length + 1);
                    path.input.ints[path.input.length++] = path.arc.label;
                    int cmp = this.bottom.input.compareTo(path.input);
                    --path.input.length;
                    assert (cmp != 0);
                    if (cmp < 0) {
                        return;
                    }
                }
            }
            FSTPath newPath = new FSTPath(cost, path.arc, this.comparator);
            newPath.input.grow(path.input.length + 1);
            System.arraycopy(path.input.ints, 0, newPath.input.ints, 0, path.input.length);
            newPath.input.ints[path.input.length] = path.arc.label;
            newPath.input.length = path.input.length + 1;
            this.queue.add(newPath);
            if (this.bottom != null) {
                assert (!this.queue.isEmpty());
                FSTPath<T> removed = this.queue.last();
                this.queue.remove(removed);
                assert (removed == this.bottom);
                this.bottom = this.queue.last();
            } else if (this.queue.size() == this.topN) {
                this.bottom = this.queue.last();
            }
        }

        public MinResult<T>[] search() throws IOException {
            FST.Arc scratchArc = new FST.Arc();
            ArrayList results = new ArrayList();
            Object NO_OUTPUT = this.fst.outputs.getNoOutput();
            block0: while (results.size() < this.topN) {
                FSTPath<T> path;
                if (this.queue == null) {
                    if (results.size() != 0) break;
                    if (this.topN > 1) {
                        this.queue = new TreeSet();
                    }
                    Object minArcCost = null;
                    FST.Arc minArc = null;
                    path = new FSTPath(NO_OUTPUT, this.fromNode, this.comparator);
                    this.fst.readFirstTargetArc(this.fromNode, path.arc);
                    while (true) {
                        Object arcScore = path.arc.output;
                        if (minArcCost == null || this.comparator.compare(arcScore, minArcCost) < 0) {
                            minArcCost = arcScore;
                            minArc = scratchArc.copyFrom(path.arc);
                        }
                        if (this.queue != null) {
                            this.addIfCompetitive(path);
                        }
                        if (path.arc.isLast()) break;
                        this.fst.readNextArc(path.arc);
                    }
                    assert (minArc != null);
                    if (this.queue != null) {
                        assert (!this.queue.isEmpty());
                        path = this.queue.first();
                        this.queue.remove(path);
                        assert (path.arc.label == minArc.label);
                        if (this.bottom != null && this.queue.size() == this.topN - 1) {
                            this.bottom = this.queue.last();
                        }
                    } else {
                        path.arc.copyFrom(minArc);
                        path.input.grow(1);
                        path.input.ints[0] = minArc.label;
                        path.input.length = 1;
                        path.cost = minArc.output;
                    }
                } else {
                    if (this.queue.isEmpty()) {
                        path = null;
                        break;
                    }
                    path = this.queue.first();
                    this.queue.remove(path);
                }
                if (path.arc.label == -1) {
                    --path.input.length;
                    results.add(new MinResult(path.input, path.cost, this.comparator));
                    continue;
                }
                if (results.size() == this.topN - 1) {
                    this.queue = null;
                }
                while (true) {
                    this.fst.readFirstTargetArc(path.arc, path.arc);
                    boolean foundZero = false;
                    while (true) {
                        if (this.comparator.compare(NO_OUTPUT, path.arc.output) == 0) {
                            if (this.queue == null) {
                                foundZero = true;
                                break;
                            }
                            if (!foundZero) {
                                scratchArc.copyFrom(path.arc);
                                foundZero = true;
                            } else {
                                this.addIfCompetitive(path);
                            }
                        } else if (this.queue != null) {
                            this.addIfCompetitive(path);
                        }
                        if (path.arc.isLast()) break;
                        this.fst.readNextArc(path.arc);
                    }
                    assert (foundZero);
                    if (this.queue != null) {
                        path.arc.copyFrom(scratchArc);
                    }
                    if (path.arc.label == -1) {
                        results.add(new MinResult(path.input, this.fst.outputs.add(path.cost, path.arc.output), this.comparator));
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
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class FSTPath<T>
    implements Comparable<FSTPath<T>> {
        public FST.Arc<T> arc;
        public T cost;
        public final IntsRef input = new IntsRef();
        final Comparator<T> comparator;

        public FSTPath(T cost, FST.Arc<T> arc, Comparator<T> comparator) {
            this.arc = new FST.Arc<T>().copyFrom(arc);
            this.cost = cost;
            this.comparator = comparator;
        }

        public String toString() {
            return "input=" + this.input + " cost=" + this.cost;
        }

        @Override
        public int compareTo(FSTPath<T> other) {
            int cmp = this.comparator.compare(this.cost, other.cost);
            if (cmp == 0) {
                return this.input.compareTo(other.input);
            }
            return cmp;
        }
    }
}

