/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.InputStreamDataInput;
import org.apache.lucene.store.OutputStreamDataOutput;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.BytesStore;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.packed.GrowableWriter;
import org.apache.lucene.util.packed.PackedInts;

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
    private static final int VERSION_VINT_TARGET = 4;
    private static final int VERSION_CURRENT = 4;
    private static final long FINAL_END_NODE = -1L;
    private static final long NON_FINAL_END_NODE = 0L;
    T emptyOutput;
    final BytesStore bytes;
    private long startNode = -1L;
    public final Outputs<T> outputs;
    private long lastFrozenNode;
    private final T NO_OUTPUT;
    public long nodeCount;
    public long arcCount;
    public long arcWithOutputCount;
    private final boolean packed;
    private PackedInts.Reader nodeRefToAddress;
    public static final int END_LABEL = -1;
    private final boolean allowArrayArcs;
    private Arc<T>[] cachedRootArcs;
    private GrowableWriter nodeAddress;
    private GrowableWriter inCounts;
    private final int version;
    public static final int DEFAULT_MAX_BLOCK_BITS = Constants.JRE_IS_64BIT ? 30 : 28;

    private static boolean flag(int flags, int bit) {
        return (flags & bit) != 0;
    }

    FST(INPUT_TYPE inputType, Outputs<T> outputs, boolean willPackFST, float acceptableOverheadRatio, boolean allowArrayArcs, int bytesPageBits) {
        this.inputType = inputType;
        this.outputs = outputs;
        this.allowArrayArcs = allowArrayArcs;
        this.version = 4;
        this.bytes = new BytesStore(bytesPageBits);
        this.bytes.writeByte((byte)0);
        this.NO_OUTPUT = outputs.getNoOutput();
        if (willPackFST) {
            this.nodeAddress = new GrowableWriter(15, 8, acceptableOverheadRatio);
            this.inCounts = new GrowableWriter(1, 8, acceptableOverheadRatio);
        } else {
            this.nodeAddress = null;
            this.inCounts = null;
        }
        this.emptyOutput = null;
        this.packed = false;
        this.nodeRefToAddress = null;
    }

    public FST(DataInput in, Outputs<T> outputs) throws IOException {
        this(in, outputs, DEFAULT_MAX_BLOCK_BITS);
    }

    public FST(DataInput in, Outputs<T> outputs, int maxBlockBits) throws IOException {
        this.outputs = outputs;
        if (maxBlockBits < 1 || maxBlockBits > 30) {
            throw new IllegalArgumentException("maxBlockBits should be 1 .. 30; got " + maxBlockBits);
        }
        this.version = CodecUtil.checkHeader(in, FILE_FORMAT_NAME, 3, 4);
        boolean bl = this.packed = in.readByte() == 1;
        if (in.readByte() == 1) {
            BytesReader reader;
            BytesStore emptyBytes = new BytesStore(10);
            int numBytes = in.readVInt();
            emptyBytes.copyBytes(in, numBytes);
            if (this.packed) {
                reader = emptyBytes.getForwardReader();
            } else {
                reader = emptyBytes.getReverseReader();
                if (numBytes > 0) {
                    reader.setPosition(numBytes - 1);
                }
            }
            this.emptyOutput = outputs.readFinalOutput(reader);
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
        this.nodeRefToAddress = this.packed ? PackedInts.getReader(in) : null;
        this.startNode = in.readVLong();
        this.nodeCount = in.readVLong();
        this.arcCount = in.readVLong();
        this.arcWithOutputCount = in.readVLong();
        long numBytes = in.readVLong();
        this.bytes = new BytesStore(in, numBytes, 1 << maxBlockBits);
        this.NO_OUTPUT = outputs.getNoOutput();
        this.cacheRootArcs();
        this.allowArrayArcs = false;
    }

    public INPUT_TYPE getInputType() {
        return this.inputType;
    }

    public long sizeInBytes() {
        long size = this.bytes.getPosition();
        if (this.packed) {
            size += this.nodeRefToAddress.ramBytesUsed();
        } else if (this.nodeAddress != null) {
            size += this.nodeAddress.ramBytesUsed();
            size += this.inCounts.ramBytesUsed();
        }
        return size;
    }

    void finish(long startNode) throws IOException {
        if (this.startNode != -1L) {
            throw new IllegalStateException("already finished");
        }
        if (startNode == -1L && this.emptyOutput != null) {
            startNode = 0L;
        }
        this.startNode = startNode;
        this.bytes.finish();
        this.cacheRootArcs();
    }

    private long getNodeAddress(long node) {
        if (this.nodeAddress != null) {
            return this.nodeAddress.get((int)node);
        }
        return node;
    }

    private void cacheRootArcs() throws IOException {
        this.cachedRootArcs = new Arc[128];
        Arc arc = new Arc();
        this.getFirstArc(arc);
        BytesReader in = this.getBytesReader();
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
    }

    public void save(DataOutput out) throws IOException {
        if (this.startNode == -1L) {
            throw new IllegalStateException("call finish first");
        }
        if (this.nodeAddress != null) {
            throw new IllegalStateException("cannot save an FST pre-packed FST; it must first be packed");
        }
        if (this.packed && !(this.nodeRefToAddress instanceof PackedInts.Mutable)) {
            throw new IllegalStateException("cannot save a FST which has been loaded from disk ");
        }
        CodecUtil.writeHeader(out, FILE_FORMAT_NAME, 4);
        if (this.packed) {
            out.writeByte((byte)1);
        } else {
            out.writeByte((byte)0);
        }
        if (this.emptyOutput != null) {
            out.writeByte((byte)1);
            RAMOutputStream ros = new RAMOutputStream();
            this.outputs.writeFinalOutput(this.emptyOutput, ros);
            byte[] emptyOutputBytes = new byte[(int)ros.getFilePointer()];
            ros.writeTo(emptyOutputBytes, 0);
            if (!this.packed) {
                int stopAt = emptyOutputBytes.length / 2;
                for (int upto = 0; upto < stopAt; ++upto) {
                    byte b = emptyOutputBytes[upto];
                    emptyOutputBytes[upto] = emptyOutputBytes[emptyOutputBytes.length - upto - 1];
                    emptyOutputBytes[emptyOutputBytes.length - upto - 1] = b;
                }
            }
            out.writeVInt(emptyOutputBytes.length);
            out.writeBytes(emptyOutputBytes, 0, emptyOutputBytes.length);
        } else {
            out.writeByte((byte)0);
        }
        int t = this.inputType == INPUT_TYPE.BYTE1 ? 0 : (this.inputType == INPUT_TYPE.BYTE2 ? 1 : 2);
        out.writeByte((byte)t);
        if (this.packed) {
            ((PackedInts.Mutable)this.nodeRefToAddress).save(out);
        }
        out.writeVLong(this.startNode);
        out.writeVLong(this.nodeCount);
        out.writeVLong(this.arcCount);
        out.writeVLong(this.arcWithOutputCount);
        long numBytes = this.bytes.getPosition();
        out.writeVLong(numBytes);
        this.bytes.writeTo(out);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(File file) throws IOException {
        block5: {
            BufferedOutputStream os;
            block4: {
                boolean success = false;
                os = new BufferedOutputStream(new FileOutputStream(file));
                try {
                    this.save(new OutputStreamDataOutput(os));
                    success = true;
                    if (!success) break block4;
                }
                catch (Throwable throwable) {
                    if (success) {
                        IOUtils.close(os);
                    } else {
                        IOUtils.closeWhileHandlingException(os);
                    }
                    throw throwable;
                }
                IOUtils.close(os);
                break block5;
            }
            IOUtils.closeWhileHandlingException(os);
        }
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
                    if (!success) break block4;
                }
                catch (Throwable throwable) {
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

    private void writeLabel(DataOutput out, int v) throws IOException {
        assert (v >= 0) : "v=" + v;
        if (this.inputType == INPUT_TYPE.BYTE1) {
            assert (v <= 255) : "v=" + v;
            out.writeByte((byte)v);
        } else if (this.inputType == INPUT_TYPE.BYTE2) {
            assert (v <= 65535) : "v=" + v;
            out.writeShort((short)v);
        } else {
            out.writeVInt(v);
        }
    }

    int readLabel(DataInput in) throws IOException {
        int v = this.inputType == INPUT_TYPE.BYTE1 ? in.readByte() & 0xFF : (this.inputType == INPUT_TYPE.BYTE2 ? in.readShort() & 0xFFFF : in.readVInt());
        return v;
    }

    public static <T> boolean targetHasArcs(Arc<T> arc) {
        return arc.target > 0L;
    }

    long addNode(Builder.UnCompiledNode<T> nodeIn) throws IOException {
        long node;
        if (nodeIn.numArcs == 0) {
            if (nodeIn.isFinal) {
                return -1L;
            }
            return 0L;
        }
        long startAddress = this.bytes.getPosition();
        boolean doFixedArray = this.shouldExpand(nodeIn);
        if (doFixedArray && this.bytesPerArc.length < nodeIn.numArcs) {
            this.bytesPerArc = new int[ArrayUtil.oversize(nodeIn.numArcs, 1)];
        }
        this.arcCount += (long)nodeIn.numArcs;
        int lastArc = nodeIn.numArcs - 1;
        long lastArcStart = this.bytes.getPosition();
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
            boolean bl = targetHasArcs = target.node > 0L;
            if (!targetHasArcs) {
                flags += 8;
            } else if (this.inCounts != null) {
                this.inCounts.set((int)target.node, this.inCounts.get((int)target.node) + 1L);
            }
            if (arc.output != this.NO_OUTPUT) {
                flags += 16;
            }
            this.bytes.writeByte((byte)flags);
            this.writeLabel(this.bytes, arc.label);
            if (arc.output != this.NO_OUTPUT) {
                this.outputs.write(arc.output, this.bytes);
                ++this.arcWithOutputCount;
            }
            if (arc.nextFinalOutput != this.NO_OUTPUT) {
                this.outputs.writeFinalOutput(arc.nextFinalOutput, this.bytes);
            }
            if (targetHasArcs && (flags & 4) == 0) {
                assert (target.node > 0L);
                this.bytes.writeVLong(target.node);
            }
            if (!doFixedArray) continue;
            this.bytesPerArc[arcIdx] = (int)(this.bytes.getPosition() - lastArcStart);
            lastArcStart = this.bytes.getPosition();
            maxBytesPerArc = Math.max(maxBytesPerArc, this.bytesPerArc[arcIdx]);
        }
        if (doFixedArray) {
            int MAX_HEADER_SIZE = 11;
            assert (maxBytesPerArc > 0);
            byte[] header = new byte[11];
            ByteArrayDataOutput bad = new ByteArrayDataOutput(header);
            bad.writeByte((byte)32);
            bad.writeVInt(nodeIn.numArcs);
            bad.writeVInt(maxBytesPerArc);
            int headerLen = bad.getPosition();
            long fixedArrayStart = startAddress + (long)headerLen;
            long srcPos = this.bytes.getPosition();
            long destPos = fixedArrayStart + (long)(nodeIn.numArcs * maxBytesPerArc);
            assert (destPos >= srcPos);
            if (destPos > srcPos) {
                this.bytes.skipBytes((int)(destPos - srcPos));
                for (int arcIdx = nodeIn.numArcs - 1; arcIdx >= 0; --arcIdx) {
                    if ((srcPos -= (long)this.bytesPerArc[arcIdx]) == (destPos -= (long)maxBytesPerArc)) continue;
                    assert (destPos > srcPos) : "destPos=" + destPos + " srcPos=" + srcPos + " arcIdx=" + arcIdx + " maxBytesPerArc=" + maxBytesPerArc + " bytesPerArc[arcIdx]=" + this.bytesPerArc[arcIdx] + " nodeIn.numArcs=" + nodeIn.numArcs;
                    this.bytes.copyBytes(srcPos, destPos, this.bytesPerArc[arcIdx]);
                }
            }
            this.bytes.writeBytes(startAddress, header, 0, headerLen);
        }
        long thisNodeAddress = this.bytes.getPosition() - 1L;
        this.bytes.reverse(startAddress, thisNodeAddress);
        if (this.nodeAddress != null && this.nodeCount == Integer.MAX_VALUE) {
            throw new IllegalStateException("cannot create a packed FST with more than 2.1 billion nodes");
        }
        ++this.nodeCount;
        if (this.nodeAddress != null) {
            if ((int)this.nodeCount == this.nodeAddress.size()) {
                this.nodeAddress = this.nodeAddress.resize(ArrayUtil.oversize(this.nodeAddress.size() + 1, this.nodeAddress.getBitsPerValue()));
                this.inCounts = this.inCounts.resize(ArrayUtil.oversize(this.inCounts.size() + 1, this.inCounts.getBitsPerValue()));
            }
            this.nodeAddress.set((int)this.nodeCount, thisNodeAddress);
            node = this.nodeCount;
        } else {
            node = thisNodeAddress;
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

    public Arc<T> readLastTargetArc(Arc<T> follow, Arc<T> arc, BytesReader in) throws IOException {
        if (!FST.targetHasArcs(follow)) {
            assert (follow.isFinal());
            arc.label = -1;
            arc.target = -1L;
            arc.output = follow.nextFinalOutput;
            arc.flags = (byte)2;
            return arc;
        }
        in.setPosition(this.getNodeAddress(follow.target));
        arc.node = follow.target;
        byte b = in.readByte();
        if (b == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed || this.version >= 4 ? in.readVInt() : in.readInt();
            arc.posArcsStart = in.getPosition();
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
                    this.outputs.readFinalOutput(in);
                }
                if (!arc.flag(8) && !arc.flag(4)) {
                    if (this.packed) {
                        in.readVLong();
                    } else {
                        this.readUnpackedNodeTarget(in);
                    }
                }
                arc.flags = in.readByte();
            }
            in.skipBytes(-1);
            arc.nextArc = in.getPosition();
        }
        this.readNextRealArc(arc, in);
        assert (arc.isLast());
        return arc;
    }

    private long readUnpackedNodeTarget(BytesReader in) throws IOException {
        long target = this.version < 4 ? (long)in.readInt() : in.readVLong();
        return target;
    }

    public Arc<T> readFirstTargetArc(Arc<T> follow, Arc<T> arc, BytesReader in) throws IOException {
        if (follow.isFinal()) {
            arc.label = -1;
            arc.output = follow.nextFinalOutput;
            arc.flags = 1;
            if (follow.target <= 0L) {
                arc.flags = (byte)(arc.flags | 2);
            } else {
                arc.node = follow.target;
                arc.nextArc = follow.target;
            }
            arc.target = -1L;
            return arc;
        }
        return this.readFirstRealTargetArc(follow.target, arc, in);
    }

    public Arc<T> readFirstRealTargetArc(long node, Arc<T> arc, BytesReader in) throws IOException {
        long address = this.getNodeAddress(node);
        in.setPosition(address);
        arc.node = node;
        if (in.readByte() == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed || this.version >= 4 ? in.readVInt() : in.readInt();
            arc.arcIdx = -1;
            arc.nextArc = arc.posArcsStart = in.getPosition();
        } else {
            arc.nextArc = address;
            arc.bytesPerArc = 0;
        }
        return this.readNextRealArc(arc, in);
    }

    boolean isExpandedTarget(Arc<T> follow, BytesReader in) throws IOException {
        if (!FST.targetHasArcs(follow)) {
            return false;
        }
        in.setPosition(this.getNodeAddress(follow.target));
        return in.readByte() == 32;
    }

    public Arc<T> readNextArc(Arc<T> arc, BytesReader in) throws IOException {
        if (arc.label == -1) {
            if (arc.nextArc <= 0L) {
                throw new IllegalArgumentException("cannot readNextArc when arc.isLast()=true");
            }
            return this.readFirstRealTargetArc(arc.nextArc, arc, in);
        }
        return this.readNextRealArc(arc, in);
    }

    public int readNextArcLabel(Arc<T> arc, BytesReader in) throws IOException {
        assert (!arc.isLast());
        if (arc.label == -1) {
            long pos = this.getNodeAddress(arc.nextArc);
            in.setPosition(pos);
            byte b = in.readByte();
            if (b == 32) {
                in.readVInt();
                if (this.packed || this.version >= 4) {
                    in.readVInt();
                } else {
                    in.readInt();
                }
            } else {
                in.setPosition(pos);
            }
        } else if (arc.bytesPerArc != 0) {
            in.setPosition(arc.posArcsStart);
            in.skipBytes((1 + arc.arcIdx) * arc.bytesPerArc);
        } else {
            in.setPosition(arc.nextArc);
        }
        in.readByte();
        return this.readLabel(in);
    }

    public Arc<T> readNextRealArc(Arc<T> arc, BytesReader in) throws IOException {
        if (arc.bytesPerArc != 0) {
            ++arc.arcIdx;
            assert (arc.arcIdx < arc.numArcs);
            in.setPosition(arc.posArcsStart);
            in.skipBytes(arc.arcIdx * arc.bytesPerArc);
        } else {
            in.setPosition(arc.nextArc);
        }
        arc.flags = in.readByte();
        arc.label = this.readLabel(in);
        arc.output = arc.flag(16) ? this.outputs.read(in) : this.outputs.getNoOutput();
        arc.nextFinalOutput = arc.flag(32) ? this.outputs.readFinalOutput(in) : this.outputs.getNoOutput();
        if (arc.flag(8)) {
            arc.target = arc.flag(1) ? -1L : 0L;
            arc.nextArc = in.getPosition();
        } else if (arc.flag(4)) {
            arc.nextArc = in.getPosition();
            if (this.nodeAddress == null) {
                if (!arc.flag(2)) {
                    if (arc.bytesPerArc == 0) {
                        this.seekToNextNode(in);
                    } else {
                        in.setPosition(arc.posArcsStart);
                        in.skipBytes(arc.bytesPerArc * arc.numArcs);
                    }
                }
                arc.target = in.getPosition();
            } else {
                arc.target = arc.node - 1L;
                assert (arc.target > 0L);
            }
        } else {
            if (this.packed) {
                long pos = in.getPosition();
                long code = in.readVLong();
                arc.target = arc.flag(64) ? pos + code : (code < (long)this.nodeRefToAddress.size() ? this.nodeRefToAddress.get((int)code) : code);
            } else {
                arc.target = this.readUnpackedNodeTarget(in);
            }
            arc.nextArc = in.getPosition();
        }
        return arc;
    }

    public Arc<T> findTargetArc(int labelToMatch, Arc<T> follow, Arc<T> arc, BytesReader in) throws IOException {
        assert (this.cachedRootArcs != null);
        if (labelToMatch == -1) {
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
        in.setPosition(this.getNodeAddress(follow.target));
        arc.node = follow.target;
        if (in.readByte() == 32) {
            arc.numArcs = in.readVInt();
            arc.bytesPerArc = this.packed || this.version >= 4 ? in.readVInt() : in.readInt();
            arc.posArcsStart = in.getPosition();
            int low = 0;
            int high = arc.numArcs - 1;
            while (low <= high) {
                int mid = low + high >>> 1;
                in.setPosition(arc.posArcsStart);
                in.skipBytes(arc.bytesPerArc * mid + 1);
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
                this.outputs.readFinalOutput(in);
            }
            if (FST.flag(flags, 8) || FST.flag(flags, 4)) continue;
            if (this.packed) {
                in.readVLong();
                continue;
            }
            this.readUnpackedNodeTarget(in);
        } while (!FST.flag(flags, 2));
    }

    public long getNodeCount() {
        return 1L + this.nodeCount;
    }

    public long getArcCount() {
        return this.arcCount;
    }

    public long getArcWithOutputCount() {
        return this.arcWithOutputCount;
    }

    private boolean shouldExpand(Builder.UnCompiledNode<T> node) {
        return this.allowArrayArcs && (node.depth <= 3 && node.numArcs >= 5 || node.numArcs >= 10);
    }

    public BytesReader getBytesReader() {
        BytesReader in = this.packed ? this.bytes.getForwardReader() : this.bytes.getReverseReader();
        return in;
    }

    private FST(INPUT_TYPE inputType, Outputs<T> outputs, int bytesPageBits) {
        this.version = 4;
        this.packed = true;
        this.inputType = inputType;
        this.bytes = new BytesStore(bytesPageBits);
        this.outputs = outputs;
        this.NO_OUTPUT = outputs.getNoOutput();
        this.allowArrayArcs = false;
    }

    FST<T> pack(int minInCountDeref, int maxDerefNodes, float acceptableOverheadRatio) throws IOException {
        Object writer;
        FST<T> fst;
        boolean negDelta;
        boolean changed;
        if (this.nodeAddress == null) {
            throw new IllegalArgumentException("this FST was not built with willPackFST=true");
        }
        Arc arc = new Arc();
        BytesReader r = this.getBytesReader();
        int topN = Math.min(maxDerefNodes, this.inCounts.size());
        NodeQueue q = new NodeQueue(topN);
        NodeAndInCount bottom = null;
        for (int node = 0; node < this.inCounts.size(); ++node) {
            if (this.inCounts.get(node) < (long)minInCountDeref) continue;
            if (bottom == null) {
                q.add(new NodeAndInCount(node, (int)this.inCounts.get(node)));
                if (q.size() != topN) continue;
                bottom = (NodeAndInCount)q.top();
                continue;
            }
            if (this.inCounts.get(node) <= (long)bottom.count) continue;
            q.insertWithOverflow(new NodeAndInCount(node, (int)this.inCounts.get(node)));
        }
        this.inCounts = null;
        HashMap<Integer, Integer> topNodeMap = new HashMap<Integer, Integer>();
        for (int downTo = q.size() - 1; downTo >= 0; --downTo) {
            NodeAndInCount n = (NodeAndInCount)q.pop();
            topNodeMap.put(n.node, downTo);
        }
        GrowableWriter newNodeAddress = new GrowableWriter(PackedInts.bitsRequired(this.bytes.getPosition()), (int)(1L + this.nodeCount), acceptableOverheadRatio);
        int node = 1;
        while ((long)node <= this.nodeCount) {
            newNodeAddress.set(node, 1L + this.bytes.getPosition() - this.nodeAddress.get(node));
            ++node;
        }
        do {
            changed = false;
            negDelta = false;
            fst = new FST<T>(this.inputType, this.outputs, this.bytes.getBlockBits());
            writer = fst.bytes;
            ((BytesStore)writer).writeByte((byte)0);
            fst.arcWithOutputCount = 0L;
            fst.nodeCount = 0L;
            fst.arcCount = 0L;
            int nextCount = 0;
            int topCount = 0;
            int deltaCount = 0;
            int absCount = 0;
            int changedCount = 0;
            long addressError = 0L;
            for (int node2 = (int)this.nodeCount; node2 >= 1; --node2) {
                ++fst.nodeCount;
                long address = ((BytesStore)writer).getPosition();
                if (address != newNodeAddress.get(node2)) {
                    addressError = address - newNodeAddress.get(node2);
                    changed = true;
                    newNodeAddress.set(node2, address);
                    ++changedCount;
                }
                int nodeArcCount = 0;
                int bytesPerArc = 0;
                boolean retry = false;
                boolean anyNegDelta = false;
                while (true) {
                    boolean useArcArray;
                    this.readFirstRealTargetArc(node2, arc, r);
                    boolean bl = useArcArray = arc.bytesPerArc != 0;
                    if (useArcArray) {
                        if (bytesPerArc == 0) {
                            bytesPerArc = arc.bytesPerArc;
                        }
                        ((BytesStore)writer).writeByte((byte)32);
                        ((DataOutput)writer).writeVInt(arc.numArcs);
                        ((DataOutput)writer).writeVInt(bytesPerArc);
                    }
                    int maxBytesPerArc = 0;
                    while (true) {
                        long absPtr;
                        boolean doWriteTarget;
                        long arcStartPos = ((BytesStore)writer).getPosition();
                        ++nodeArcCount;
                        byte flags = 0;
                        if (arc.isLast()) {
                            flags = (byte)(flags + 2);
                        }
                        if (!useArcArray && node2 != 1 && arc.target == (long)(node2 - 1)) {
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
                            Integer ptr = (Integer)topNodeMap.get(arc.target);
                            absPtr = ptr != null ? (long)ptr.intValue() : (long)topNodeMap.size() + newNodeAddress.get((int)arc.target) + addressError;
                            long delta = newNodeAddress.get((int)arc.target) + addressError - ((BytesStore)writer).getPosition() - 2L;
                            if (delta < 0L) {
                                anyNegDelta = true;
                                delta = 0L;
                            }
                            if (delta < absPtr) {
                                flags = (byte)(flags | 0x40);
                            }
                        } else {
                            absPtr = 0L;
                        }
                        assert (flags != 32);
                        ((BytesStore)writer).writeByte(flags);
                        super.writeLabel((DataOutput)writer, arc.label);
                        if (arc.output != this.NO_OUTPUT) {
                            this.outputs.write(arc.output, (DataOutput)writer);
                            if (!retry) {
                                ++fst.arcWithOutputCount;
                            }
                        }
                        if (arc.nextFinalOutput != this.NO_OUTPUT) {
                            this.outputs.writeFinalOutput(arc.nextFinalOutput, (DataOutput)writer);
                        }
                        if (doWriteTarget) {
                            long delta = newNodeAddress.get((int)arc.target) + addressError - ((BytesStore)writer).getPosition();
                            if (delta < 0L) {
                                anyNegDelta = true;
                                delta = 0L;
                            }
                            if (FST.flag(flags, 64)) {
                                ((DataOutput)writer).writeVLong(delta);
                                if (!retry) {
                                    ++deltaCount;
                                }
                            } else {
                                ((DataOutput)writer).writeVLong(absPtr);
                                if (!retry) {
                                    if (absPtr >= (long)topNodeMap.size()) {
                                        ++absCount;
                                    } else {
                                        ++topCount;
                                    }
                                }
                            }
                        }
                        if (useArcArray) {
                            int arcBytes = (int)(((BytesStore)writer).getPosition() - arcStartPos);
                            maxBytesPerArc = Math.max(maxBytesPerArc, arcBytes);
                            ((BytesStore)writer).skipBytes((int)(arcStartPos + (long)bytesPerArc - ((BytesStore)writer).getPosition()));
                        }
                        if (arc.isLast()) break;
                        this.readNextRealArc(arc, r);
                    }
                    if (!useArcArray || maxBytesPerArc == bytesPerArc || retry && maxBytesPerArc <= bytesPerArc) break;
                    bytesPerArc = maxBytesPerArc;
                    ((BytesStore)writer).truncate(address);
                    nodeArcCount = 0;
                    retry = true;
                    anyNegDelta = false;
                }
                negDelta |= anyNegDelta;
                fst.arcCount += (long)nodeArcCount;
            }
        } while (changed);
        assert (!negDelta);
        long maxAddress = 0L;
        writer = topNodeMap.keySet().iterator();
        while (writer.hasNext()) {
            long key = ((Integer)writer.next()).intValue();
            maxAddress = Math.max(maxAddress, newNodeAddress.get((int)key));
        }
        PackedInts.Mutable nodeRefToAddressIn = PackedInts.getMutable(topNodeMap.size(), PackedInts.bitsRequired(maxAddress), acceptableOverheadRatio);
        for (Map.Entry ent : topNodeMap.entrySet()) {
            nodeRefToAddressIn.set((Integer)ent.getValue(), newNodeAddress.get((Integer)ent.getKey()));
        }
        fst.nodeRefToAddress = nodeRefToAddressIn;
        fst.startNode = newNodeAddress.get((int)this.startNode);
        if (this.emptyOutput != null) {
            fst.setEmptyOutput(this.emptyOutput);
        }
        assert (fst.nodeCount == this.nodeCount) : "fst.nodeCount=" + fst.nodeCount + " nodeCount=" + this.nodeCount;
        assert (fst.arcCount == this.arcCount);
        assert (fst.arcWithOutputCount == this.arcWithOutputCount) : "fst.arcWithOutputCount=" + fst.arcWithOutputCount + " arcWithOutputCount=" + this.arcWithOutputCount;
        fst.bytes.finish();
        super.cacheRootArcs();
        return fst;
    }

    private static class NodeQueue
    extends PriorityQueue<NodeAndInCount> {
        public NodeQueue(int topN) {
            super(topN, false);
        }

        @Override
        public boolean lessThan(NodeAndInCount a, NodeAndInCount b) {
            int cmp = a.compareTo(b);
            assert (cmp != 0);
            return cmp < 0;
        }
    }

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

    private static class ArcAndState<T> {
        final Arc<T> arc;
        final IntsRef chain;

        public ArcAndState(Arc<T> arc, IntsRef chain) {
            this.arc = arc;
            this.chain = chain;
        }
    }

    public static abstract class BytesReader
    extends DataInput {
        public abstract long getPosition();

        public abstract void setPosition(long var1);

        public abstract boolean reversed();

        public abstract void skipBytes(int var1);
    }

    public static final class Arc<T> {
        public int label;
        public T output;
        long node;
        public long target;
        byte flags;
        public T nextFinalOutput;
        long nextArc;
        long posArcsStart;
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

    public static enum INPUT_TYPE {
        BYTE1,
        BYTE2,
        BYTE4;

    }
}

