/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hslf.record.InteractiveInfoAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public class InteractiveInfo
extends RecordContainer {
    private byte[] _header;
    private static final long _type = RecordTypes.InteractiveInfo.typeID;
    private InteractiveInfoAtom infoAtom;

    public InteractiveInfoAtom getInteractiveInfoAtom() {
        return this.infoAtom;
    }

    protected InteractiveInfo(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        if (this._children == null || this._children.length == 0 || !(this._children[0] instanceof InteractiveInfoAtom)) {
            LOG.atWarn().log("First child record wasn't a InteractiveInfoAtom - leaving this atom in an invalid state...");
            return;
        }
        this.infoAtom = (InteractiveInfoAtom)this._children[0];
    }

    public InteractiveInfo() {
        this._header = new byte[8];
        this._children = new Record[1];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)_type);
        this.infoAtom = new InteractiveInfoAtom();
        this._children[0] = this.infoAtom;
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }
}

