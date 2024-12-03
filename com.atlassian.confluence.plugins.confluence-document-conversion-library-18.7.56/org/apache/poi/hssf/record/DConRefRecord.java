/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

public class DConRefRecord
extends StandardRecord {
    public static final short sid = 81;
    private final int firstRow;
    private final int lastRow;
    private final int firstCol;
    private final int lastCol;
    private final int charCount;
    private final int charType;
    private final byte[] path;
    private byte[] _unused;

    public DConRefRecord(DConRefRecord other) {
        super(other);
        this.firstCol = other.firstCol;
        this.firstRow = other.firstRow;
        this.lastCol = other.lastCol;
        this.lastRow = other.lastRow;
        this.charCount = other.charCount;
        this.charType = other.charType;
        this.path = other.path == null ? null : (byte[])other.path.clone();
        this._unused = other._unused == null ? null : (byte[])other._unused.clone();
    }

    public DConRefRecord(byte[] data) {
        this(DConRefRecord.bytesToRIStream(data));
    }

    public DConRefRecord(RecordInputStream inStream) {
        if (inStream.getSid() != 81) {
            throw new RecordFormatException("Wrong sid: " + inStream.getSid());
        }
        this.firstRow = inStream.readUShort();
        this.lastRow = inStream.readUShort();
        this.firstCol = inStream.readUByte();
        this.lastCol = inStream.readUByte();
        this.charCount = inStream.readUShort();
        this.charType = inStream.readUByte() & 1;
        int byteLength = this.charCount * (this.charType + 1);
        this.path = IOUtils.safelyAllocate(byteLength, HSSFWorkbook.getMaxRecordLength());
        inStream.readFully(this.path);
        if (this.path[0] == 2) {
            this._unused = inStream.readRemainder();
        }
    }

    @Override
    protected int getDataSize() {
        int sz = 9 + this.path.length;
        if (this.path[0] == 2) {
            sz += this._unused.length;
        }
        return sz;
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.firstRow);
        out.writeShort(this.lastRow);
        out.writeByte(this.firstCol);
        out.writeByte(this.lastCol);
        out.writeShort(this.charCount);
        out.writeByte(this.charType);
        out.write(this.path);
        if (this.path[0] == 2) {
            out.write(this._unused);
        }
    }

    @Override
    public short getSid() {
        return 81;
    }

    public int getFirstColumn() {
        return this.firstCol;
    }

    public int getFirstRow() {
        return this.firstRow;
    }

    public int getLastColumn() {
        return this.lastCol;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    public byte[] getPath() {
        return Arrays.copyOf(this.path, this.path.length);
    }

    public String getReadablePath() {
        if (this.path != null) {
            int offset;
            for (offset = 1; offset < this.path.length && this.path[offset] < 32; ++offset) {
            }
            String out = new String(Arrays.copyOfRange(this.path, offset, this.path.length), StringUtil.UTF8);
            out = out.replace("\u0003", "/");
            return out;
        }
        return null;
    }

    public boolean isExternalRef() {
        return this.path[0] == 1;
    }

    @Override
    public DConRefRecord copy() {
        return new DConRefRecord(this);
    }

    private static RecordInputStream bytesToRIStream(byte[] data) {
        RecordInputStream ric = new RecordInputStream((InputStream)new UnsynchronizedByteArrayInputStream(data));
        ric.nextRecord();
        return ric;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DCON_REF;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("firstRow", this::getFirstRow, "lastRow", this::getLastRow, "firstColumn", this::getFirstColumn, "lastColumn", this::getLastColumn, "charCount", () -> this.charCount, "charType", () -> this.charType, "path", this::getReadablePath);
    }
}

