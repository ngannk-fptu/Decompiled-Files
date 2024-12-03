/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.data;

import org.apache.avro.data.RecordBuilder;

public interface ErrorBuilder<T>
extends RecordBuilder<T> {
    public Object getValue();

    public ErrorBuilder<T> setValue(Object var1);

    public boolean hasValue();

    public ErrorBuilder<T> clearValue();

    public Throwable getCause();

    public ErrorBuilder<T> setCause(Throwable var1);

    public boolean hasCause();

    public ErrorBuilder<T> clearCause();
}

