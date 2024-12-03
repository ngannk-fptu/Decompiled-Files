/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hssf.record;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.FtCblsSubRecord;
import org.apache.poi.hssf.record.FtCfSubRecord;
import org.apache.poi.hssf.record.FtPioGrbitSubRecord;
import org.apache.poi.hssf.record.GroupMarkerSubRecord;
import org.apache.poi.hssf.record.LbsDataSubRecord;
import org.apache.poi.hssf.record.NoteStructureSubRecord;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

public abstract class SubRecord
implements Duplicatable,
GenericRecord {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected SubRecord() {
    }

    protected SubRecord(SubRecord other) {
    }

    public static SubRecord createSubRecord(LittleEndianInput in, int cmoOt) {
        int sid = in.readUShort();
        int size = in.readUShort();
        SubRecordTypes srt = SubRecordTypes.forSID(sid);
        return srt.recordConstructor.apply(in, size, srt == SubRecordTypes.UNKNOWN ? sid : cmoOt);
    }

    public final String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    protected abstract int getDataSize();

    public byte[] serialize() {
        int size = this.getDataSize() + 4;
        UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream(size);
        this.serialize(new LittleEndianOutputStream((OutputStream)baos));
        if (baos.size() != size) {
            throw new RuntimeException("write size mismatch");
        }
        return baos.toByteArray();
    }

    public abstract void serialize(LittleEndianOutput var1);

    public boolean isTerminating() {
        return false;
    }

    @Override
    public abstract SubRecord copy();

    public abstract SubRecordTypes getGenericRecordType();

    private static final class UnknownSubRecord
    extends SubRecord {
        private final int _sid;
        private final byte[] _data;

        public UnknownSubRecord(LittleEndianInput in, int size, int sid) {
            this._sid = sid;
            byte[] buf = IOUtils.safelyAllocate(size, MAX_RECORD_LENGTH);
            in.readFully(buf);
            this._data = buf;
        }

        @Override
        protected int getDataSize() {
            return this._data.length;
        }

        @Override
        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._sid);
            out.writeShort(this._data.length);
            out.write(this._data);
        }

        @Override
        public UnknownSubRecord copy() {
            return this;
        }

        @Override
        public SubRecordTypes getGenericRecordType() {
            return SubRecordTypes.UNKNOWN;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("sid", () -> this._sid, "data", () -> this._data);
        }
    }

    public static enum SubRecordTypes {
        UNKNOWN(-1, UnknownSubRecord::new),
        END(0, EndSubRecord::new),
        GROUP_MARKER(6, GroupMarkerSubRecord::new),
        FT_CF(7, FtCfSubRecord::new),
        FT_PIO_GRBIT(8, FtPioGrbitSubRecord::new),
        EMBEDDED_OBJECT_REF(9, EmbeddedObjectRefSubRecord::new),
        FT_CBLS(12, FtCblsSubRecord::new),
        NOTE_STRUCTURE(13, NoteStructureSubRecord::new),
        LBS_DATA(19, LbsDataSubRecord::new),
        COMMON_OBJECT_DATA(21, CommonObjectDataSubRecord::new);

        private static final Map<Short, SubRecordTypes> LOOKUP;
        public final short sid;
        public final RecordConstructor<?> recordConstructor;

        private SubRecordTypes(int sid, RecordConstructor<?> recordConstructor) {
            this.sid = (short)sid;
            this.recordConstructor = recordConstructor;
        }

        public static SubRecordTypes forSID(int sid) {
            return LOOKUP.getOrDefault((short)sid, UNKNOWN);
        }

        public short getSid() {
            return this.sid;
        }

        static {
            LOOKUP = Arrays.stream(SubRecordTypes.values()).collect(Collectors.toMap(SubRecordTypes::getSid, Function.identity()));
        }

        @FunctionalInterface
        public static interface RecordConstructor<T extends SubRecord> {
            public T apply(LittleEndianInput var1, int var2, int var3);
        }
    }
}

