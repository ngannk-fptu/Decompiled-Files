/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.util.Iterator;
import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.SchemaValidator;

public final class ValidateLatest
implements SchemaValidator {
    private final SchemaValidationStrategy strategy;

    public ValidateLatest(SchemaValidationStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void validate(Schema toValidate, Iterable<Schema> schemasInOrder) throws SchemaValidationException {
        Iterator<Schema> schemas = schemasInOrder.iterator();
        if (schemas.hasNext()) {
            Schema existing = schemas.next();
            this.strategy.validate(toValidate, existing);
        }
    }
}

