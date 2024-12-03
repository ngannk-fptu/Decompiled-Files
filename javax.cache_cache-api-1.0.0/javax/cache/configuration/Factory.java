/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.io.Serializable;

public interface Factory<T>
extends Serializable {
    public T create();
}

