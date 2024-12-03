/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.util.Date;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.CustomEncoding;

public class DateAsLongEncoding
extends CustomEncoding<Date> {
    public DateAsLongEncoding() {
        this.schema = Schema.create(Schema.Type.LONG);
        this.schema.addProp("CustomEncoding", "DateAsLongEncoding");
    }

    @Override
    protected final void write(Object datum, Encoder out) throws IOException {
        out.writeLong(((Date)datum).getTime());
    }

    @Override
    protected final Date read(Object reuse, Decoder in) throws IOException {
        if (reuse instanceof Date) {
            ((Date)reuse).setTime(in.readLong());
            return (Date)reuse;
        }
        return new Date(in.readLong());
    }
}

