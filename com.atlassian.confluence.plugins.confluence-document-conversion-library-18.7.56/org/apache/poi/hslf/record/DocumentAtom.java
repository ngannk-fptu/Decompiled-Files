/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Removal;

public final class DocumentAtom
extends RecordAtom {
    private final byte[] _header = new byte[8];
    private static final long _type = RecordTypes.DocumentAtom.typeID;
    private long slideSizeX;
    private long slideSizeY;
    private long notesSizeX;
    private long notesSizeY;
    private long serverZoomFrom;
    private long serverZoomTo;
    private final long notesMasterPersist;
    private final long handoutMasterPersist;
    private final int firstSlideNum;
    private int slideSizeType;
    private byte saveWithFonts;
    private final byte omitTitlePlace;
    private final byte rightToLeft;
    private final byte showComments;
    private final byte[] reserved;

    public long getSlideSizeX() {
        return this.slideSizeX;
    }

    public long getSlideSizeY() {
        return this.slideSizeY;
    }

    public long getNotesSizeX() {
        return this.notesSizeX;
    }

    public long getNotesSizeY() {
        return this.notesSizeY;
    }

    public void setSlideSizeX(long x) {
        this.slideSizeX = x;
    }

    public void setSlideSizeY(long y) {
        this.slideSizeY = y;
    }

    public void setNotesSizeX(long x) {
        this.notesSizeX = x;
    }

    public void setNotesSizeY(long y) {
        this.notesSizeY = y;
    }

    public long getServerZoomFrom() {
        return this.serverZoomFrom;
    }

    public long getServerZoomTo() {
        return this.serverZoomTo;
    }

    public void setServerZoomFrom(long zoom) {
        this.serverZoomFrom = zoom;
    }

    public void setServerZoomTo(long zoom) {
        this.serverZoomTo = zoom;
    }

    public long getNotesMasterPersist() {
        return this.notesMasterPersist;
    }

    public long getHandoutMasterPersist() {
        return this.handoutMasterPersist;
    }

    public int getFirstSlideNum() {
        return this.firstSlideNum;
    }

    public SlideSize getSlideSizeType() {
        return SlideSize.values()[this.slideSizeType];
    }

    @Deprecated
    @Removal(version="6.0.0")
    public SlideSize getSlideSizeTypeEnum() {
        return SlideSize.values()[this.slideSizeType];
    }

    public void setSlideSize(SlideSize size) {
        this.slideSizeType = size.ordinal();
    }

    public boolean getSaveWithFonts() {
        return this.saveWithFonts != 0;
    }

    public void setSaveWithFonts(boolean saveWithFonts) {
        this.saveWithFonts = (byte)(saveWithFonts ? 1 : 0);
    }

    public boolean getOmitTitlePlace() {
        return this.omitTitlePlace != 0;
    }

    public boolean getRightToLeft() {
        return this.rightToLeft != 0;
    }

    public boolean getShowComments() {
        return this.showComments != 0;
    }

    DocumentAtom(byte[] source, int start, int len) {
        int maxLen = Math.max(len, 48);
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(source, start, maxLen);
        leis.readFully(this._header);
        this.slideSizeX = leis.readInt();
        this.slideSizeY = leis.readInt();
        this.notesSizeX = leis.readInt();
        this.notesSizeY = leis.readInt();
        this.serverZoomFrom = leis.readInt();
        this.serverZoomTo = leis.readInt();
        this.notesMasterPersist = leis.readInt();
        this.handoutMasterPersist = leis.readInt();
        this.firstSlideNum = leis.readShort();
        this.slideSizeType = leis.readShort();
        this.saveWithFonts = leis.readByte();
        this.omitTitlePlace = leis.readByte();
        this.rightToLeft = leis.readByte();
        this.showComments = leis.readByte();
        this.reserved = IOUtils.safelyAllocate((long)maxLen - 48L, DocumentAtom.getMaxRecordLength());
        leis.readFully(this.reserved);
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        DocumentAtom.writeLittleEndian((int)this.slideSizeX, out);
        DocumentAtom.writeLittleEndian((int)this.slideSizeY, out);
        DocumentAtom.writeLittleEndian((int)this.notesSizeX, out);
        DocumentAtom.writeLittleEndian((int)this.notesSizeY, out);
        DocumentAtom.writeLittleEndian((int)this.serverZoomFrom, out);
        DocumentAtom.writeLittleEndian((int)this.serverZoomTo, out);
        DocumentAtom.writeLittleEndian((int)this.notesMasterPersist, out);
        DocumentAtom.writeLittleEndian((int)this.handoutMasterPersist, out);
        DocumentAtom.writeLittleEndian((short)this.firstSlideNum, out);
        DocumentAtom.writeLittleEndian((short)this.slideSizeType, out);
        out.write(this.saveWithFonts);
        out.write(this.omitTitlePlace);
        out.write(this.rightToLeft);
        out.write(this.showComments);
        out.write(this.reserved);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("slideSizeX", this::getSlideSizeX);
        m.put("slideSizeY", this::getSlideSizeY);
        m.put("notesSizeX", this::getNotesSizeX);
        m.put("notesSizeY", this::getNotesSizeY);
        m.put("serverZoomFrom", this::getServerZoomFrom);
        m.put("serverZoomTo", this::getServerZoomTo);
        m.put("notesMasterPersist", this::getNotesMasterPersist);
        m.put("handoutMasterPersist", this::getHandoutMasterPersist);
        m.put("firstSlideNum", this::getFirstSlideNum);
        m.put("slideSize", this::getSlideSizeTypeEnum);
        m.put("saveWithFonts", this::getSaveWithFonts);
        m.put("omitTitlePlace", this::getOmitTitlePlace);
        m.put("rightToLeft", this::getRightToLeft);
        m.put("showComments", this::getShowComments);
        return Collections.unmodifiableMap(m);
    }

    public static enum SlideSize {
        ON_SCREEN,
        LETTER_SIZED_PAPER,
        A4_SIZED_PAPER,
        ON_35MM,
        OVERHEAD,
        BANNER,
        CUSTOM;

    }
}

