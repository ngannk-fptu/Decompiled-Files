/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.group;

import java.util.List;

public interface DefaultGroupSequenceProvider<T> {
    public List<Class<?>> getValidationGroups(T var1);
}

