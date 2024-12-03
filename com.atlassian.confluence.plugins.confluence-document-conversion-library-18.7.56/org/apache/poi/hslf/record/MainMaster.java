/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.SheetContainer;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.record.TxMasterStyleAtom;

public final class MainMaster
extends SheetContainer {
    private byte[] _header;
    private static long _type = 1016L;
    private SlideAtom slideAtom;
    private PPDrawing ppDrawing;
    private TxMasterStyleAtom[] txmasters;
    private ColorSchemeAtom[] clrscheme;
    private ColorSchemeAtom _colorScheme;

    public SlideAtom getSlideAtom() {
        return this.slideAtom;
    }

    @Override
    public PPDrawing getPPDrawing() {
        return this.ppDrawing;
    }

    public TxMasterStyleAtom[] getTxMasterStyleAtoms() {
        return this.txmasters;
    }

    public ColorSchemeAtom[] getColorSchemeAtoms() {
        return this.clrscheme;
    }

    protected MainMaster(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        ArrayList<TxMasterStyleAtom> tx = new ArrayList<TxMasterStyleAtom>();
        ArrayList<ColorSchemeAtom> clr = new ArrayList<ColorSchemeAtom>();
        for (Record child : this._children) {
            if (child instanceof SlideAtom) {
                this.slideAtom = (SlideAtom)child;
            } else if (child instanceof PPDrawing) {
                this.ppDrawing = (PPDrawing)child;
            } else if (child instanceof TxMasterStyleAtom) {
                tx.add((TxMasterStyleAtom)child);
            } else if (child instanceof ColorSchemeAtom) {
                clr.add((ColorSchemeAtom)child);
            }
            if (this.ppDrawing == null || !(child instanceof ColorSchemeAtom)) continue;
            this._colorScheme = (ColorSchemeAtom)child;
        }
        this.txmasters = tx.toArray(new TxMasterStyleAtom[0]);
        this.clrscheme = clr.toArray(new ColorSchemeAtom[0]);
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }

    @Override
    public ColorSchemeAtom getColorScheme() {
        return this._colorScheme;
    }
}

