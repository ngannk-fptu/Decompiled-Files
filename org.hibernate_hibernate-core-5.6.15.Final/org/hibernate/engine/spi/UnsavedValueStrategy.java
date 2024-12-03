/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

public interface UnsavedValueStrategy {
    public Boolean isUnsaved(Object var1);

    public Object getDefaultValue(Object var1);
}

