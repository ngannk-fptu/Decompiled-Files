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
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class NotesAtom
extends RecordAtom {
    private byte[] _header;
    private static long _type = 1009L;
    private int slideID;
    private boolean followMasterObjects;
    private boolean followMasterScheme;
    private boolean followMasterBackground;
    private byte[] reserved;

    public int getSlideID() {
        return this.slideID;
    }

    public void setSlideID(int id) {
        this.slideID = id;
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

    protected NotesAtom(byte[] source, int start, int len) {
        if (len < 8) {
            len = 8;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.slideID = LittleEndian.getInt(source, start + 8);
        int flags = LittleEndian.getUShort(source, start + 12);
        this.followMasterBackground = (flags & 4) == 4;
        this.followMasterScheme = (flags & 2) == 2;
        this.followMasterObjects = (flags & 1) == 1;
        this.reserved = IOUtils.safelyClone(source, start + 14, len - 14, NotesAtom.getMaxRecordLength());
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        NotesAtom.writeLittleEndian(this.slideID, out);
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
        NotesAtom.writeLittleEndian(flags, out);
        out.write(this.reserved);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("slideId", this::getSlideID, "followMasterObjects", this::getFollowMasterObjects, "followMasterScheme", this::getFollowMasterScheme, "followMasterBackground", this::getFollowMasterBackground);
    }
}

