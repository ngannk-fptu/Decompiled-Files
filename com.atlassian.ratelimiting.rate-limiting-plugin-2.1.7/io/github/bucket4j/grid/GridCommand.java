/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.grid.GridBucketState;
import java.io.Serializable;

public interface GridCommand<T extends Serializable>
extends Serializable {
    public T execute(GridBucketState var1, long var2);

    public boolean isBucketStateModified();
}

