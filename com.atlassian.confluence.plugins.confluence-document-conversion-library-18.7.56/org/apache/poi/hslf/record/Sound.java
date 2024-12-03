/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SoundData;

public final class Sound
extends RecordContainer {
    private byte[] _header;
    private CString _name;
    private CString _type;
    private SoundData _data;

    protected Sound(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        Record child = this._children[0];
        if (child instanceof CString) {
            this._name = (CString)child;
        } else {
            LOG.atError().log("First child record wasn't a CString, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        child = this._children[1];
        if (child instanceof CString) {
            this._type = (CString)child;
        } else {
            LOG.atError().log("Second child record wasn't a CString, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        for (int i = 2; i < this._children.length; ++i) {
            if (!(this._children[i] instanceof SoundData)) continue;
            this._data = (SoundData)this._children[i];
            break;
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.Sound.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    public String getSoundName() {
        return this._name.getText();
    }

    public String getSoundType() {
        return this._type.getText();
    }

    public byte[] getSoundData() {
        return this._data == null ? null : this._data.getData();
    }
}

