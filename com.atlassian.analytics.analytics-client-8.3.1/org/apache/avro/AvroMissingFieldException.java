/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.util.ArrayList;
import java.util.List;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public class AvroMissingFieldException
extends AvroRuntimeException {
    private List<Schema.Field> chainOfFields = new ArrayList<Schema.Field>(8);

    public AvroMissingFieldException(String message, Schema.Field field) {
        super(message);
        this.chainOfFields.add(field);
    }

    public void addParentField(Schema.Field field) {
        this.chainOfFields.add(field);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Schema.Field field : this.chainOfFields) {
            result.insert(0, " --> " + field.name());
        }
        return "Path in schema:" + result;
    }
}

