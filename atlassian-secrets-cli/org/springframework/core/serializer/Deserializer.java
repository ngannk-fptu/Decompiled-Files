/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface Deserializer<T> {
    public T deserialize(InputStream var1) throws IOException;
}

