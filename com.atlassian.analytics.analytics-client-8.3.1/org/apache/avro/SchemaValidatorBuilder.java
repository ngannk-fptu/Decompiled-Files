/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.SchemaValidator;
import org.apache.avro.ValidateAll;
import org.apache.avro.ValidateCanBeRead;
import org.apache.avro.ValidateCanRead;
import org.apache.avro.ValidateLatest;
import org.apache.avro.ValidateMutualRead;

public final class SchemaValidatorBuilder {
    private SchemaValidationStrategy strategy;

    public SchemaValidatorBuilder strategy(SchemaValidationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public SchemaValidatorBuilder canReadStrategy() {
        this.strategy = new ValidateCanRead();
        return this;
    }

    public SchemaValidatorBuilder canBeReadStrategy() {
        this.strategy = new ValidateCanBeRead();
        return this;
    }

    public SchemaValidatorBuilder mutualReadStrategy() {
        this.strategy = new ValidateMutualRead();
        return this;
    }

    public SchemaValidator validateLatest() {
        this.valid();
        return new ValidateLatest(this.strategy);
    }

    public SchemaValidator validateAll() {
        this.valid();
        return new ValidateAll(this.strategy);
    }

    private void valid() {
        if (null == this.strategy) {
            throw new AvroRuntimeException("SchemaValidationStrategy not specified in builder");
        }
    }
}

