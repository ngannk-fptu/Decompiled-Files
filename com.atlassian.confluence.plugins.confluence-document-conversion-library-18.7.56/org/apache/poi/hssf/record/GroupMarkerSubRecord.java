/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class GroupMarkerSubRecord
extends SubRecord {
    public static final short sid = 6;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private byte[] reserved;

    public GroupMarkerSubRecord() {
        this.reserved = EMPTY_BYTE_ARRAY;
    }

    public GroupMarkerSubRecord(GroupMarkerSubRecord other) {
        super(other);
        this.reserved = (byte[])other.reserved.clone();
    }

    public GroupMarkerSubRecord(LittleEndianInput in, int size) {
        this(in, size, -1);
    }

    GroupMarkerSubRecord(LittleEndianInput in, int size, int cmoOt) {
        byte[] buf = IOUtils.safelyAllocate(size, HSSFWorkbook.getMaxRecordLength());
        in.readFully(buf);
        this.reserved = buf;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(6);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }

    @Override
    protected int getDataSize() {
        return this.reserved.length;
    }

    public short getSid() {
        return 6;
    }

    @Override
    public GroupMarkerSubRecord copy() {
        return new GroupMarkerSubRecord(this);
    }

    @Override
    public SubRecord.SubRecordTypes getGenericRecordType() {
        return SubRecord.SubRecordTypes.GROUP_MARKER;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved", () -> this.reserved);
    }
}

