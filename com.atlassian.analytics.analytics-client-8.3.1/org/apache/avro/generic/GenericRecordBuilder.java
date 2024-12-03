/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilderBase;
import org.apache.avro.generic.GenericData;

public class GenericRecordBuilder
extends RecordBuilderBase<GenericData.Record> {
    private final GenericData.Record record;

    public GenericRecordBuilder(Schema schema) {
        super(schema, GenericData.get());
        this.record = new GenericData.Record(schema);
    }

    public GenericRecordBuilder(GenericRecordBuilder other) {
        super(other, GenericData.get());
        this.record = new GenericData.Record(other.record, true);
    }

    public GenericRecordBuilder(GenericData.Record other) {
        super(other.getSchema(), GenericData.get());
        this.record = new GenericData.Record(other, true);
        for (Schema.Field f : this.schema().getFields()) {
            Object value;
            if (!GenericRecordBuilder.isValidValue(f, value = other.get(f.pos()))) continue;
            this.set(f, this.data().deepCopy(f.schema(), value));
        }
    }

    public Object get(String fieldName) {
        return this.get(this.schema().getField(fieldName));
    }

    public Object get(Schema.Field field) {
        return this.get(field.pos());
    }

    protected Object get(int pos) {
        return this.record.get(pos);
    }

    public GenericRecordBuilder set(String fieldName, Object value) {
        return this.set(this.schema().getField(fieldName), value);
    }

    public GenericRecordBuilder set(Schema.Field field, Object value) {
        return this.set(field, field.pos(), value);
    }

    protected GenericRecordBuilder set(int pos, Object value) {
        return this.set(this.fields()[pos], pos, value);
    }

    private GenericRecordBuilder set(Schema.Field field, int pos, Object value) {
        this.validate(field, value);
        this.record.put(pos, value);
        this.fieldSetFlags()[pos] = true;
        return this;
    }

    public boolean has(String fieldName) {
        return this.has(this.schema().getField(fieldName));
    }

    public boolean has(Schema.Field field) {
        return this.has(field.pos());
    }

    protected boolean has(int pos) {
        return this.fieldSetFlags()[pos];
    }

    public GenericRecordBuilder clear(String fieldName) {
        return this.clear(this.schema().getField(fieldName));
    }

    public GenericRecordBuilder clear(Schema.Field field) {
        return this.clear(field.pos());
    }

    protected GenericRecordBuilder clear(int pos) {
        this.record.put(pos, null);
        this.fieldSetFlags()[pos] = false;
        return this;
    }

    @Override
    public GenericData.Record build() {
        GenericData.Record record;
        try {
            record = new GenericData.Record(this.schema());
        }
        catch (Exception e) {
            throw new AvroRuntimeException(e);
        }
        for (Schema.Field field : this.fields()) {
            Object value;
            try {
                value = this.getWithDefault(field);
            }
            catch (IOException e) {
                throw new AvroRuntimeException(e);
            }
            if (value == null) continue;
            record.put(field.pos(), value);
        }
        return record;
    }

    private Object getWithDefault(Schema.Field field) throws IOException {
        return this.fieldSetFlags()[field.pos()] ? this.record.get(field.pos()) : this.defaultValue(field);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.record == null ? 0 : this.record.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        GenericRecordBuilder other = (GenericRecordBuilder)obj;
        if (this.record == null) {
            return other.record == null;
        }
        return this.record.equals(other.record);
    }
}

