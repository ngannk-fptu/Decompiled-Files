/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.LVLF;
import org.apache.poi.hwpf.model.Xst;
import org.apache.poi.hwpf.model.types.LVLFAbstractType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;

@Internal
public final class ListLevel {
    private static final int MAX_RECORD_LENGTH = 0xA00000;
    private static final Logger LOG = LogManager.getLogger(ListLevel.class);
    private byte[] _grpprlChpx;
    private byte[] _grpprlPapx;
    private LVLF _lvlf;
    private Xst _xst = new Xst();

    ListLevel() {
    }

    @Deprecated
    public ListLevel(byte[] buf, int startOffset) {
        this.read(buf, startOffset);
    }

    public ListLevel(int level, boolean numbered) {
        this._lvlf = new LVLF();
        this.setStartAt(1);
        this._grpprlPapx = new byte[0];
        this._grpprlChpx = new byte[0];
        if (numbered) {
            this._lvlf.getRgbxchNums()[0] = 1;
            this._xst = new Xst("" + (char)level + ".");
        } else {
            this._xst = new Xst("\u2022");
        }
    }

    public ListLevel(int startAt, int numberFormatCode, int alignment, byte[] numberProperties, byte[] entryProperties, String numberText) {
        this._lvlf = new LVLF();
        this.setStartAt(startAt);
        this._lvlf.setNfc((byte)numberFormatCode);
        this._lvlf.setJc((byte)alignment);
        this._grpprlChpx = (byte[])numberProperties.clone();
        this._grpprlPapx = (byte[])entryProperties.clone();
        this._xst = new Xst(numberText);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ListLevel)) {
            return false;
        }
        ListLevel lvl = (ListLevel)obj;
        return lvl._lvlf.equals(this._lvlf) && Arrays.equals(lvl._grpprlChpx, this._grpprlChpx) && Arrays.equals(lvl._grpprlPapx, this._grpprlPapx) && lvl._xst.equals(this._xst);
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public int getAlignment() {
        return this._lvlf.getJc();
    }

    public byte[] getGrpprlChpx() {
        return this._grpprlChpx;
    }

    public byte[] getGrpprlPapx() {
        return this._grpprlPapx;
    }

    public byte[] getLevelProperties() {
        return this._grpprlPapx;
    }

    public int getNumberFormat() {
        return this._lvlf.getNfc();
    }

    public String getNumberText() {
        return this._xst.getAsJavaString();
    }

    public int getSizeInBytes() {
        return LVLFAbstractType.getSize() + this._lvlf.getCbGrpprlChpx() + this._lvlf.getCbGrpprlPapx() + this._xst.getSize();
    }

    public int getStartAt() {
        return this._lvlf.getIStartAt();
    }

    public byte getTypeOfCharFollowingTheNumber() {
        return this._lvlf.getIxchFollow();
    }

    public short getRestart() {
        return this._lvlf.isFNoRestart() ? this._lvlf.getIlvlRestartLim() : (short)-1;
    }

    public boolean isLegalNumbering() {
        return this._lvlf.isFLegal();
    }

    public byte[] getLevelNumberingPlaceholderOffsets() {
        return this._lvlf.getRgbxchNums();
    }

    int read(byte[] data, int startOffset) {
        int offset = startOffset;
        this._lvlf = new LVLF(data, offset);
        this._grpprlPapx = new byte[this._lvlf.getCbGrpprlPapx()];
        System.arraycopy(data, offset += LVLFAbstractType.getSize(), this._grpprlPapx, 0, this._lvlf.getCbGrpprlPapx());
        this._grpprlChpx = new byte[this._lvlf.getCbGrpprlChpx()];
        System.arraycopy(data, offset += this._lvlf.getCbGrpprlPapx(), this._grpprlChpx, 0, this._lvlf.getCbGrpprlChpx());
        this._xst = new Xst(data, offset += this._lvlf.getCbGrpprlChpx());
        offset += this._xst.getSize();
        if (this._lvlf.getNfc() == 23 && this._xst.getCch() != 1) {
            LOG.atWarn().log("LVL at offset {} has nfc == 0x17 (bullets), but cch != 1 ({})", (Object)Unbox.box(startOffset), (Object)Unbox.box(this._xst.getCch()));
        }
        return offset - startOffset;
    }

    public void setAlignment(int alignment) {
        this._lvlf.setJc((byte)alignment);
    }

    public void setLevelProperties(byte[] grpprl) {
        this._grpprlPapx = grpprl;
    }

    public void setNumberFormat(int numberFormatCode) {
        this._lvlf.setNfc((byte)numberFormatCode);
    }

    public void setNumberProperties(byte[] grpprl) {
        this._grpprlChpx = grpprl;
    }

    public void setStartAt(int startAt) {
        this._lvlf.setIStartAt(startAt);
    }

    public void setTypeOfCharFollowingTheNumber(byte value) {
        this._lvlf.setIxchFollow(value);
    }

    public byte[] toByteArray() {
        byte[] buf = IOUtils.safelyAllocate(this.getSizeInBytes(), 0xA00000);
        int offset = 0;
        this._lvlf.setCbGrpprlChpx((short)this._grpprlChpx.length);
        this._lvlf.setCbGrpprlPapx((short)this._grpprlPapx.length);
        this._lvlf.serialize(buf, offset);
        System.arraycopy(this._grpprlPapx, 0, buf, offset += LVLFAbstractType.getSize(), this._grpprlPapx.length);
        System.arraycopy(this._grpprlChpx, 0, buf, offset += this._grpprlPapx.length, this._grpprlChpx.length);
        this._xst.serialize(buf, offset += this._grpprlChpx.length);
        return buf;
    }

    public String toString() {
        return "LVL: " + ("\n" + this._lvlf).replace("\n", "\n    ") + "\n" + "PAPX's grpprl: " + Arrays.toString(this._grpprlPapx) + "\n" + "CHPX's grpprl: " + Arrays.toString(this._grpprlChpx) + "\n" + "xst: " + this._xst + "\n";
    }
}

