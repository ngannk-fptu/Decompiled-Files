/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.SheetContainer;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.util.LittleEndian;

public final class Slide
extends SheetContainer {
    private byte[] _header;
    private static long _type = 1006L;
    private SlideAtom slideAtom;
    private PPDrawing ppDrawing;
    private ColorSchemeAtom _colorScheme;

    public SlideAtom getSlideAtom() {
        return this.slideAtom;
    }

    @Override
    public PPDrawing getPPDrawing() {
        return this.ppDrawing;
    }

    protected Slide(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        for (Record child : this._children = Record.findChildRecords(source, start + 8, len - 8)) {
            if (child instanceof SlideAtom) {
                this.slideAtom = (SlideAtom)child;
            } else if (child instanceof PPDrawing) {
                this.ppDrawing = (PPDrawing)child;
            }
            if (this.ppDrawing == null || !(child instanceof ColorSchemeAtom)) continue;
            this._colorScheme = (ColorSchemeAtom)child;
        }
    }

    public Slide() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 15);
        LittleEndian.putUShort(this._header, 2, (int)_type);
        LittleEndian.putInt(this._header, 4, 0);
        this.slideAtom = new SlideAtom();
        this.ppDrawing = new PPDrawing();
        ColorSchemeAtom colorAtom = new ColorSchemeAtom();
        this._children = new Record[]{this.slideAtom, this.ppDrawing, colorAtom};
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

