/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.serializer;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface Serializer<T> {
    public void serialize(T var1, OutputStream var2) throws IOException;
}

