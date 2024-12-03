/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.json.marshal.wrapped;

import com.atlassian.json.marshal.Jsonable;
import java.io.IOException;
import java.io.Writer;

public abstract class WrappingJsonable<T>
implements Jsonable {
    private final T value;

    public WrappingJsonable(T value) {
        this.value = value;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(null == this.value ? "null" : this.convertValueToString(this.value));
    }

    protected abstract String convertValueToString(T var1);
}

