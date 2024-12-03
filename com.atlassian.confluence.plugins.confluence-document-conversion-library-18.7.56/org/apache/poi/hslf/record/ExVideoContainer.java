/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ExMediaAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class ExVideoContainer
extends RecordContainer {
    private byte[] _header;
    private ExMediaAtom mediaAtom;
    private CString pathAtom;

    protected ExVideoContainer(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        Record child = this._children[0];
        if (child instanceof ExMediaAtom) {
            this.mediaAtom = (ExMediaAtom)child;
        } else {
            LOG.atError().log("First child record wasn't a ExMediaAtom, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        child = this._children[1];
        if (child instanceof CString) {
            this.pathAtom = (CString)child;
        } else {
            LOG.atError().log("Second child record wasn't a CString, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
    }

    public ExVideoContainer() {
        this._header = new byte[8];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        this._children = new Record[2];
        this.mediaAtom = new ExMediaAtom();
        this._children[0] = this.mediaAtom;
        this.pathAtom = new CString();
        this._children[1] = this.pathAtom;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExVideoContainer.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    public ExMediaAtom getExMediaAtom() {
        return this.mediaAtom;
    }

    public CString getPathAtom() {
        return this.pathAtom;
    }
}

