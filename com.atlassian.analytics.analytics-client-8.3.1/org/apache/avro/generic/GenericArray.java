/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import java.util.List;
import org.apache.avro.generic.GenericContainer;

public interface GenericArray<T>
extends List<T>,
GenericContainer {
    public T peek();

    default public void reset() {
        this.clear();
    }

    default public void prune() {
    }

    public void reverse();
}

