/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public class UnresolvedUnionException
extends AvroRuntimeException {
    private Object unresolvedDatum;
    private Schema unionSchema;

    public UnresolvedUnionException(Schema unionSchema, Object unresolvedDatum) {
        super("Not in union " + unionSchema + ": " + unresolvedDatum);
        this.unionSchema = unionSchema;
        this.unresolvedDatum = unresolvedDatum;
    }

    public UnresolvedUnionException(Schema unionSchema, Schema.Field field, Object unresolvedDatum) {
        super("Not in union " + unionSchema + ": " + unresolvedDatum + " (field=" + field.name() + ")");
        this.unionSchema = unionSchema;
        this.unresolvedDatum = unresolvedDatum;
    }

    public Object getUnresolvedDatum() {
        return this.unresolvedDatum;
    }

    public Schema getUnionSchema() {
        return this.unionSchema;
    }
}

