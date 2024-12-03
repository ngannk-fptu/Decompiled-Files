/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hslf.record.FontCollection;
import org.apache.poi.hslf.record.PositionDependentRecordContainer;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.TxMasterStyleAtom;

public final class Environment
extends PositionDependentRecordContainer {
    private byte[] _header;
    private static long _type = 1010L;
    private FontCollection fontCollection;
    private TxMasterStyleAtom txmaster;

    public FontCollection getFontCollection() {
        return this.fontCollection;
    }

    protected Environment(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        for (Record child : this._children = Record.findChildRecords(source, start + 8, len - 8)) {
            if (child instanceof FontCollection) {
                this.fontCollection = (FontCollection)child;
                continue;
            }
            if (!(child instanceof TxMasterStyleAtom)) continue;
            this.txmaster = (TxMasterStyleAtom)child;
        }
        if (this.fontCollection == null) {
            throw new IllegalStateException("Environment didn't contain a FontCollection record!");
        }
    }

    public TxMasterStyleAtom getTxMasterStyleAtom() {
        return this.txmaster;
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

