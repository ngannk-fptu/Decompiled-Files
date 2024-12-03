/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;
import java.util.Date;

public class HeadTable
extends TrueTypeTable {
    private int version;
    private int fontRevision;
    private int checksumAdjustment;
    private int magicNumber;
    private short flags;
    private short unitsPerEm;
    private long created;
    private long modified;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private short macStyle;
    private short lowestRecPPem;
    private short fontDirectionHint;
    private short indexToLocFormat;
    private short glyphDataFormat;

    protected HeadTable() {
        super(1751474532);
        this.setVersion(65536);
        this.setFontRevision(65536);
        this.setChecksumAdjustment(0);
        this.setMagicNumber(1594834165);
        this.setFlags((short)0);
        this.setUnitsPerEm((short)64);
        this.setCreated(System.currentTimeMillis());
        this.setModified(System.currentTimeMillis());
        this.setXMin((short)0);
        this.setXMax((short)Short.MAX_VALUE);
        this.setYMin((short)0);
        this.setYMax((short)Short.MAX_VALUE);
        this.setMacStyle((short)0);
        this.setLowestRecPPem((short)0);
        this.setFontDirectionHint((short)0);
        this.setIndexToLocFormat((short)0);
        this.setGlyphDataFormat((short)0);
    }

    @Override
    public void setData(ByteBuffer data) {
        if (data.remaining() != 54) {
            throw new IllegalArgumentException("Bad Head table size");
        }
        this.setVersion(data.getInt());
        this.setFontRevision(data.getInt());
        this.setChecksumAdjustment(data.getInt());
        this.setMagicNumber(data.getInt());
        this.setFlags(data.getShort());
        this.setUnitsPerEm(data.getShort());
        this.setCreated(data.getLong());
        this.setModified(data.getLong());
        this.setXMin(data.getShort());
        this.setXMax(data.getShort());
        this.setYMin(data.getShort());
        this.setYMax(data.getShort());
        this.setMacStyle(data.getShort());
        this.setLowestRecPPem(data.getShort());
        this.setFontDirectionHint(data.getShort());
        this.setIndexToLocFormat(data.getShort());
        this.setGlyphDataFormat(data.getShort());
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putInt(this.getVersion());
        buf.putInt(this.getFontRevision());
        buf.putInt(this.getChecksumAdjustment());
        buf.putInt(this.getMagicNumber());
        buf.putShort(this.getFlags());
        buf.putShort(this.getUnitsPerEm());
        buf.putLong(this.getCreated());
        buf.putLong(this.getModified());
        buf.putShort(this.getXMin());
        buf.putShort(this.getXMax());
        buf.putShort(this.getYMin());
        buf.putShort(this.getYMax());
        buf.putShort(this.getMacStyle());
        buf.putShort(this.getLowestRecPPem());
        buf.putShort(this.getFontDirectionHint());
        buf.putShort(this.getIndexToLocFormat());
        buf.putShort(this.getGlyphDataFormat());
        buf.flip();
        return buf;
    }

    @Override
    public int getLength() {
        return 54;
    }

    public int getVersion() {
        return this.version;
    }

    public int getFontRevision() {
        return this.fontRevision;
    }

    public int getChecksumAdjustment() {
        return this.checksumAdjustment;
    }

    public int getMagicNumber() {
        return this.magicNumber;
    }

    public short getFlags() {
        return this.flags;
    }

    public short getUnitsPerEm() {
        return this.unitsPerEm;
    }

    public long getCreated() {
        return this.created;
    }

    public long getModified() {
        return this.modified;
    }

    public short getXMin() {
        return this.xMin;
    }

    public short getYMin() {
        return this.yMin;
    }

    public short getXMax() {
        return this.xMax;
    }

    public short getYMax() {
        return this.yMax;
    }

    public short getMacStyle() {
        return this.macStyle;
    }

    public short getLowestRecPPem() {
        return this.lowestRecPPem;
    }

    public short getFontDirectionHint() {
        return this.fontDirectionHint;
    }

    public short getIndexToLocFormat() {
        return this.indexToLocFormat;
    }

    public short getGlyphDataFormat() {
        return this.glyphDataFormat;
    }

    public void setXMax(short xMax) {
        this.xMax = xMax;
    }

    public void setXMin(short xMin) {
        this.xMin = xMin;
    }

    public void setYMax(short yMax) {
        this.yMax = yMax;
    }

    public void setYMin(short yMin) {
        this.yMin = yMin;
    }

    public void setChecksumAdjustment(int checksumAdjustment) {
        this.checksumAdjustment = checksumAdjustment;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public void setFontDirectionHint(short fontDirectionHint) {
        this.fontDirectionHint = fontDirectionHint;
    }

    public void setFontRevision(int fontRevision) {
        this.fontRevision = fontRevision;
    }

    public void setGlyphDataFormat(short glyphDataFormat) {
        this.glyphDataFormat = glyphDataFormat;
    }

    public void setIndexToLocFormat(short indexToLocFormat) {
        this.indexToLocFormat = indexToLocFormat;
    }

    public void setLowestRecPPem(short lowestRecPPem) {
        this.lowestRecPPem = lowestRecPPem;
    }

    public void setMacStyle(short macStyle) {
        this.macStyle = macStyle;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public void setUnitsPerEm(short unitsPerEm) {
        this.unitsPerEm = unitsPerEm;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Version          : " + Integer.toHexString(this.getVersion()) + "\n");
        buf.append(indent + "Revision         : " + Integer.toHexString(this.getFontRevision()) + "\n");
        buf.append(indent + "ChecksumAdj      : " + Integer.toHexString(this.getChecksumAdjustment()) + "\n");
        buf.append(indent + "MagicNumber      : " + Integer.toHexString(this.getMagicNumber()) + "\n");
        buf.append(indent + "Flags            : " + Integer.toBinaryString(this.getFlags()) + "\n");
        buf.append(indent + "UnitsPerEm       : " + this.getUnitsPerEm() + "\n");
        buf.append(indent + "Created          : " + new Date(this.getCreated()) + "\n");
        buf.append(indent + "Modified         : " + new Date(this.getModified()) + "\n");
        buf.append(indent + "XMin             : " + this.getXMin() + "\n");
        buf.append(indent + "XMax             : " + this.getXMax() + "\n");
        buf.append(indent + "YMin             : " + this.getYMin() + "\n");
        buf.append(indent + "YMax             : " + this.getYMax() + "\n");
        buf.append(indent + "MacStyle         : " + Integer.toBinaryString(this.getMacStyle()) + "\n");
        buf.append(indent + "LowestPPem       : " + this.getLowestRecPPem() + "\n");
        buf.append(indent + "FontDirectionHint: " + this.getFontDirectionHint() + "\n");
        buf.append(indent + "IndexToLocFormat : " + this.getIndexToLocFormat() + "\n");
        buf.append(indent + "GlyphDataFormat  : " + this.getGlyphDataFormat() + "\n");
        return buf.toString();
    }
}

