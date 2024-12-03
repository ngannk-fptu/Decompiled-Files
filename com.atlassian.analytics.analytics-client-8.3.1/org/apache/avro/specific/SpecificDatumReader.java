/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.util.ClassUtils;

public class SpecificDatumReader<T>
extends GenericDatumReader<T> {
    public SpecificDatumReader() {
        this(null, null, SpecificData.get());
    }

    public SpecificDatumReader(Class<T> c) {
        this(SpecificData.getForClass(c));
        this.setSchema(this.getSpecificData().getSchema(c));
    }

    public SpecificDatumReader(Schema schema) {
        this(schema, schema, SpecificData.getForSchema(schema));
    }

    public SpecificDatumReader(Schema writer, Schema reader) {
        this(writer, reader, SpecificData.getForSchema(reader));
    }

    public SpecificDatumReader(Schema writer, Schema reader, SpecificData data) {
        super(writer, reader, data);
    }

    public SpecificDatumReader(SpecificData data) {
        super(data);
    }

    public SpecificData getSpecificData() {
        return (SpecificData)this.getData();
    }

    @Override
    public void setSchema(Schema actual) {
        SpecificData data;
        Class c;
        if (this.getExpected() == null && actual != null && actual.getType() == Schema.Type.RECORD && (c = (data = this.getSpecificData()).getClass(actual)) != null && SpecificRecord.class.isAssignableFrom(c)) {
            this.setExpected(data.getSchema(c));
        }
        super.setSchema(actual);
    }

    @Override
    protected Class findStringClass(Schema schema) {
        Class stringClass = null;
        switch (schema.getType()) {
            case STRING: {
                stringClass = this.getPropAsClass(schema, "java-class");
                break;
            }
            case MAP: {
                stringClass = this.getPropAsClass(schema, "java-key-class");
            }
        }
        if (stringClass != null) {
            return stringClass;
        }
        return super.findStringClass(schema);
    }

    private Class getPropAsClass(Schema schema, String prop) {
        String name = schema.getProp(prop);
        if (name == null) {
            return null;
        }
        try {
            return ClassUtils.forName(this.getData().getClassLoader(), name);
        }
        catch (ClassNotFoundException e) {
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    protected Object readRecord(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        SpecificRecordBase d;
        SpecificData data = this.getSpecificData();
        if (data.useCustomCoders() && (old = data.newRecord(old, expected)) instanceof SpecificRecordBase && (d = (SpecificRecordBase)old).hasCustomCoders()) {
            d.customDecode(in);
            return d;
        }
        return super.readRecord(old, expected, in);
    }

    @Override
    protected void readField(Object record, Schema.Field field, Object oldDatum, ResolvingDecoder in, Object state) throws IOException {
        if (record instanceof SpecificRecordBase) {
            Conversion<?> conversion = ((SpecificRecordBase)record).getConversion(field.pos());
            Object datum = conversion != null ? this.readWithConversion(oldDatum, field.schema(), field.schema().getLogicalType(), conversion, in) : this.readWithoutConversion(oldDatum, field.schema(), in);
            this.getData().setField(record, field.name(), field.pos(), datum);
        } else {
            super.readField(record, field, oldDatum, in, state);
        }
    }
}

