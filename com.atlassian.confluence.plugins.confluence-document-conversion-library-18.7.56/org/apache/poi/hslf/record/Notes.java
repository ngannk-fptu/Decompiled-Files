/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hslf.record.ColorSchemeAtom;
import org.apache.poi.hslf.record.NotesAtom;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.SheetContainer;

public final class Notes
extends SheetContainer {
    private byte[] _header;
    private static long _type = 1008L;
    private NotesAtom notesAtom;
    private PPDrawing ppDrawing;
    private ColorSchemeAtom _colorScheme;

    public NotesAtom getNotesAtom() {
        return this.notesAtom;
    }

    @Override
    public PPDrawing getPPDrawing() {
        return this.ppDrawing;
    }

    protected Notes(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        for (Record child : this._children = Record.findChildRecords(source, start + 8, len - 8)) {
            if (child instanceof NotesAtom) {
                this.notesAtom = (NotesAtom)child;
            }
            if (child instanceof PPDrawing) {
                this.ppDrawing = (PPDrawing)child;
            }
            if (this.ppDrawing == null || !(child instanceof ColorSchemeAtom)) continue;
            this._colorScheme = (ColorSchemeAtom)child;
        }
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

