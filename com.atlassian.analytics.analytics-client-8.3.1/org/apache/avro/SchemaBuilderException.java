/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;

public class SchemaBuilderException
extends AvroRuntimeException {
    public SchemaBuilderException(Throwable cause) {
        super(cause);
    }

    public SchemaBuilderException(String message) {
        super(message);
    }
}

