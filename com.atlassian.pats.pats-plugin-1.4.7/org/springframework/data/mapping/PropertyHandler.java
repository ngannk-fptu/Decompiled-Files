/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.PersistentProperty;

public interface PropertyHandler<P extends PersistentProperty<P>> {
    public void doWithPersistentProperty(P var1);
}

