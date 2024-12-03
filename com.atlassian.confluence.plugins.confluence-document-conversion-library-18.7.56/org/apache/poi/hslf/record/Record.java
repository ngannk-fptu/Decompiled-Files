/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hslf.exceptions.CorruptPowerPointFileException;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public abstract class Record
implements GenericRecord {
    protected static final Logger LOG = LogManager.getLogger(Record.class);

    public abstract boolean isAnAtom();

    public abstract long getRecordType();

    public abstract Record[] getChildRecords();

    public abstract void writeOut(OutputStream var1) throws IOException;

    public Enum getGenericRecordType() {
        return RecordTypes.forTypeID((int)this.getRecordType());
    }

    public List<Record> getGenericChildren() {
        Record[] recs = this.getChildRecords();
        return recs == null ? null : Arrays.asList(recs);
    }

    public static void writeLittleEndian(int i, OutputStream o) throws IOException {
        byte[] bi = new byte[4];
        LittleEndian.putInt(bi, 0, i);
        o.write(bi);
    }

    public static void writeLittleEndian(short s, OutputStream o) throws IOException {
        byte[] bs = new byte[2];
        LittleEndian.putShort(bs, 0, s);
        o.write(bs);
    }

    public static Record buildRecordAtOffset(byte[] b, int offset) {
        long type = LittleEndian.getUShort(b, offset + 2);
        long rlen = LittleEndian.getUInt(b, offset + 4);
        int rleni = (int)rlen;
        if (rleni < 0) {
            rleni = 0;
        }
        return Record.createRecordForType(type, b, offset, 8 + rleni);
    }

    public static Record[] findChildRecords(byte[] b, int start, int len) {
        int rleni;
        ArrayList<Record> children = new ArrayList<Record>(5);
        for (int pos = start; pos <= start + len - 8; pos += rleni) {
            long type = LittleEndian.getUShort(b, pos + 2);
            long rlen = LittleEndian.getUInt(b, pos + 4);
            rleni = (int)rlen;
            if (rleni < 0) {
                rleni = 0;
            }
            if (pos == 0 && type == 0L && rleni == 65535) {
                throw new CorruptPowerPointFileException("Corrupt document - starts with record of type 0000 and length 0xFFFF");
            }
            Record r = Record.createRecordForType(type, b, pos, 8 + rleni);
            if (r != null) {
                children.add(r);
            }
            pos += 8;
        }
        return children.toArray(new Record[0]);
    }

    public static Record createRecordForType(long type, byte[] b, int start, int len) {
        Object toReturn;
        RecordTypes recordType = RecordTypes.forTypeID((short)type);
        RecordTypes.RecordConstructor<?> c = recordType.recordConstructor;
        if (c == null) {
            LOG.atDebug().log(() -> new StringFormattedMessage("Known but unhandled record type %d (0x%04x) at offset %d", type, type, start));
            c = RecordTypes.UnknownRecordPlaceholder.recordConstructor;
        } else if (recordType == RecordTypes.UnknownRecordPlaceholder) {
            LOG.atDebug().log(() -> new StringFormattedMessage("Unknown placeholder type %d (0x%04x) at offset %d", type, type, start));
        }
        try {
            toReturn = c.apply(b, start, len);
        }
        catch (RuntimeException e) {
            if (start + len > b.length) {
                LOG.atWarn().log("Warning: Skipping record of type {} at position {} which claims to be longer than the file! ({} vs {})", (Object)type, (Object)Unbox.box(start), (Object)Unbox.box(len), (Object)Unbox.box(b.length - start));
                return null;
            }
            throw new HSLFException("Couldn't instantiate the class for type with id " + type + " on class " + c + " : " + e, e);
        }
        if (toReturn instanceof PositionDependentRecord) {
            PositionDependentRecord pdr = (PositionDependentRecord)toReturn;
            pdr.setLastOnDiskOffset(start);
        }
        return toReturn;
    }
}

