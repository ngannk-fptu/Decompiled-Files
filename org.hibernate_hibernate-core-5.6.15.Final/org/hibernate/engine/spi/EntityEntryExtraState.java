/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

public interface EntityEntryExtraState {
    public void addExtraState(EntityEntryExtraState var1);

    public <T extends EntityEntryExtraState> T getExtraState(Class<T> var1);
}

