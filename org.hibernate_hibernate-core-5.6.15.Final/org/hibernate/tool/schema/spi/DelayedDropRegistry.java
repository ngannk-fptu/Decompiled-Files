/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.tool.schema.spi.DelayedDropAction;

public interface DelayedDropRegistry {
    public void registerOnCloseAction(DelayedDropAction var1);
}

