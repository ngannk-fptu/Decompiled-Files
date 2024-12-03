/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.StyleTextProp9Atom;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.util.GenericRecordUtil;

public final class EscherTextboxWrapper
extends RecordContainer {
    private final EscherTextboxRecord _escherRecord;
    private long _type;
    private int shapeId;
    private StyleTextPropAtom styleTextPropAtom;
    private StyleTextProp9Atom styleTextProp9Atom;

    public EscherTextboxRecord getEscherRecord() {
        return this._escherRecord;
    }

    public EscherTextboxWrapper(EscherTextboxRecord textbox) {
        this._escherRecord = textbox;
        this._type = this._escherRecord.getRecordId();
        byte[] data = this._escherRecord.getData();
        for (Record r : this._children = Record.findChildRecords(data, 0, data.length)) {
            if (!(r instanceof StyleTextPropAtom)) continue;
            this.styleTextPropAtom = (StyleTextPropAtom)r;
        }
    }

    public EscherTextboxWrapper() {
        this._escherRecord = new EscherTextboxRecord();
        this._escherRecord.setRecordId(EscherTextboxRecord.RECORD_ID);
        this._escherRecord.setOptions((short)15);
        this._children = new Record[0];
    }

    @Override
    public long getRecordType() {
        return this._type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            for (Record r : this._children) {
                r.writeOut((OutputStream)baos);
            }
            this._escherRecord.setData(baos.toByteArray());
        }
    }

    public int getShapeId() {
        return this.shapeId;
    }

    public void setShapeId(int id) {
        this.shapeId = id;
    }

    public StyleTextPropAtom getStyleTextPropAtom() {
        return this.styleTextPropAtom;
    }

    public void setStyleTextProp9Atom(StyleTextProp9Atom nineAtom) {
        this.styleTextProp9Atom = nineAtom;
    }

    public StyleTextProp9Atom getStyleTextProp9Atom() {
        return this.styleTextProp9Atom;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("shapeId", this::getShapeId, "escherRecord", this::getEscherRecord);
    }
}

