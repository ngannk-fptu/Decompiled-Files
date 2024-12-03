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
import org.apache.poi.hslf.record.SlideAtomLayout;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class SlideAtom
extends RecordAtom {
    public static final int USES_MASTER_SLIDE_ID = Integer.MIN_VALUE;
    private byte[] _header;
    private static long _type = 1007L;
    private int masterID;
    private int notesID;
    private boolean followMasterObjects;
    private boolean followMasterScheme;
    private boolean followMasterBackground;
    private SlideAtomLayout layoutAtom;
    private byte[] reserved;

    public int getMasterID() {
        return this.masterID;
    }

    public void setMasterID(int id) {
        this.masterID = id;
    }

    public int getNotesID() {
        return this.notesID;
    }

    public SlideAtomLayout getSSlideLayoutAtom() {
        return this.layoutAtom;
    }

    public void setNotesID(int id) {
        this.notesID = id;
    }

    public boolean getFollowMasterObjects() {
        return this.followMasterObjects;
    }

    public boolean getFollowMasterScheme() {
        return this.followMasterScheme;
    }

    public boolean getFollowMasterBackground() {
        return this.followMasterBackground;
    }

    public void setFollowMasterObjects(boolean flag) {
        this.followMasterObjects = flag;
    }

    public void setFollowMasterScheme(boolean flag) {
        this.followMasterScheme = flag;
    }

    public void setFollowMasterBackground(boolean flag) {
        this.followMasterBackground = flag;
    }

    protected SlideAtom(byte[] source, int start, int len) {
        if (len < 30) {
            len = 30;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        byte[] SSlideLayoutAtomData = Arrays.copyOfRange(source, start + 8, start + 12 + 8);
        this.layoutAtom = new SlideAtomLayout(SSlideLayoutAtomData);
        this.masterID = LittleEndian.getInt(source, start + 12 + 8);
        this.notesID = LittleEndian.getInt(source, start + 16 + 8);
        int flags = LittleEndian.getUShort(source, start + 20 + 8);
        this.followMasterBackground = (flags & 4) == 4;
        this.followMasterScheme = (flags & 2) == 2;
        this.followMasterObjects = (flags & 1) == 1;
        this.reserved = IOUtils.safelyClone(source, start + 30, len - 30, SlideAtom.getMaxRecordLength());
    }

    public SlideAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 2);
        LittleEndian.putUShort(this._header, 2, (int)_type);
        LittleEndian.putInt(this._header, 4, 24);
        byte[] ssdate = new byte[12];
        this.layoutAtom = new SlideAtomLayout(ssdate);
        this.layoutAtom.setGeometryType(SlideAtomLayout.SlideLayoutType.BLANK_SLIDE);
        this.followMasterObjects = true;
        this.followMasterScheme = true;
        this.followMasterBackground = true;
        this.masterID = Integer.MIN_VALUE;
        this.notesID = 0;
        this.reserved = new byte[2];
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        this.layoutAtom.writeOut(out);
        SlideAtom.writeLittleEndian(this.masterID, out);
        SlideAtom.writeLittleEndian(this.notesID, out);
        short flags = 0;
        if (this.followMasterObjects) {
            flags = (short)(flags + 1);
        }
        if (this.followMasterScheme) {
            flags = (short)(flags + 2);
        }
        if (this.followMasterBackground) {
            flags = (short)(flags + 4);
        }
        SlideAtom.writeLittleEndian(flags, out);
        out.write(this.reserved);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("masterID", this::getMasterID, "notesID", this::getNotesID, "followMasterObjects", this::getFollowMasterObjects, "followMasterScheme", this::getFollowMasterScheme, "followMasterBackground", this::getFollowMasterBackground, "layoutAtom", this::getSSlideLayoutAtom);
    }
}

