/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hwpf.model.CharIndexTranslator;
import org.apache.poi.hwpf.model.FormattedDiskPage;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.ParagraphHeight;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class PAPFormattedDiskPage
extends FormattedDiskPage {
    private static final int BX_SIZE = 13;
    private static final int FC_SIZE = 4;
    private ArrayList<PAPX> _papxList = new ArrayList();
    private ArrayList<PAPX> _overFlow;

    public PAPFormattedDiskPage() {
    }

    public PAPFormattedDiskPage(byte[] documentStream, byte[] dataStream, int offset, CharIndexTranslator translator) {
        super(documentStream, offset);
        for (int x = 0; x < this._crun; ++x) {
            int bytesStartAt = this.getStart(x);
            int bytesEndAt = this.getEnd(x);
            for (int[] range : translator.getCharIndexRanges(bytesStartAt, bytesEndAt)) {
                PAPX papx = new PAPX(range[0], range[1], this.getGrpprl(x), this.getParagraphHeight(x), dataStream);
                this._papxList.add(papx);
            }
        }
        this._fkp = null;
    }

    public void fill(List<PAPX> filler) {
        this._papxList.addAll(filler);
    }

    ArrayList<PAPX> getOverflow() {
        return this._overFlow;
    }

    public PAPX getPAPX(int index) {
        return this._papxList.get(index);
    }

    public List<PAPX> getPAPXs() {
        return Collections.unmodifiableList(this._papxList);
    }

    @Override
    protected byte[] getGrpprl(int index) {
        int papxOffset = 2 * LittleEndian.getUByte(this._fkp, this._offset + ((this._crun + 1) * 4 + index * 13));
        int size = 2 * LittleEndian.getUByte(this._fkp, this._offset + papxOffset);
        size = size == 0 ? 2 * LittleEndian.getUByte(this._fkp, this._offset + ++papxOffset) : --size;
        return IOUtils.safelyClone(this._fkp, this._offset + papxOffset + 1, size, 512);
    }

    protected byte[] toByteArray(ByteArrayOutputStream dataStream, CharIndexTranslator translator) throws IOException {
        int index;
        byte[] buf = new byte[512];
        int size = this._papxList.size();
        int grpprlOffset = 0;
        int bxOffset = 0;
        int fcOffset = 0;
        byte[] lastGrpprl = new byte[]{};
        int totalSize = 4;
        for (index = 0; index < size; ++index) {
            byte[] grpprl = this._papxList.get(index).getGrpprl();
            int grpprlLength = grpprl.length;
            if (grpprlLength > 488) {
                grpprlLength = 8;
            }
            int addition = 0;
            addition = !Arrays.equals(grpprl, lastGrpprl) ? 17 + grpprlLength + 1 : 17;
            if ((totalSize += addition) > 511 + index % 2) {
                totalSize -= addition;
                break;
            }
            totalSize = grpprlLength % 2 > 0 ? ++totalSize : (totalSize += 2);
            lastGrpprl = grpprl;
        }
        if (index != size) {
            this._overFlow = new ArrayList();
            this._overFlow.addAll(this._papxList.subList(index, size));
        }
        buf[511] = (byte)index;
        bxOffset = 4 * index + 4;
        grpprlOffset = 511;
        PAPX papx = null;
        lastGrpprl = new byte[]{};
        for (int x = 0; x < index; ++x) {
            boolean same;
            papx = this._papxList.get(x);
            byte[] phe = papx.getParagraphHeight().toByteArray();
            byte[] grpprl = papx.getGrpprl();
            if (grpprl.length > 488) {
                byte[] hugePapx = Arrays.copyOfRange(grpprl, 2, grpprl.length);
                int dataStreamOffset = dataStream.size();
                dataStream.write(hugePapx);
                int istd = LittleEndian.getUShort(grpprl, 0);
                grpprl = new byte[8];
                LittleEndian.putUShort(grpprl, 0, istd);
                LittleEndian.putUShort(grpprl, 2, 26182);
                LittleEndian.putInt(grpprl, 4, dataStreamOffset);
            }
            if (!(same = Arrays.equals(lastGrpprl, grpprl))) {
                grpprlOffset -= grpprl.length + (2 - grpprl.length % 2);
                grpprlOffset -= grpprlOffset % 2;
            }
            LittleEndian.putInt(buf, fcOffset, translator.getByteIndex(papx.getStart()));
            buf[bxOffset] = (byte)(grpprlOffset / 2);
            System.arraycopy(phe, 0, buf, bxOffset + 1, phe.length);
            if (!same) {
                int copyOffset = grpprlOffset;
                if (grpprl.length % 2 > 0) {
                    buf[copyOffset++] = (byte)((grpprl.length + 1) / 2);
                } else {
                    buf[++copyOffset] = (byte)(grpprl.length / 2);
                    ++copyOffset;
                }
                System.arraycopy(grpprl, 0, buf, copyOffset, grpprl.length);
                lastGrpprl = grpprl;
            }
            bxOffset += 13;
            fcOffset += 4;
        }
        if (papx != null) {
            LittleEndian.putInt(buf, fcOffset, translator.getByteIndex(papx.getEnd()));
        }
        return buf;
    }

    private ParagraphHeight getParagraphHeight(int index) {
        int pheOffset = this._offset + 1 + ((this._crun + 1) * 4 + index * 13);
        return new ParagraphHeight(this._fkp, pheOffset);
    }
}

