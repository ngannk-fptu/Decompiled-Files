/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.util.LittleEndian;

public final class SlideListWithText
extends RecordContainer {
    private static final long _type = RecordTypes.SlideListWithText.typeID;
    public static final int SLIDES = 0;
    public static final int MASTER = 1;
    public static final int NOTES = 2;
    private final byte[] _header;
    private SlideAtomsSet[] slideAtomsSets;

    protected SlideListWithText(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        ArrayList<SlideAtomsSet> sets = new ArrayList<SlideAtomsSet>();
        for (int i = 0; i < this._children.length; ++i) {
            int endPos;
            if (!(this._children[i] instanceof SlidePersistAtom)) continue;
            for (endPos = i + 1; endPos < this._children.length && !(this._children[endPos] instanceof SlidePersistAtom); ++endPos) {
            }
            int clen = endPos - i - 1;
            Record[] spaChildren = (Record[])Arrays.copyOfRange(this._children, i + 1, i + 1 + clen, Record[].class);
            SlideAtomsSet set = new SlideAtomsSet((SlidePersistAtom)this._children[i], spaChildren);
            sets.add(set);
            i += clen;
        }
        this.slideAtomsSets = sets.toArray(new SlideAtomsSet[0]);
    }

    public SlideListWithText() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 15);
        LittleEndian.putUShort(this._header, 2, (int)_type);
        LittleEndian.putInt(this._header, 4, 0);
        this._children = new Record[0];
        this.slideAtomsSets = new SlideAtomsSet[0];
    }

    public void addSlidePersistAtom(SlidePersistAtom spa) {
        this.appendChildRecord(spa);
        SlideAtomsSet newSAS = new SlideAtomsSet(spa, new Record[0]);
        SlideAtomsSet[] sas = new SlideAtomsSet[this.slideAtomsSets.length + 1];
        System.arraycopy(this.slideAtomsSets, 0, sas, 0, this.slideAtomsSets.length);
        sas[sas.length - 1] = newSAS;
        this.slideAtomsSets = sas;
    }

    public int getInstance() {
        return LittleEndian.getShort(this._header, 0) >> 4;
    }

    public void setInstance(int inst) {
        LittleEndian.putShort(this._header, 0, (short)(inst << 4 | 0xF));
    }

    public SlideAtomsSet[] getSlideAtomsSets() {
        return this.slideAtomsSets;
    }

    public void setSlideAtomsSets(SlideAtomsSet[] sas) {
        this.slideAtomsSets = (SlideAtomsSet[])sas.clone();
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }

    public static class SlideAtomsSet {
        private final SlidePersistAtom slidePersistAtom;
        private final Record[] slideRecords;

        public SlidePersistAtom getSlidePersistAtom() {
            return this.slidePersistAtom;
        }

        public Record[] getSlideRecords() {
            return this.slideRecords;
        }

        public SlideAtomsSet(SlidePersistAtom s, Record[] r) {
            this.slidePersistAtom = s;
            this.slideRecords = (Record[])r.clone();
        }
    }
}

