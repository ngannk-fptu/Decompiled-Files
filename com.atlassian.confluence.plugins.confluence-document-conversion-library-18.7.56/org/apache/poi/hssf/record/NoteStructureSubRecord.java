/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class NoteStructureSubRecord
extends SubRecord {
    public static final short sid = 13;
    private static final int ENCODED_SIZE = 22;
    private final byte[] reserved;

    public NoteStructureSubRecord() {
        this.reserved = new byte[22];
    }

    public NoteStructureSubRecord(NoteStructureSubRecord other) {
        super(other);
        this.reserved = (byte[])other.reserved.clone();
    }

    public NoteStructureSubRecord(LittleEndianInput in, int size) {
        this(in, size, -1);
    }

    public NoteStructureSubRecord(LittleEndianInput in, int size, int cmoOt) {
        if (size != 22) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
        byte[] buf = IOUtils.safelyAllocate(size, 22);
        in.readFully(buf);
        this.reserved = buf;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(13);
        out.writeShort(this.reserved.length);
        out.write(this.reserved);
    }

    @Override
    protected int getDataSize() {
        return this.reserved.length;
    }

    public short getSid() {
        return 13;
    }

    @Override
    public NoteStructureSubRecord copy() {
        return new NoteStructureSubRecord(this);
    }

    @Override
    public SubRecord.SubRecordTypes getGenericRecordType() {
        return SubRecord.SubRecordTypes.NOTE_STRUCTURE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved", () -> this.reserved);
    }
}

