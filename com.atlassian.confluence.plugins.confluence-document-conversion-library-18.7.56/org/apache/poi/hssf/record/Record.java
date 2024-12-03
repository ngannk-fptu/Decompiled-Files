/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hssf.record;

import java.io.InputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.GenericRecordJsonWriter;

public abstract class Record
extends RecordBase
implements Duplicatable,
GenericRecord {
    protected Record() {
    }

    protected Record(Record other) {
    }

    public final byte[] serialize() {
        byte[] retval = new byte[this.getRecordSize()];
        this.serialize(0, retval);
        return retval;
    }

    public final String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    public abstract short getSid();

    public Record cloneViaReserialise() {
        byte[] b = this.serialize();
        RecordInputStream rinp = new RecordInputStream((InputStream)new UnsynchronizedByteArrayInputStream(b));
        rinp.nextRecord();
        Record[] r = RecordFactory.createRecord(rinp);
        if (r.length != 1) {
            throw new IllegalStateException("Re-serialised a record to clone it, but got " + r.length + " records back!");
        }
        return r[0];
    }

    @Override
    public abstract Record copy();

    public abstract HSSFRecordTypes getGenericRecordType();
}

