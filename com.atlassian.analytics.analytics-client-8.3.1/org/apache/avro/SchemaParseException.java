/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;

public class SchemaParseException
extends AvroRuntimeException {
    public SchemaParseException(Throwable cause) {
        super(cause);
    }

    public SchemaParseException(String message) {
        super(message);
    }
}

