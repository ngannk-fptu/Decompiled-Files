/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.lang.reflect.Method;

public interface AttributeMetadata<S, T> {
    public String getName();

    public Class<T> getAttributeType();

    public Method getGetterMethod();

    public Method getSetterMethod();

    public boolean isVersionAttribute();

    public boolean isHashKeyAttribute();
}

