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
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class ExOleObjAtom
extends RecordAtom {
    private static final int[] DRAWASPECT_MASKS = new int[]{1, 2, 4, 8};
    private static final String[] DRAWASPECT_NAMES = new String[]{"CONTENT", "THUMBNAIL", "ICON", "DOCPRINT"};
    private static final int MAX_RECORD_LENGTH = 0xA00000;
    public static final int DRAW_ASPECT_VISIBLE = 1;
    public static final int DRAW_ASPECT_THUMBNAIL = 2;
    public static final int DRAW_ASPECT_ICON = 4;
    public static final int DRAW_ASPECT_DOCPRINT = 8;
    public static final int TYPE_EMBEDDED = 0;
    public static final int TYPE_LINKED = 1;
    public static final int TYPE_CONTROL = 2;
    public static final int SUBTYPE_DEFAULT = 0;
    public static final int SUBTYPE_CLIPART_GALLERY = 1;
    public static final int SUBTYPE_WORD_TABLE = 2;
    public static final int SUBTYPE_EXCEL = 3;
    public static final int SUBTYPE_GRAPH = 4;
    public static final int SUBTYPE_ORGANIZATION_CHART = 5;
    public static final int SUBTYPE_EQUATION = 6;
    public static final int SUBTYPE_WORDART = 7;
    public static final int SUBTYPE_SOUND = 8;
    public static final int SUBTYPE_IMAGE = 9;
    public static final int SUBTYPE_POWERPOINT_PRESENTATION = 10;
    public static final int SUBTYPE_POWERPOINT_SLIDE = 11;
    public static final int SUBTYPE_PROJECT = 12;
    public static final int SUBTYPE_NOTEIT = 13;
    public static final int SUBTYPE_EXCEL_CHART = 14;
    public static final int SUBTYPE_MEDIA_PLAYER = 15;
    private byte[] _header;
    private byte[] _data;

    public ExOleObjAtom() {
        this._header = new byte[8];
        this._data = new byte[24];
        LittleEndian.putShort(this._header, 0, (short)1);
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected ExOleObjAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, 0xA00000);
        if (this._data.length < 24) {
            throw new IllegalArgumentException("The length of the data for a ExOleObjAtom must be at least 24 bytes, but was only " + this._data.length);
        }
    }

    public int getDrawAspect() {
        return LittleEndian.getInt(this._data, 0);
    }

    public void setDrawAspect(int aspect) {
        LittleEndian.putInt(this._data, 0, aspect);
    }

    public int getType() {
        return LittleEndian.getInt(this._data, 4);
    }

    public void setType(int type) {
        LittleEndian.putInt(this._data, 4, type);
    }

    public int getObjID() {
        return LittleEndian.getInt(this._data, 8);
    }

    public void setObjID(int id) {
        LittleEndian.putInt(this._data, 8, id);
    }

    public int getSubType() {
        return LittleEndian.getInt(this._data, 12);
    }

    public void setSubType(int type) {
        LittleEndian.putInt(this._data, 12, type);
    }

    public int getObjStgDataRef() {
        return LittleEndian.getInt(this._data, 16);
    }

    public void setObjStgDataRef(int ref) {
        LittleEndian.putInt(this._data, 16, ref);
    }

    public boolean getIsBlank() {
        return LittleEndian.getInt(this._data, 20) != 0;
    }

    public int getOptions() {
        return LittleEndian.getInt(this._data, 20);
    }

    public void setOptions(int opts) {
        LittleEndian.putInt(this._data, 20, opts);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExOleObjAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("drawAspect", GenericRecordUtil.getBitsAsString(this::getDrawAspect, DRAWASPECT_MASKS, DRAWASPECT_NAMES), "type", GenericRecordUtil.safeEnum((Enum[])OleType.values(), this::getType), "objID", this::getObjID, "subType", GenericRecordUtil.safeEnum((Enum[])Subtype.values(), this::getSubType), "objStgDataRef", this::getObjStgDataRef, "options", this::getOptions);
    }

    public static enum Subtype {
        DEFAULT,
        CLIPART_GALLERY,
        WORD_TABLE,
        EXCEL,
        GRAPH,
        ORGANIZATION_CHART,
        EQUATION,
        WORDART,
        SOUND,
        IMAGE,
        POWERPOINT_PRESENTATION,
        POWERPOINT_SLIDE,
        PROJECT,
        NOTEIT,
        EXCEL_CHART,
        MEDIA_PLAYER;

    }

    public static enum OleType {
        EMBEDDED,
        LINKED,
        CONTROL;

    }
}

