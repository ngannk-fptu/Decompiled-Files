/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class AnimationInfoAtom
extends RecordAtom {
    public static final int Reverse = 1;
    public static final int Automatic = 4;
    public static final int Sound = 16;
    public static final int StopSound = 64;
    public static final int Play = 256;
    public static final int Synchronous = 1024;
    public static final int Hide = 4096;
    public static final int AnimateBg = 16384;
    private static final int[] FLAGS_MASKS = new int[]{1, 4, 16, 64, 256, 1024, 4096, 16384};
    private static final String[] FLAGS_NAMES = new String[]{"REVERSE", "AUTOMATIC", "SOUND", "STOP_SOUND", "PLAY", "SYNCHRONOUS", "HIDE", "ANIMATE_BG"};
    private final byte[] _header;
    private final byte[] _recdata;

    protected AnimationInfoAtom() {
        this._recdata = new byte[28];
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 0, (short)1);
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._recdata.length);
    }

    protected AnimationInfoAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._recdata = IOUtils.safelyClone(source, start + 8, len - 8, AnimationInfoAtom.getMaxRecordLength());
    }

    @Override
    public long getRecordType() {
        return RecordTypes.AnimationInfoAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._recdata);
    }

    public int getDimColor() {
        return LittleEndian.getInt(this._recdata, 0);
    }

    public void setDimColor(int rgb) {
        LittleEndian.putInt(this._recdata, 0, rgb);
    }

    public int getMask() {
        return LittleEndian.getInt(this._recdata, 4);
    }

    public void setMask(int mask) {
        LittleEndian.putInt(this._recdata, 4, mask);
    }

    public boolean getFlag(int bit) {
        return (this.getMask() & bit) != 0;
    }

    public void setFlag(int bit, boolean value) {
        int mask = this.getMask();
        mask = value ? (mask |= bit) : (mask &= ~bit);
        this.setMask(mask);
    }

    public int getSoundIdRef() {
        return LittleEndian.getInt(this._recdata, 8);
    }

    public void setSoundIdRef(int id) {
        LittleEndian.putInt(this._recdata, 8, id);
    }

    public int getDelayTime() {
        return LittleEndian.getInt(this._recdata, 12);
    }

    public void setDelayTime(int id) {
        LittleEndian.putInt(this._recdata, 12, id);
    }

    public int getOrderID() {
        return LittleEndian.getInt(this._recdata, 16);
    }

    public void setOrderID(int id) {
        LittleEndian.putInt(this._recdata, 16, id);
    }

    public int getSlideCount() {
        return LittleEndian.getInt(this._recdata, 18);
    }

    public void setSlideCount(int id) {
        LittleEndian.putInt(this._recdata, 18, id);
    }

    public String toString() {
        int mask = this.getMask();
        return "AnimationInfoAtom\n\tDimColor: " + this.getDimColor() + "\n\tMask: " + mask + ", 0x" + Integer.toHexString(mask) + "\n\t  Reverse: " + this.getFlag(1) + "\n\t  Automatic: " + this.getFlag(4) + "\n\t  Sound: " + this.getFlag(16) + "\n\t  StopSound: " + this.getFlag(64) + "\n\t  Play: " + this.getFlag(256) + "\n\t  Synchronous: " + this.getFlag(1024) + "\n\t  Hide: " + this.getFlag(4096) + "\n\t  AnimateBg: " + this.getFlag(16384) + "\n\tSoundIdRef: " + this.getSoundIdRef() + "\n\tDelayTime: " + this.getDelayTime() + "\n\tOrderID: " + this.getOrderID() + "\n\tSlideCount: " + this.getSlideCount() + "\n";
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("dimColor", this::getDimColor, "flags", GenericRecordUtil.getBitsAsString(this::getMask, FLAGS_MASKS, FLAGS_NAMES), "soundIdRef", this::getSoundIdRef, "delayTime", this::getDelayTime, "orderID", this::getOrderID, "slideCount", this::getSlideCount);
    }
}

