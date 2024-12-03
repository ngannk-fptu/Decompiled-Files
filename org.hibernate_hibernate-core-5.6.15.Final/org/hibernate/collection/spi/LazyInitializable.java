/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.spi;

import org.hibernate.Incubating;

@Incubating
public interface LazyInitializable {
    public boolean wasInitialized();

    public void forceInitialization();
}

