/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.Schema;

public class SchemaValidationException
extends Exception {
    public SchemaValidationException(Schema reader, Schema writer) {
        super(SchemaValidationException.getMessage(reader, writer));
    }

    public SchemaValidationException(Schema reader, Schema writer, Throwable cause) {
        super(SchemaValidationException.getMessage(reader, writer), cause);
    }

    private static String getMessage(Schema reader, Schema writer) {
        return "Unable to read schema: \n" + writer.toString(true) + "\nusing schema:\n" + reader.toString(true);
    }
}

