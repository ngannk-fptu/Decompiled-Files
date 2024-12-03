/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;

public interface SchemaValidationStrategy {
    public void validate(Schema var1, Schema var2) throws SchemaValidationException;
}

