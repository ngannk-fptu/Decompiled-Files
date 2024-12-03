/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.lifecycle.services;

import java.io.Closeable;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface OptionalService<T>
extends Closeable {
    public boolean isAvailable();

    public T get();

    public List<T> getAll();

    @Override
    public void close();
}

