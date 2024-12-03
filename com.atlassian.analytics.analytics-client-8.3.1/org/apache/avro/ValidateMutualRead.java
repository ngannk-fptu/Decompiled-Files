/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaValidationException;
import org.apache.avro.SchemaValidationStrategy;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.apache.avro.io.parsing.Symbol;

class ValidateMutualRead
implements SchemaValidationStrategy {
    ValidateMutualRead() {
    }

    @Override
    public void validate(Schema toValidate, Schema existing) throws SchemaValidationException {
        ValidateMutualRead.canRead(toValidate, existing);
        ValidateMutualRead.canRead(existing, toValidate);
    }

    static void canRead(Schema writtenWith, Schema readUsing) throws SchemaValidationException {
        boolean error;
        try {
            error = Symbol.hasErrors(new ResolvingGrammarGenerator().generate(writtenWith, readUsing));
        }
        catch (IOException e) {
            throw new SchemaValidationException(readUsing, writtenWith, e);
        }
        if (error) {
            throw new SchemaValidationException(readUsing, writtenWith);
        }
    }
}

