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
import org.apache.poi.util.LittleEndian;

public class SSSlideInfoAtom
extends RecordAtom {
    public static final int MANUAL_ADVANCE_BIT = 1;
    public static final int HIDDEN_BIT = 4;
    public static final int SOUND_BIT = 16;
    public static final int LOOP_SOUND_BIT = 64;
    public static final int STOP_SOUND_BIT = 256;
    public static final int AUTO_ADVANCE_BIT = 1024;
    public static final int CURSOR_VISIBLE_BIT = 4096;
    private static final int[] EFFECT_MASKS = new int[]{1, 4, 16, 64, 256, 1024, 4096};
    private static final String[] EFFECT_NAMES = new String[]{"MANUAL_ADVANCE", "HIDDEN", "SOUND", "LOOP_SOUND", "STOP_SOUND", "AUTO_ADVANCE", "CURSOR_VISIBLE"};
    private static final long _type = RecordTypes.SSSlideInfoAtom.typeID;
    private final byte[] _header;
    private int _slideTime;
    private int _soundIdRef;
    private short _effectDirection;
    private short _effectType;
    private short _effectTransitionFlags;
    private short _speed;
    private final byte[] _unused;

    public SSSlideInfoAtom() {
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 0, (short)0);
        LittleEndian.putShort(this._header, 2, (short)_type);
        LittleEndian.putShort(this._header, 4, (short)16);
        LittleEndian.putShort(this._header, 6, (short)0);
        this._unused = new byte[3];
    }

    public SSSlideInfoAtom(byte[] source, int offset, int len) {
        int ofs = offset;
        if (len != 24) {
            len = 24;
        }
        assert (source.length >= offset + len);
        this._header = Arrays.copyOfRange(source, ofs, ofs + 8);
        ofs += this._header.length;
        if (LittleEndian.getShort(this._header, 0) != 0) {
            LOG.atDebug().log("Invalid data for SSSlideInfoAtom at offset 0: " + LittleEndian.getShort(this._header, 0));
        }
        if (LittleEndian.getShort(this._header, 2) != RecordTypes.SSSlideInfoAtom.typeID) {
            LOG.atDebug().log("Invalid data for SSSlideInfoAtom at offset 2: " + LittleEndian.getShort(this._header, 2));
        }
        if (LittleEndian.getShort(this._header, 4) != 16) {
            LOG.atDebug().log("Invalid data for SSSlideInfoAtom at offset 4: " + LittleEndian.getShort(this._header, 4));
        }
        if (LittleEndian.getShort(this._header, 6) == 0) {
            LOG.atDebug().log("Invalid data for SSSlideInfoAtom at offset 6: " + LittleEndian.getShort(this._header, 6));
        }
        this._slideTime = LittleEndian.getInt(source, ofs);
        if (this._slideTime < 0 || this._slideTime > 86399000) {
            LOG.atDebug().log("Invalid data for SSSlideInfoAtom - invalid slideTime: " + this._slideTime);
        }
        this._soundIdRef = LittleEndian.getInt(source, ofs += 4);
        this._effectDirection = LittleEndian.getUByte(source, ofs += 4);
        this._effectType = LittleEndian.getUByte(source, ++ofs);
        this._effectTransitionFlags = LittleEndian.getShort(source, ++ofs);
        this._speed = LittleEndian.getUByte(source, ofs += 2);
        this._unused = Arrays.copyOfRange(source, ++ofs, ofs + 3);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        SSSlideInfoAtom.writeLittleEndian(this._slideTime, out);
        SSSlideInfoAtom.writeLittleEndian(this._soundIdRef, out);
        byte[] byteBuf = new byte[1];
        LittleEndian.putUByte(byteBuf, 0, this._effectDirection);
        out.write(byteBuf);
        LittleEndian.putUByte(byteBuf, 0, this._effectType);
        out.write(byteBuf);
        SSSlideInfoAtom.writeLittleEndian(this._effectTransitionFlags, out);
        LittleEndian.putUByte(byteBuf, 0, this._speed);
        out.write(byteBuf);
        assert (this._unused.length == 3);
        out.write(this._unused);
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    public int getSlideTime() {
        return this._slideTime;
    }

    public void setSlideTime(int slideTime) {
        this._slideTime = slideTime;
    }

    public int getSoundIdRef() {
        return this._soundIdRef;
    }

    public void setSoundIdRef(int soundIdRef) {
        this._soundIdRef = soundIdRef;
    }

    public short getEffectDirection() {
        return this._effectDirection;
    }

    public void setEffectDirection(short effectDirection) {
        this._effectDirection = effectDirection;
    }

    public short getEffectType() {
        return this._effectType;
    }

    public void setEffectType(short effectType) {
        this._effectType = effectType;
    }

    public short getEffectTransitionFlags() {
        return this._effectTransitionFlags;
    }

    public void setEffectTransitionFlags(short effectTransitionFlags) {
        this._effectTransitionFlags = effectTransitionFlags;
    }

    public void setEffectTransitionFlagByBit(int bitmask, boolean enabled) {
        this._effectTransitionFlags = enabled ? (short)(this._effectTransitionFlags | bitmask) : (short)(this._effectTransitionFlags & (0xFFFF ^ bitmask));
    }

    public boolean getEffectTransitionFlagByBit(int bitmask) {
        return (this._effectTransitionFlags & bitmask) != 0;
    }

    public short getSpeed() {
        return this._speed;
    }

    public void setSpeed(short speed) {
        this._speed = speed;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("effectTransitionFlags", GenericRecordUtil.getBitsAsString(this::getEffectTransitionFlags, EFFECT_MASKS, EFFECT_NAMES), "slideTime", this::getSlideTime, "soundIdRef", this::getSoundIdRef, "effectDirection", this::getEffectDirection, "effectType", this::getEffectType, "speed", this::getSpeed);
    }
}

