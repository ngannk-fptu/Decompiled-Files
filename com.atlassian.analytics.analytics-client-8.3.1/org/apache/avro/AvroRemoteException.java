/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

public class AvroRemoteException
extends Exception {
    private Object value;

    protected AvroRemoteException() {
    }

    public AvroRemoteException(Throwable value) {
        this((Object)value.toString());
        this.initCause(value);
    }

    public AvroRemoteException(Object value) {
        super(value != null ? value.toString() : null);
        this.value = value;
    }

    public AvroRemoteException(Object value, Throwable cause) {
        super(value != null ? value.toString() : null, cause);
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
}

