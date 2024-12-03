/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.ValidateMutualRead;

class ValidateCanBeRead
implements SchemaValidationStrategy {
    ValidateCanBeRead() {
    }

    @Override
    public void validate(Schema toValidate, Schema existing) throws SchemaValidationException {
        ValidateMutualRead.canRead(toValidate, existing);
    }
}

