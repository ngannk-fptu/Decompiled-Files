/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.record.ParentAwareRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.util.ArrayUtil;
import org.apache.poi.util.LittleEndian;

public abstract class RecordContainer
extends Record {
    protected Record[] _children;

    @Override
    public Record[] getChildRecords() {
        return this._children;
    }

    @Override
    public boolean isAnAtom() {
        return false;
    }

    private int findChildLocation(Record child) {
        int i = 0;
        for (Record r : this._children) {
            if (r.equals(child)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private int appendChild(Record newChild) {
        Record[] nc = (Record[])Arrays.copyOf(this._children, this._children.length + 1, Record[].class);
        nc[this._children.length] = newChild;
        this._children = nc;
        return this._children.length;
    }

    private void addChildAt(Record newChild, int position) {
        this.appendChild(newChild);
        this.moveChildRecords(this._children.length - 1, position, 1);
    }

    private void moveChildRecords(int oldLoc, int newLoc, int number) {
        if (oldLoc == newLoc) {
            return;
        }
        if (number == 0) {
            return;
        }
        if (oldLoc + number > this._children.length) {
            throw new IllegalArgumentException("Asked to move more records than there are!");
        }
        ArrayUtil.arrayMoveWithin(this._children, oldLoc, newLoc, number);
    }

    public Record findFirstOfType(long type) {
        for (Record r : this._children) {
            if (r.getRecordType() != type) continue;
            return r;
        }
        return null;
    }

    public Record removeChild(Record ch) {
        Record rm = null;
        ArrayList<Record> lst = new ArrayList<Record>();
        for (Record r : this._children) {
            if (r != ch) {
                lst.add(r);
                continue;
            }
            rm = r;
        }
        this._children = lst.toArray(new Record[0]);
        return rm;
    }

    public int appendChildRecord(Record newChild) {
        return this.appendChild(newChild);
    }

    public int addChildAfter(Record newChild, Record after) {
        int loc = this.findChildLocation(after);
        if (loc == -1) {
            throw new IllegalArgumentException("Asked to add a new child after another record, but that record wasn't one of our children!");
        }
        this.addChildAt(newChild, loc + 1);
        return loc + 1;
    }

    public int addChildBefore(Record newChild, Record before) {
        int loc = this.findChildLocation(before);
        if (loc == -1) {
            throw new IllegalArgumentException("Asked to add a new child before another record, but that record wasn't one of our children!");
        }
        this.addChildAt(newChild, loc);
        return loc;
    }

    public void setChildRecord(Record[] records) {
        this._children = (Record[])records.clone();
    }

    public void writeOut(byte headerA, byte headerB, long type, Record[] children, OutputStream out) throws IOException {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            baos.write(new byte[]{headerA, headerB});
            byte[] typeB = new byte[2];
            LittleEndian.putShort(typeB, 0, (short)type);
            baos.write(typeB);
            baos.write(new byte[]{0, 0, 0, 0});
            for (Record aChildren : children) {
                aChildren.writeOut((OutputStream)baos);
            }
            byte[] toWrite = baos.toByteArray();
            LittleEndian.putInt(toWrite, 4, toWrite.length - 8);
            out.write(toWrite);
        }
    }

    public static void handleParentAwareRecords(RecordContainer br) {
        for (Record record : br.getChildRecords()) {
            if (record instanceof ParentAwareRecord) {
                ((ParentAwareRecord)((Object)record)).setParentRecord(br);
            }
            if (!(record instanceof RecordContainer)) continue;
            RecordContainer.handleParentAwareRecords((RecordContainer)record);
        }
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

