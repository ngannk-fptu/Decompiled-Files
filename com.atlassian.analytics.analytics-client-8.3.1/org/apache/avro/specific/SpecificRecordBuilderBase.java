/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilderBase;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;

public abstract class SpecificRecordBuilderBase<T extends SpecificRecord>
extends RecordBuilderBase<T> {
    protected SpecificRecordBuilderBase(Schema schema) {
        super(schema, (GenericData)SpecificData.getForSchema(schema));
    }

    protected SpecificRecordBuilderBase(Schema schema, SpecificData model) {
        super(schema, (GenericData)model);
    }

    protected SpecificRecordBuilderBase(SpecificRecordBuilderBase<T> other) {
        super(other, other.data());
    }

    protected SpecificRecordBuilderBase(T other) {
        super(other.getSchema(), (GenericData)SpecificData.getForSchema(other.getSchema()));
    }
}

