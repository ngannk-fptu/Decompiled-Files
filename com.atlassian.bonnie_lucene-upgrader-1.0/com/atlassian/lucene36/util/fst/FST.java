/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.store.InputStreamDataInput;
import com.atlassian.lucene36.store.OutputStreamDataOutput;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.CodecUtil;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.IntsRef;
import com.atlassian.lucene36.util.PriorityQueue;
import com.atlassian.lucene36.util.fst.Builder;
import com.atlassian.lucene36.util.fst.Outputs;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FST<T> {
    public final INPUT_TYPE inputType;
    static final int BIT_FINAL_ARC = 1;
    static final int BIT_LAST_ARC = 2;
    static final int BIT_TARGET_NEXT = 4;
    static final int BIT_STOP_NODE = 8;
    static final int BIT_ARC_HAS_OUTPUT = 16;
    static final int BIT_ARC_HAS_FINAL_OUTPUT = 32;
    private static final int BIT_TARGET_DELTA = 64;
    private static final byte ARCS_AS_FIXED_ARRAY = 32;
    static final int FIXED_ARRAY_SHALLOW_DISTANCE = 3;
    static final int FIXED_ARRAY_NUM_ARCS_SHALLOW = 5;
    static final int FIXED_ARRAY_NUM_ARCS_DEEP = 10;
    private int[] bytesPerArc = new int[0];
    private static final String FILE_FORMAT_NAME = "FST";
    private static final int VERSION_START = 0;
    private static final int VERSION_INT_NUM_BYTES_PER_ARC = 1;
    private static final int VERSION_SHORT_BYTE2_LABELS = 2;
    private static final int VERSION_PACKED = 3;
    private static final int VERSION_CURRENT = 3;
    private static final int FINAL_END_NODE = -1;
    private static final int NON_FINAL_END_NODE = 0;
    T emptyOutput;
    private byte[] emptyOutputBytes;
    byte[] bytes;
    int byteUpto = 0;
    private int startNode = -1;
    public final Outputs<T> outputs;
    private int lastFrozenNode;
    private final T NO_OUTPUT;
    public int nodeCount;
    public int arcCount;
    public int arcWithOutputCount;
    private final boolean packed;
    private final int[] nodeRefToAddress;
    public static final int END_LABEL = -1;
    private boolean allowArrayArcs = true;
    private Arc<T>[] cachedRootArcs;
    private final BytesWriter writer;
    private int[] nodeAddress;
    private int[] inCounts;

    private static final boolean flag(int flags, int bit) {
        return (flags & bit) != 0;
    }

    FST(INPUT_TYPE inputType, Outputs<T> outputs, boolean willPackFST) {
        this.inputType = inputType;
        this.outputs = outputs;
        this.bytes = new byte[128];
        this.NO_OUTPUT = outputs.getNoOutput();
        if (willPackFST) {
            this.nodeAddress = new int[8];
            this.inCounts = new int[8];
        } else {
            this.nodeAddress = null;
            this.inCounts = null;
        }
        this.writer = new BytesWriter();
        this.emptyOutput = null;
        this.packed = false;
        this.nodeRefToAddress = null;
    }

    public FST(DataInput in, Outputs<T> outputs) throws IOException {
        this.outputs = outputs;
        this.writer = null;
        CodecUtil.checkHeader(in, FILE_FORMAT_NAME, 3, 3);
        boolean bl = this.packed = in.readByte() == 1;
        if (in.readByte() == 1) {
            int numBytes = in.readVInt();
            this.bytes = new byte[numBytes];
            in.readBytes(this.bytes, 0, numBytes);
            this.emptyOutput = this.packed ? outputs.read(this.getBytesReader(0)) : outputs.read(this.getBytesReader(numBytes - 1));
        } else {
            this.emptyOutput = null;
        }
        byte t = in.readByte();
        switch (t) {
            case 0: {
                this.inputType = INPUT_TYPE.BYTE1;
                break;
            }
            case 1: {
                this.inputType = INPUT_TYPE.BYTE2;
                break;
            }
            case 2: {
                this.inputType = INPUT_TYPE.BYTE4;
                break;
            }
            default: {
                throw new IllegalStateException("invalid input type " + t);
            }
        }
        if (this.packed) {
            int nodeRefCount = in.readVInt();
            this.nodeRefToAddress = new int[nodeRefCount];
            for (int idx = 0; idx < nodeRefCount; ++idx) {
                this.nodeRefToAddress[idx] = in.readVInt();
            }
        } else {
            this.nodeRefToAddress = null;
        }
        this.startNode = in.readVInt();
        this.nodeCount = in.readVInt();
        this.arcCount = in.readVInt();
        this.arcWithOutputCount = in.readVInt();
        this.bytes = new byte[in.readVInt()];
        in.readBytes(this.bytes, 0, this.bytes.length);
        this.NO_OUTPUT = outputs.getNoOutput();
        this.cacheRootArcs();
    }

    public INPUT_TYPE getInputType() {
        return this.inputType;
    }

    public int sizeInBytes() {
        int size = this.bytes.length;
        if (this.packed) {
            size += this.nodeRefToAddress.length * 4;
        } else if (this.nodeAddress != null) {
            size += this.nodeAddress.length * 4;
            size += this.inCounts.length * 4;
        }
        return size;
    }

    void finish(int startNode) throws IOException {
        if (startNode == -1 && this.emptyOutput != null) {
            startNode = 0;
        }
        if (this.startNode != -1) {
            throw new IllegalStateException("already finished");
        }
        byte[] finalBytes = new byte[this.writer.posWrite];
        System.arraycopy(this.bytes, 0, finalBytes, 0, this.writer.posWrite);
        this.bytes = finalBytes;
        this.startNode = startNode;
        this.cacheRootArcs();
    }

    private int getNodeAddress(int node) {
        if (this.nodeAddress != null) {
            return this.nodeAddress[node];
        }
        return node;
    }

    private void cacheRootArcs() throws IOException {
        this.cachedRootArcs = new Arc[128];
        Arc arc = new Arc();
        this.getFirstArc(arc);
        BytesReader in = this.getBytesReader(0);
        if (FST.targetHasArcs(arc)) {
            this.readFirstRealTargetArc(arc.target, arc, in);
            while (true) {
                assert (arc.label != -1);
                if (arc.label >= this.cachedRootArcs.length) break;
                this.cachedRootArcs[arc.label] = new Arc().copyFrom(arc);
                if (arc.isLast()) break;
                this.readNextRealArc(arc, in);
            }
        }
    }

    public T getEmptyOutput() {
        return this.emptyOutput;
    }

    void setEmptyOutput(T v) throws IOException {
        this.emptyOutput = this.emptyOutput != null ? this.outputs.merge(this.emptyOutput, v) : v;
        int posSave = this.writer.posWrite;
        this.outputs.write(this.emptyOutput, this.writer);
        this.emptyOutputBytes = new byte[this.writer.posWrite - posSave];
        if (!this.packed) {
            int stopAt = (this.writer.posWrite - posSave) / 2;
            for (int upto = 0; upto < stopAt; ++upto) {
                byte b = this.bytes[posSave + upto];
                this.bytes[posSave + upto] = this.bytes[this.writer.posWrite - upto - 1];
                this.bytes[this.writer.posWrite - upto - 1] = b;
            }
        }
        System.arraycopy(this.bytes, posSave, this.emptyOutputBytes, 0, this.writer.posWrite - posSave);
        this.writer.posWrite = posSave;
    }

    public void save(DataOutput out) throws IOException {
        if (this.startNode == -1) {
            throw new IllegalStateException("call finish first");
        }
        if (this.nodeAddress != null) {
            throw new IllegalStateException("cannot save an FST pre-packed FST; it must first be packed");
        }
        CodecUtil.writeHeader(out, FILE_FORMAT_NAME, 3);
        if (this.packed) {
            out.writeByte((byte)1);
        } else {
            out.writeByte((byte)0);
        }
        if (this.emptyOutput != null) {
            out.writeByte((byte)1);
            out.writeVInt(this.emptyOutputBytes.length);
            out.writeBytes(this.emptyOutputBytes, 0, this.emptyOutputBytes.length);
        } else {
            out.writeByte((byte)0);
        }
        int t = this.inputType == INPUT_TYPE.BYTE1 ? 0 : (this.inputType == INPUT_TYPE.BYTE2 ? 1 : 2);
        out.writeByte((byte)t);
        if (this.packed) {
            assert (this.nodeRefToAddress != null);
            out.writeVInt(this.nodeRefToAddress.length);
            for (int idx = 0; idx < this.nodeRefToAddress.length; ++idx) {
                out.writeVInt(this.nodeRefToAddress[idx]);
            }
        }
        out.writeVInt(this.startNode);
        out.writeVInt(this.nodeCount);
        out.writeVInt(this.arcCount);
        out.writeVInt(this.arcWithOutputCount);
        out.writeVInt(this.bytes.length);
        out.writeBytes(this.bytes, 0, this.bytes.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void save(File file) throws IOException {
        BufferedOutputStream os;
        block3: {
            boolean success = false;
            os = new BufferedOutputStream(new FileOutputStream(file));
            try {
                this.save(new OutputStreamDataOutput(os));
                success = true;
                Object var5_4 = null;
                if (!success) break block3;
            }
            catch (Throwable throwable) {
                Object var5_5 = null;
                if (success) {
                    IOUtils.close(os);
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException(os);
                throw throwable;
            }
            IOUtils.close(os);
            return;
        }
        IOUtils.closeWhileHandlingException(os);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> FST<T> read(File file, Outputs<T> outputs) throws IOException {
        FST<T> fST;
        block5: {
            BufferedInputStream is;
            block4: {
                is = new BufferedInputStream(new FileInputStream(file));
                boolean success = false;
                try {
                    FST<T> fst = new FST<T>(new InputStreamDataInput(is), outputs);
                    success = true;
                    fST = fst;
                    Object var7_6 = null;
                    if (!success) break block4;
                }
                catch (Throwable throwable) {
                    Object var7_7 = null;
                    if (success) {
                        IOUtils.close(is);
                    } else {
                        IOUtils.closeWhileHandlingException(is);
                    }
                    throw throwable;
                }
                IOUtils.close(is);
                break block5;
            }
            IOUtils.closeWhileHandlingException(is);
        }
        return fST;
    }

    private void writeLabel(int v) throws IOException {
        assert (v >= 0) : "v=" + v;
        if (this.inputType == INPUT_TYPE.BYTE1) {
            assert (v <= 255) : "v=" + v;
            this.writer.writeByte((byte)v);
        } else if (this.inputType == INPUT_TYPE.BYTE2) {
            assert (v <= 65535) : "v=" + v;
            this.writer.writeShort((short)v);
        } else {
            this.writer.writeVInt(v);
        }
    }

    int readLabel(DataInput in) throws IOException {
        int v = this.inputType == INPUT_TYPE.BYTE1 ? in.readByte() & 0xFF : (this.inputType == INPUT_TYPE.BYTE2 ? in.readShort() & 0xFFFF : in.readVInt());
        return v;
    }

    public static <T> boolean targetHasArcs(Arc<T> arc) {
        return arc.target > 0;
    }

    int addNode(Builder.UnCompiledNode<T> nodeIn) throws IOException {
        int node;
        int fixedArrayStart;
        if (nodeIn.numArcs == 0) {
            if (nodeIn.isFinal) {
                return -1;
            }
            return 0;
        }
        int startAddress = this.writer.posWrite;
        boolean doFixedArray = this.shouldExpand(nodeIn);
        if (doFixedArray) {
            if (this.bytesPerArc.length < nodeIn.numArcs) {
                this.bytesPerArc = new int[ArrayUtil.oversize(nodeIn.numArcs, 1)];
            }
            this.writer.writeByte((byte)32);
            this.writer.writeVInt(nodeIn.numArcs);
            this.writer.writeInt(0);
            fixedArrayStart = this.writer.posWrite;
        } else {
            fixedArrayStart = 0;
        }
        this.arcCount += nodeIn.numArcs;
        int lastArc = nodeIn.numArcs - 1;
        int lastArcStart = this.writer.posWrite;
        int maxBytesPerArc = 0;
        for (int arcIdx = 0; arcIdx < nodeIn.numArcs; ++arcIdx) {
            boolean targetHasArcs;
            Builder.Arc arc = nodeIn.arcs[arcIdx];
            Builder.CompiledNode target = (Builder.CompiledNode)arc.target;
            int flags = 0;
            if (arcIdx == lastArc) {
                flags += 2;
            }
            if (this.lastFrozenNode == target.node && !doFixedArray) {
                flags += 4;
            }
            if (arc.isFinal) {
                ++flags;
                if (arc.nextFinalOutput != this.NO_OUTPUT) {
                    flags += 32;
                }
            } else assert (arc.nextFinalOutput == this.NO_OUTPUT);
            boolean bl = targetHasArcs = target.node > 0;
            if (!targetHasArcs) {
                flags += 8;
            } else if (this.inCounts != null) {
                int n = target.node;
                this.inCounts[n] = this.inCounts[n] + 1;
            }
            if (arc.output != this.NO_OUTPUT) {
                flags += 16;
            }
            this.writer.writeByte((byte)flags);
            this.writeLabel(arc.label);
            if (arc.output != this.NO_OUTPUT) {
                this.outputs.write(arc.output, this.writer);
                ++this.arcWithOutputCount;
            }
            if (arc.nextFinalOutput != this.NO_OUTPUT) {
                this.outputs.write(arc.nextFinalOutput, this.writer);
            }
            if (targetHasArcs && (flags & 4) == 0) {
                assert (target.node > 0);
                this.writer.writeInt(target.node);
            }
            if (!doFixedArray) continue;
            this.bytesPerArc[arcIdx] = this.writer.posWrite - lastArcStart;
            lastArcStart = this.writer.posWrite;
            maxBytesPerArc = Math.max(maxBytesPerArc, this.bytesPerArc[arcIdx]);
        }
        if (doFixedArray) {
            int destPos;
            assert (maxBytesPerArc > 0);
            int sizeNeeded = fixedArrayStart + nodeIn.numArcs * maxBytesPerArc;
            this.bytes = ArrayUtil.grow(this.bytes, sizeNeeded);
            this.bytes[fixedArrayStart - 4] = (byte)(maxBytesPerArc >> 24);
            this.bytes[fixedArrayStart - 3] = (byte)(maxBytesPerArc >> 16);
            this.bytes[fixedArrayStart - 2] = (byte)(maxBytesPerArc >> 8);
            this.bytes[fixedArrayStart - 1] = (byte)maxBytesPerArc;
            int srcPos = this.writer.posWrite;
            this.writer.posWrite = destPos = fixedArrayStart + nodeIn.numArcs * maxBytesPerArc;
            for (int arcIdx = nodeIn.numArcs - 1; arcIdx >= 0; --arcIdx) {
                if ((srcPos -= this.bytesPerArc[arcIdx]) == (destPos -= maxBytesPerArc)) continue;
                assert (destPos > srcPos);
                System.arraycopy(this.bytes, srcPos, this.bytes, destPos, this.bytesPerArc[arcIdx]);
            }
        }
        int endAddress = this.writer.posWrite - 1;
        int left = startAddress;
        int right = endAddress;
        while (left < right) {
            byte b = this.bytes[left];
            this.bytes[left++] = this.bytes[right];
            this.bytes[right--] = b;
        }
        ++this.nodeCount;
        if (this.nodeAddress != null) {
            if (this.nodeCount == this.nodeAddress.length) {
                this.nodeAddress = ArrayUtil.grow(this.nodeAddress);
                this.inCounts = ArrayUtil.grow(this.inCounts);
            }
            this.nodeAddress[this.nodeCount] = endAddress;
            node = this.nodeCount;
        } else {
            node = endAddress;
        }
        this.lastFrozenNode = node;
        return node;
    }

    public Arc<T> getFirstArc(Arc<T> arc) {
        if (this.emptyOutput != null) {
            arc.flags = (byte)3;
            arc.nextFinalOutput = this.emptyOutput;
        } else {
            arc.flags = (byte)2;
            arc.nextFinalOutput = this.NO_OUTPUT;
        }
        arc.output = this.NO_OUTPUT;
        arc.target = this.startNode;
        return arc;
    }

    public Arc<T> readLastTargetArc(Arc<T> follow, Arc<T> arc) throws IOException {
        if (!FST.targetHasArcs(follow)) {
            assert (follow.isFinal());
            arc.label = -1;
            arc.target = -1;
            arc.output = follow.nextFinalOutput;
            arc.flags = (byte)2;
            return arc;
        }
        BytesReader in = this.getBytesReader(this.getNodeAddress(follow.target));
        arc.node = follow.target;
        byte b = in.readByte();
        if (b == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed ? in.readVInt() : in.readInt();
            arc.posArcsStart = in.pos;
            arc.arcIdx = arc.numArcs - 2;
        } else {
            arc.flags = b;
            arc.bytesPerArc = 0;
            while (!arc.isLast()) {
                this.readLabel(in);
                if (arc.flag(16)) {
                    this.outputs.read(in);
                }
                if (arc.flag(32)) {
                    this.outputs.read(in);
                }
                if (!arc.flag(8) && !arc.flag(4)) {
                    if (this.packed) {
                        in.readVInt();
                    } else {
                        in.skip(4);
                    }
                }
                arc.flags = in.readByte();
            }
            in.skip(-1);
            arc.nextArc = in.pos;
        }
        this.readNextRealArc(arc, in);
        assert (arc.isLast());
        return arc;
    }

    public Arc<T> readFirstTargetArc(Arc<T> follow, Arc<T> arc) throws IOException {
        if (follow.isFinal()) {
            arc.label = -1;
            arc.output = follow.nextFinalOutput;
            arc.flags = 1;
            if (follow.target <= 0) {
                arc.flags = (byte)(arc.flags | 2);
            } else {
                arc.node = follow.target;
                arc.nextArc = follow.target;
            }
            arc.target = -1;
            return arc;
        }
        return this.readFirstRealTargetArc(follow.target, arc, this.getBytesReader(0));
    }

    public Arc<T> readFirstRealTargetArc(int node, Arc<T> arc, BytesReader in) throws IOException {
        int address;
        assert (in.bytes == this.bytes);
        in.pos = address = this.getNodeAddress(node);
        arc.node = node;
        if (in.readByte() == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed ? in.readVInt() : in.readInt();
            arc.arcIdx = -1;
            arc.nextArc = arc.posArcsStart = in.pos;
        } else {
            arc.nextArc = address;
            arc.bytesPerArc = 0;
        }
        return this.readNextRealArc(arc, in);
    }

    boolean isExpandedTarget(Arc<T> follow) throws IOException {
        if (!FST.targetHasArcs(follow)) {
            return false;
        }
        BytesReader in = this.getBytesReader(this.getNodeAddress(follow.target));
        return in.readByte() == 32;
    }

    public Arc<T> readNextArc(Arc<T> arc) throws IOException {
        if (arc.label == -1) {
            if (arc.nextArc <= 0) {
                throw new IllegalArgumentException("cannot readNextArc when arc.isLast()=true");
            }
            return this.readFirstRealTargetArc(arc.nextArc, arc, this.getBytesReader(0));
        }
        return this.readNextRealArc(arc, this.getBytesReader(0));
    }

    public int readNextArcLabel(Arc<T> arc) throws IOException {
        BytesReader in;
        assert (!arc.isLast());
        if (arc.label == -1) {
            in = this.getBytesReader(this.getNodeAddress(arc.nextArc));
            byte b = this.bytes[in.pos];
            if (b == 32) {
                in.skip(1);
                in.readVInt();
                if (this.packed) {
                    in.readVInt();
                } else {
                    in.readInt();
                }
            }
        } else if (arc.bytesPerArc != 0) {
            in = this.getBytesReader(arc.posArcsStart);
            in.skip((1 + arc.arcIdx) * arc.bytesPerArc);
        } else {
            in = this.getBytesReader(arc.nextArc);
        }
        in.readByte();
        return this.readLabel(in);
    }

    public Arc<T> readNextRealArc(Arc<T> arc, BytesReader in) throws IOException {
        assert (in.bytes == this.bytes);
        if (arc.bytesPerArc != 0) {
            ++arc.arcIdx;
            assert (arc.arcIdx < arc.numArcs);
            in.skip(arc.posArcsStart, arc.arcIdx * arc.bytesPerArc);
        } else {
            in.pos = arc.nextArc;
        }
        arc.flags = in.readByte();
        arc.label = this.readLabel(in);
        arc.output = arc.flag(16) ? this.outputs.read(in) : this.outputs.getNoOutput();
        arc.nextFinalOutput = arc.flag(32) ? this.outputs.read(in) : this.outputs.getNoOutput();
        if (arc.flag(8)) {
            arc.target = arc.flag(1) ? -1 : 0;
            arc.nextArc = in.pos;
        } else if (arc.flag(4)) {
            arc.nextArc = in.pos;
            if (this.nodeAddress == null) {
                if (!arc.flag(2)) {
                    if (arc.bytesPerArc == 0) {
                        this.seekToNextNode(in);
                    } else {
                        in.skip(arc.posArcsStart, arc.bytesPerArc * arc.numArcs);
                    }
                }
                arc.target = in.pos;
            } else {
                arc.target = arc.node - 1;
                assert (arc.target > 0);
            }
        } else {
            if (this.packed) {
                int pos = in.pos;
                int code = in.readVInt();
                arc.target = arc.flag(64) ? pos + code : (code < this.nodeRefToAddress.length ? this.nodeRefToAddress[code] : code);
            } else {
                arc.target = in.readInt();
            }
            arc.nextArc = in.pos;
        }
        return arc;
    }

    public Arc<T> findTargetArc(int labelToMatch, Arc<T> follow, Arc<T> arc, BytesReader in) throws IOException {
        assert (this.cachedRootArcs != null);
        assert (in.bytes == this.bytes);
        if (labelToMatch == -1) {
            if (follow.isFinal()) {
                if (follow.target <= 0) {
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
        if (follow.target == this.startNode && labelToMatch < this.cachedRootArcs.length) {
            Arc<T> result = this.cachedRootArcs[labelToMatch];
            if (result == null) {
                return result;
            }
            arc.copyFrom(result);
            return arc;
        }
        if (!FST.targetHasArcs(follow)) {
            return null;
        }
        in.pos = this.getNodeAddress(follow.target);
        arc.node = follow.target;
        if (in.readByte() == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed ? in.readVInt() : in.readInt();
            arc.posArcsStart = in.pos;
            int low = 0;
            int high = arc.numArcs - 1;
            while (low <= high) {
                int mid = low + high >>> 1;
                in.skip(arc.posArcsStart, arc.bytesPerArc * mid + 1);
                int midLabel = this.readLabel(in);
                int cmp = midLabel - labelToMatch;
                if (cmp < 0) {
                    low = mid + 1;
                    continue;
                }
                if (cmp > 0) {
                    high = mid - 1;
                    continue;
                }
                arc.arcIdx = mid - 1;
                return this.readNextRealArc(arc, in);
            }
            return null;
        }
        this.readFirstRealTargetArc(follow.target, arc, in);
        while (arc.label != labelToMatch) {
            if (arc.label > labelToMatch) {
                return null;
            }
            if (arc.isLast()) {
                return null;
            }
            this.readNextRealArc(arc, in);
        }
        return arc;
    }

    private void seekToNextNode(BytesReader in) throws IOException {
        byte flags;
        do {
            flags = in.readByte();
            this.readLabel(in);
            if (FST.flag(flags, 16)) {
                this.outputs.read(in);
            }
            if (FST.flag(flags, 32)) {
                this.outputs.read(in);
            }
            if (FST.flag(flags, 8) || FST.flag(flags, 4)) continue;
            if (this.packed) {
                in.readVInt();
                continue;
            }
            in.readInt();
        } while (!FST.flag(flags, 2));
    }

    public int getNodeCount() {
        return 1 + this.nodeCount;
    }

    public int getArcCount() {
        return this.arcCount;
    }

    public int getArcWithOutputCount() {
        return this.arcWithOutputCount;
    }

    public void setAllowArrayArcs(boolean v) {
        this.allowArrayArcs = v;
    }

    private boolean shouldExpand(Builder.UnCompiledNode<T> node) {
        return this.allowArrayArcs && (node.depth <= 3 && node.numArcs >= 5 || node.numArcs >= 10);
    }

    public final BytesReader getBytesReader(int pos) {
        if (this.packed) {
            return new ForwardBytesReader(this.bytes, pos);
        }
        return new ReverseBytesReader(this.bytes, pos);
    }

    private FST(INPUT_TYPE inputType, int[] nodeRefToAddress, Outputs<T> outputs) {
        this.packed = true;
        this.inputType = inputType;
        this.bytes = new byte[128];
        this.nodeRefToAddress = nodeRefToAddress;
        this.outputs = outputs;
        this.NO_OUTPUT = outputs.getNoOutput();
        this.writer = new BytesWriter();
    }

    public FST<T> pack(int minInCountDeref, int maxDerefNodes) throws IOException {
        boolean negDelta;
        boolean changed;
        if (this.nodeAddress == null) {
            throw new IllegalArgumentException("this FST was not built with willPackFST=true");
        }
        Arc arc = new Arc();
        BytesReader r = this.getBytesReader(0);
        int topN = Math.min(maxDerefNodes, this.inCounts.length);
        NodeQueue q = new NodeQueue(topN);
        NodeAndInCount bottom = null;
        for (int node = 0; node < this.inCounts.length; ++node) {
            if (this.inCounts[node] < minInCountDeref) continue;
            if (bottom == null) {
                q.add(new NodeAndInCount(node, this.inCounts[node]));
                if (q.size() != topN) continue;
                bottom = (NodeAndInCount)q.top();
                continue;
            }
            if (this.inCounts[node] <= bottom.count) continue;
            q.insertWithOverflow(new NodeAndInCount(node, this.inCounts[node]));
        }
        this.inCounts = null;
        HashMap<Integer, Integer> topNodeMap = new HashMap<Integer, Integer>();
        for (int downTo = q.size() - 1; downTo >= 0; --downTo) {
            NodeAndInCount n = (NodeAndInCount)q.pop();
            topNodeMap.put(n.node, downTo);
        }
        int[] nodeRefToAddressIn = new int[topNodeMap.size()];
        FST<T> fst = new FST<T>(this.inputType, nodeRefToAddressIn, this.outputs);
        BytesWriter writer = fst.writer;
        int[] newNodeAddress = new int[1 + this.nodeCount];
        for (int node = 1; node <= this.nodeCount; ++node) {
            newNodeAddress[node] = 1 + this.bytes.length - this.nodeAddress[node];
        }
        do {
            changed = false;
            negDelta = false;
            writer.posWrite = 0;
            writer.writeByte((byte)0);
            fst.arcWithOutputCount = 0;
            fst.nodeCount = 0;
            fst.arcCount = 0;
            int nextCount = 0;
            int topCount = 0;
            int deltaCount = 0;
            int absCount = 0;
            int changedCount = 0;
            int addressError = 0;
            for (int node = this.nodeCount; node >= 1; --node) {
                ++fst.nodeCount;
                int address = writer.posWrite;
                if (address != newNodeAddress[node]) {
                    addressError = address - newNodeAddress[node];
                    changed = true;
                    newNodeAddress[node] = address;
                    ++changedCount;
                }
                int nodeArcCount = 0;
                int bytesPerArc = 0;
                boolean retry = false;
                boolean anyNegDelta = false;
                while (true) {
                    boolean useArcArray;
                    this.readFirstRealTargetArc(node, arc, r);
                    boolean bl = useArcArray = arc.bytesPerArc != 0;
                    if (useArcArray) {
                        if (bytesPerArc == 0) {
                            bytesPerArc = arc.bytesPerArc;
                        }
                        writer.writeByte((byte)32);
                        writer.writeVInt(arc.numArcs);
                        writer.writeVInt(bytesPerArc);
                    }
                    int maxBytesPerArc = 0;
                    while (true) {
                        int delta;
                        int absPtr;
                        Integer ptr;
                        boolean doWriteTarget;
                        int arcStartPos = writer.posWrite;
                        ++nodeArcCount;
                        byte flags = 0;
                        if (arc.isLast()) {
                            flags = (byte)(flags + 2);
                        }
                        if (!useArcArray && node != 1 && arc.target == node - 1) {
                            flags = (byte)(flags + 4);
                            if (!retry) {
                                ++nextCount;
                            }
                        }
                        if (arc.isFinal()) {
                            flags = (byte)(flags + 1);
                            if (arc.nextFinalOutput != this.NO_OUTPUT) {
                                flags = (byte)(flags + 32);
                            }
                        } else assert (arc.nextFinalOutput == this.NO_OUTPUT);
                        if (!FST.targetHasArcs(arc)) {
                            flags = (byte)(flags + 8);
                        }
                        if (arc.output != this.NO_OUTPUT) {
                            flags = (byte)(flags + 16);
                        }
                        boolean bl2 = doWriteTarget = FST.targetHasArcs(arc) && (flags & 4) == 0;
                        if (doWriteTarget) {
                            ptr = (Integer)topNodeMap.get(arc.target);
                            absPtr = ptr != null ? ptr : topNodeMap.size() + newNodeAddress[arc.target] + addressError;
                            delta = newNodeAddress[arc.target] + addressError - writer.posWrite - 2;
                            if (delta < 0) {
                                anyNegDelta = true;
                                delta = 0;
                            }
                            if (delta < absPtr) {
                                flags = (byte)(flags | 0x40);
                            }
                        } else {
                            ptr = null;
                            absPtr = 0;
                        }
                        writer.writeByte(flags);
                        super.writeLabel(arc.label);
                        if (arc.output != this.NO_OUTPUT) {
                            this.outputs.write(arc.output, writer);
                            if (!retry) {
                                ++fst.arcWithOutputCount;
                            }
                        }
                        if (arc.nextFinalOutput != this.NO_OUTPUT) {
                            this.outputs.write(arc.nextFinalOutput, writer);
                        }
                        if (doWriteTarget) {
                            delta = newNodeAddress[arc.target] + addressError - writer.posWrite;
                            if (delta < 0) {
                                anyNegDelta = true;
                                delta = 0;
                            }
                            if (FST.flag(flags, 64)) {
                                writer.writeVInt(delta);
                                if (!retry) {
                                    ++deltaCount;
                                }
                            } else {
                                writer.writeVInt(absPtr);
                                if (!retry) {
                                    if (absPtr >= topNodeMap.size()) {
                                        ++absCount;
                                    } else {
                                        ++topCount;
                                    }
                                }
                            }
                        }
                        if (useArcArray) {
                            int arcBytes = writer.posWrite - arcStartPos;
                            maxBytesPerArc = Math.max(maxBytesPerArc, arcBytes);
                            writer.setPosWrite(arcStartPos + bytesPerArc);
                        }
                        if (arc.isLast()) break;
                        this.readNextRealArc(arc, r);
                    }
                    if (!useArcArray || maxBytesPerArc == bytesPerArc || retry && maxBytesPerArc <= bytesPerArc) break;
                    bytesPerArc = maxBytesPerArc;
                    writer.posWrite = address;
                    nodeArcCount = 0;
                    retry = true;
                    anyNegDelta = false;
                }
                negDelta |= anyNegDelta;
                fst.arcCount += nodeArcCount;
            }
        } while (changed);
        assert (!negDelta);
        for (Map.Entry ent : topNodeMap.entrySet()) {
            nodeRefToAddressIn[((Integer)ent.getValue()).intValue()] = newNodeAddress[(Integer)ent.getKey()];
        }
        fst.startNode = newNodeAddress[this.startNode];
        if (this.emptyOutput != null) {
            fst.setEmptyOutput(this.emptyOutput);
        }
        assert (fst.nodeCount == this.nodeCount) : "fst.nodeCount=" + fst.nodeCount + " nodeCount=" + this.nodeCount;
        assert (fst.arcCount == this.arcCount);
        assert (fst.arcWithOutputCount == this.arcWithOutputCount) : "fst.arcWithOutputCount=" + fst.arcWithOutputCount + " arcWithOutputCount=" + this.arcWithOutputCount;
        byte[] finalBytes = new byte[writer.posWrite];
        System.arraycopy(fst.bytes, 0, finalBytes, 0, writer.posWrite);
        fst.bytes = finalBytes;
        super.cacheRootArcs();
        return fst;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class NodeQueue
    extends PriorityQueue<NodeAndInCount> {
        public NodeQueue(int topN) {
            this.initialize(topN);
        }

        @Override
        public boolean lessThan(NodeAndInCount a, NodeAndInCount b) {
            int cmp = a.compareTo(b);
            assert (cmp != 0);
            return cmp < 0;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class NodeAndInCount
    implements Comparable<NodeAndInCount> {
        final int node;
        final int count;

        public NodeAndInCount(int node, int count) {
            this.node = node;
            this.count = count;
        }

        @Override
        public int compareTo(NodeAndInCount other) {
            if (this.count > other.count) {
                return 1;
            }
            if (this.count < other.count) {
                return -1;
            }
            return other.node - this.node;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ArcAndState<T> {
        final Arc<T> arc;
        final IntsRef chain;

        public ArcAndState(Arc<T> arc, IntsRef chain) {
            this.arc = arc;
            this.chain = chain;
        }
    }

    static final class ForwardBytesReader
    extends BytesReader {
        public ForwardBytesReader(byte[] bytes, int pos) {
            super(bytes, pos);
        }

        public byte readByte() {
            return this.bytes[this.pos++];
        }

        public void readBytes(byte[] b, int offset, int len) {
            System.arraycopy(this.bytes, this.pos, b, offset, len);
            this.pos += len;
        }

        public void skip(int count) {
            this.pos += count;
        }

        public void skip(int base, int count) {
            this.pos = base + count;
        }
    }

    static final class ReverseBytesReader
    extends BytesReader {
        public ReverseBytesReader(byte[] bytes, int pos) {
            super(bytes, pos);
        }

        public byte readByte() {
            return this.bytes[this.pos--];
        }

        public void readBytes(byte[] b, int offset, int len) {
            for (int i = 0; i < len; ++i) {
                b[offset + i] = this.bytes[this.pos--];
            }
        }

        public void skip(int count) {
            this.pos -= count;
        }

        public void skip(int base, int count) {
            this.pos = base - count;
        }
    }

    public static abstract class BytesReader
    extends DataInput {
        protected int pos;
        protected final byte[] bytes;

        protected BytesReader(byte[] bytes, int pos) {
            this.bytes = bytes;
            this.pos = pos;
        }

        abstract void skip(int var1);

        abstract void skip(int var1, int var2);
    }

    class BytesWriter
    extends DataOutput {
        int posWrite = 1;

        public void writeByte(byte b) {
            assert (this.posWrite <= FST.this.bytes.length);
            if (FST.this.bytes.length == this.posWrite) {
                FST.this.bytes = ArrayUtil.grow(FST.this.bytes);
            }
            assert (this.posWrite < FST.this.bytes.length) : "posWrite=" + this.posWrite + " bytes.length=" + FST.this.bytes.length;
            FST.this.bytes[this.posWrite++] = b;
        }

        public void setPosWrite(int posWrite) {
            this.posWrite = posWrite;
            if (FST.this.bytes.length < posWrite) {
                FST.this.bytes = ArrayUtil.grow(FST.this.bytes, posWrite);
            }
        }

        public void writeBytes(byte[] b, int offset, int length) {
            int size = this.posWrite + length;
            FST.this.bytes = ArrayUtil.grow(FST.this.bytes, size);
            System.arraycopy(b, offset, FST.this.bytes, this.posWrite, length);
            this.posWrite += length;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class Arc<T> {
        public int label;
        public T output;
        int node;
        public int target;
        byte flags;
        public T nextFinalOutput;
        int nextArc;
        int posArcsStart;
        int bytesPerArc;
        int arcIdx;
        int numArcs;

        public Arc<T> copyFrom(Arc<T> other) {
            this.node = other.node;
            this.label = other.label;
            this.target = other.target;
            this.flags = other.flags;
            this.output = other.output;
            this.nextFinalOutput = other.nextFinalOutput;
            this.nextArc = other.nextArc;
            this.bytesPerArc = other.bytesPerArc;
            if (this.bytesPerArc != 0) {
                this.posArcsStart = other.posArcsStart;
                this.arcIdx = other.arcIdx;
                this.numArcs = other.numArcs;
            }
            return this;
        }

        boolean flag(int flag) {
            return FST.flag(this.flags, flag);
        }

        public boolean isLast() {
            return this.flag(2);
        }

        public boolean isFinal() {
            return this.flag(1);
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("node=" + this.node);
            b.append(" target=" + this.target);
            b.append(" label=" + this.label);
            if (this.flag(2)) {
                b.append(" last");
            }
            if (this.flag(1)) {
                b.append(" final");
            }
            if (this.flag(4)) {
                b.append(" targetNext");
            }
            if (this.flag(16)) {
                b.append(" output=" + this.output);
            }
            if (this.flag(32)) {
                b.append(" nextFinalOutput=" + this.nextFinalOutput);
            }
            if (this.bytesPerArc != 0) {
                b.append(" arcArray(idx=" + this.arcIdx + " of " + this.numArcs + ")");
            }
            return b.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum INPUT_TYPE {
        BYTE1,
        BYTE2,
        BYTE4;

    }
}

