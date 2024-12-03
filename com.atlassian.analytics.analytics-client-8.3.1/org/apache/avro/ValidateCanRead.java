/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.ValidateMutualRead;

class ValidateCanRead
implements SchemaValidationStrategy {
    ValidateCanRead() {
    }

    @Override
    public void validate(Schema toValidate, Schema existing) throws SchemaValidationException {
        ValidateMutualRead.canRead(existing, toValidate);
    }
}

