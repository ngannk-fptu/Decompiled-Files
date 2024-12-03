/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.support.ManagedList;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ManagedArray
extends ManagedList<Object> {
    @Nullable
    volatile Class<?> resolvedElementType;

    public ManagedArray(String elementTypeName, int size) {
        super(size);
        Assert.notNull((Object)elementTypeName, (String)"elementTypeName must not be null");
        this.setElementTypeName(elementTypeName);
    }
}

