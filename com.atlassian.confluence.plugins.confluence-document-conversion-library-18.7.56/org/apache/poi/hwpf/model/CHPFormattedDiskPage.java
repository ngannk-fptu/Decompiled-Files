/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.CharIndexTranslator;
import org.apache.poi.hwpf.model.FormattedDiskPage;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

@Internal
public final class CHPFormattedDiskPage
extends FormattedDiskPage {
    private static final int FC_SIZE = 4;
    private ArrayList<CHPX> _chpxList = new ArrayList();
    private ArrayList<CHPX> _overFlow;

    public CHPFormattedDiskPage() {
    }

    public CHPFormattedDiskPage(byte[] documentStream, int offset, CharIndexTranslator translator) {
        super(documentStream, offset);
        for (int x = 0; x < this._crun; ++x) {
            int bytesStartAt = this.getStart(x);
            int bytesEndAt = this.getEnd(x);
            for (int[] range : translator.getCharIndexRanges(bytesStartAt, bytesEndAt)) {
                CHPX chpx = new CHPX(range[0], range[1], new SprmBuffer(this.getGrpprl(x), 0));
                this._chpxList.add(chpx);
            }
        }
    }

    public CHPX getCHPX(int index) {
        return this._chpxList.get(index);
    }

    public List<CHPX> getCHPXs() {
        return Collections.unmodifiableList(this._chpxList);
    }

    public void fill(List<CHPX> filler) {
        this._chpxList.addAll(filler);
    }

    public ArrayList<CHPX> getOverflow() {
        return this._overFlow;
    }

    @Override
    protected byte[] getGrpprl(int index) {
        int chpxOffset = 2 * LittleEndian.getUByte(this._fkp, this._offset + ((this._crun + 1) * 4 + index));
        if (chpxOffset == 0) {
            return new byte[0];
        }
        short size = LittleEndian.getUByte(this._fkp, this._offset + chpxOffset);
        return IOUtils.safelyClone(this._fkp, this._offset + chpxOffset + 1, size, HWPFDocument.getMaxRecordLength());
    }

    protected byte[] toByteArray(CharIndexTranslator translator) {
        int index;
        byte[] buf = new byte[512];
        int size = this._chpxList.size();
        int grpprlOffset = 511;
        int offsetOffset = 0;
        int fcOffset = 0;
        int totalSize = 6;
        for (index = 0; index < size; ++index) {
            int grpprlLength = this._chpxList.get(index).getGrpprl().length;
            if ((totalSize += 6 + grpprlLength) > 511 + index % 2) {
                totalSize -= 6 + grpprlLength;
                break;
            }
            if ((1 + grpprlLength) % 2 <= 0) continue;
            ++totalSize;
        }
        if (index == 0) {
            throw new RecordFormatException("empty grpprl entry.");
        }
        if (index != size) {
            this._overFlow = new ArrayList();
            this._overFlow.addAll(this._chpxList.subList(index, size));
        }
        buf[511] = (byte)index;
        offsetOffset = 4 * index + 4;
        int chpxEnd = 0;
        for (CHPX chpx : this._chpxList.subList(0, index)) {
            int chpxStart = translator.getByteIndex(chpx.getStart());
            chpxEnd = translator.getByteIndex(chpx.getEnd());
            LittleEndian.putInt(buf, fcOffset, chpxStart);
            byte[] grpprl = chpx.getGrpprl();
            grpprlOffset -= 1 + grpprl.length;
            grpprlOffset -= grpprlOffset % 2;
            buf[offsetOffset] = (byte)(grpprlOffset / 2);
            buf[grpprlOffset] = (byte)grpprl.length;
            System.arraycopy(grpprl, 0, buf, grpprlOffset + 1, grpprl.length);
            ++offsetOffset;
            fcOffset += 4;
        }
        LittleEndian.putInt(buf, fcOffset, chpxEnd);
        return buf;
    }
}

