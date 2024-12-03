/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

public class AvroRuntimeException
extends RuntimeException {
    public AvroRuntimeException(Throwable cause) {
        super(cause);
    }

    public AvroRuntimeException(String message) {
        super(message);
    }

    public AvroRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

