/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hslf.record.ExHyperlink;
import org.apache.poi.hslf.record.ExObjListAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public class ExObjList
extends RecordContainer {
    private byte[] _header;
    private static final long _type = RecordTypes.ExObjList.typeID;
    private ExObjListAtom exObjListAtom;

    public ExObjListAtom getExObjListAtom() {
        return this.exObjListAtom;
    }

    public ExHyperlink[] getExHyperlinks() {
        ArrayList<ExHyperlink> links = new ArrayList<ExHyperlink>();
        for (Record child : this._children) {
            if (!(child instanceof ExHyperlink)) continue;
            links.add((ExHyperlink)child);
        }
        return links.toArray(new ExHyperlink[0]);
    }

    protected ExObjList(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        if (!(this._children[0] instanceof ExObjListAtom)) {
            throw new IllegalStateException("First child record wasn't a ExObjListAtom, was of type " + this._children[0].getRecordType());
        }
        this.exObjListAtom = (ExObjListAtom)this._children[0];
    }

    public ExObjList() {
        this._header = new byte[8];
        this._children = new Record[1];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)_type);
        this._children[0] = new ExObjListAtom();
        this.findInterestingChildren();
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }

    public ExHyperlink get(int id) {
        for (Record child : this._children) {
            ExHyperlink rec;
            if (!(child instanceof ExHyperlink) || (rec = (ExHyperlink)child).getExHyperlinkAtom().getNumber() != id) continue;
            return rec;
        }
        return null;
    }
}

