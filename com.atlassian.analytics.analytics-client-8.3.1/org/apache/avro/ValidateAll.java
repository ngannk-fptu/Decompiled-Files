/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.SchemaValidator;

public final class ValidateAll
implements SchemaValidator {
    private final SchemaValidationStrategy strategy;

    public ValidateAll(SchemaValidationStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void validate(Schema toValidate, Iterable<Schema> schemasInOrder) throws SchemaValidationException {
        for (Schema existing : schemasInOrder) {
            this.strategy.validate(toValidate, existing);
        }
    }
}

