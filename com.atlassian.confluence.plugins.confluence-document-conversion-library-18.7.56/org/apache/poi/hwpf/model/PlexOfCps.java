/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

public final class PlexOfCps {
    private static final int MAX_RECORD_LENGTH = 0xA00000;
    private static final int MAX_NUMBER_OF_PROPERTIES = 100000;
    private int _iMac;
    private final int _cbStruct;
    private final List<GenericPropertyNode> _props;

    public PlexOfCps(int sizeOfStruct) {
        this._props = new ArrayList<GenericPropertyNode>();
        this._cbStruct = sizeOfStruct;
    }

    public PlexOfCps(byte[] buf, int start, int cb, int cbStruct) {
        this._iMac = (cb - 4) / (4 + cbStruct);
        IOUtils.safelyAllocateCheck(this._iMac, 100000);
        this._cbStruct = cbStruct;
        this._props = new ArrayList<GenericPropertyNode>(this._iMac);
        for (int x = 0; x < this._iMac; ++x) {
            this._props.add(this.getProperty(x, buf, start));
        }
    }

    @Internal
    void adjust(int startCp, int shift) {
        for (GenericPropertyNode node : this._props) {
            if (node.getStart() > startCp) {
                node.setStart(Math.max(node.getStart() + shift, startCp));
            }
            if (node.getEnd() < startCp) continue;
            node.setEnd(Math.max(node.getEnd() + shift, startCp));
        }
    }

    public GenericPropertyNode getProperty(int index) {
        return this._props.get(index);
    }

    public void addProperty(GenericPropertyNode node) {
        this._props.add(node);
        ++this._iMac;
    }

    void remove(int index) {
        this._props.remove(index);
        --this._iMac;
    }

    public byte[] toByteArray() {
        int size = this._props.size();
        int cpBufSize = (size + 1) * 4;
        int structBufSize = this._cbStruct * size;
        int bufSize = cpBufSize + structBufSize;
        byte[] buf = IOUtils.safelyAllocate(bufSize, 0xA00000);
        int nodeEnd = 0;
        for (int x = 0; x < size; ++x) {
            GenericPropertyNode node = this._props.get(x);
            nodeEnd = node.getEnd();
            LittleEndian.putInt(buf, 4 * x, node.getStart());
            System.arraycopy(node.getBytes(), 0, buf, cpBufSize + x * this._cbStruct, this._cbStruct);
        }
        LittleEndian.putInt(buf, 4 * size, nodeEnd);
        return buf;
    }

    private GenericPropertyNode getProperty(int index, byte[] buf, int offset) {
        int start = LittleEndian.getInt(buf, offset + this.getIntOffset(index));
        int end = LittleEndian.getInt(buf, offset + this.getIntOffset(index + 1));
        byte[] struct = IOUtils.safelyClone(buf, offset + this.getStructOffset(index), this._cbStruct, 0xA00000);
        return new GenericPropertyNode(start, end, struct);
    }

    private int getIntOffset(int index) {
        return index * 4;
    }

    public int length() {
        return this._iMac;
    }

    private int getStructOffset(int index) {
        return 4 * (this._iMac + 1) + this._cbStruct * index;
    }

    GenericPropertyNode[] toPropertiesArray() {
        if (this._props == null || this._props.isEmpty()) {
            return new GenericPropertyNode[0];
        }
        return this._props.toArray(new GenericPropertyNode[0]);
    }

    public String toString() {
        return "PLCF (cbStruct: " + this._cbStruct + "; iMac: " + this._iMac + ")";
    }
}

