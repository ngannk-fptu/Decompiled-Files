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

public class InteractiveInfoAtom
extends RecordAtom {
    public static final byte ACTION_NONE = 0;
    public static final byte ACTION_MACRO = 1;
    public static final byte ACTION_RUNPROGRAM = 2;
    public static final byte ACTION_JUMP = 3;
    public static final byte ACTION_HYPERLINK = 4;
    public static final byte ACTION_OLE = 5;
    public static final byte ACTION_MEDIA = 6;
    public static final byte ACTION_CUSTOMSHOW = 7;
    public static final byte JUMP_NONE = 0;
    public static final byte JUMP_NEXTSLIDE = 1;
    public static final byte JUMP_PREVIOUSSLIDE = 2;
    public static final byte JUMP_FIRSTSLIDE = 3;
    public static final byte JUMP_LASTSLIDE = 4;
    public static final byte JUMP_LASTSLIDEVIEWED = 5;
    public static final byte JUMP_ENDSHOW = 6;
    public static final byte LINK_NextSlide = 0;
    public static final byte LINK_PreviousSlide = 1;
    public static final byte LINK_FirstSlide = 2;
    public static final byte LINK_LastSlide = 3;
    public static final byte LINK_CustomShow = 6;
    public static final byte LINK_SlideNumber = 7;
    public static final byte LINK_Url = 8;
    public static final byte LINK_OtherPresentation = 9;
    public static final byte LINK_OtherFile = 10;
    public static final byte LINK_NULL = -1;
    private static final int[] FLAGS_MASKS = new int[]{1, 2, 4, 8};
    private static final String[] FLAGS_NAMES = new String[]{"ANIMATED", "STOP_SOUND", "CUSTOM_SHOW_RETURN", "VISITED"};
    private final byte[] _header;
    private final byte[] _data;

    protected InteractiveInfoAtom() {
        this._header = new byte[8];
        this._data = new byte[16];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected InteractiveInfoAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, InteractiveInfoAtom.getMaxRecordLength());
        if (this._data.length < 16) {
            throw new IllegalArgumentException("The length of the data for a InteractiveInfoAtom must be at least 16 bytes, but was only " + this._data.length);
        }
    }

    public int getHyperlinkID() {
        return LittleEndian.getInt(this._data, 4);
    }

    public void setHyperlinkID(int number) {
        LittleEndian.putInt(this._data, 4, number);
    }

    public int getSoundRef() {
        return LittleEndian.getInt(this._data, 0);
    }

    public void setSoundRef(int val) {
        LittleEndian.putInt(this._data, 0, val);
    }

    public byte getAction() {
        return this._data[8];
    }

    public void setAction(byte val) {
        this._data[8] = val;
    }

    public byte getOleVerb() {
        return this._data[9];
    }

    public void setOleVerb(byte val) {
        this._data[9] = val;
    }

    public byte getJump() {
        return this._data[10];
    }

    public void setJump(byte val) {
        this._data[10] = val;
    }

    public byte getFlags() {
        return this._data[11];
    }

    public void setFlags(byte val) {
        this._data[11] = val;
    }

    public byte getHyperlinkType() {
        return this._data[12];
    }

    public void setHyperlinkType(byte val) {
        this._data[12] = val;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.InteractiveInfoAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("hyperlinkID", this::getHyperlinkID, "soundRef", this::getSoundRef, "action", GenericRecordUtil.safeEnum((Enum[])Action.values(), this::getAction), "jump", GenericRecordUtil.safeEnum((Enum[])Jump.values(), this::getJump), "hyperlinkType", GenericRecordUtil.safeEnum((Enum[])Link.values(), this::getHyperlinkType, (Enum)Link.NULL), "flags", GenericRecordUtil.getBitsAsString(this::getFlags, FLAGS_MASKS, FLAGS_NAMES));
    }

    public static enum Link {
        NEXT_SLIDE,
        PREVIOUS_SLIDE,
        FIRST_SLIDE,
        LAST_SLIDE,
        CUSTOM_SHOW,
        SLIDE_NUMBER,
        URL,
        OTHER_PRESENTATION,
        OTHER_FILE,
        NULL;

    }

    public static enum Jump {
        NONE,
        NEXT_SLIDE,
        PREVIOUS_SLIDE,
        FIRST_SLIDE,
        LAST_SLIDE,
        LAST_SLIDE_VIEWED,
        END_SHOW;

    }

    public static enum Action {
        NONE,
        MACRO,
        RUN_PROGRAM,
        JUMP,
        HYPERLINK,
        OLE,
        MEDIA,
        CUSTOM_SHOW;

    }
}

