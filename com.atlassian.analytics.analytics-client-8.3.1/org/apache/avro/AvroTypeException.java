/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import org.apache.avro.AvroRuntimeException;

public class AvroTypeException
extends AvroRuntimeException {
    public AvroTypeException(String message) {
        super(message);
    }

    public AvroTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

