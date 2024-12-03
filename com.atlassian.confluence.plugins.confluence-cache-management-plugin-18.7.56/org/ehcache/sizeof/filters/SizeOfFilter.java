/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.filters;

import java.lang.reflect.Field;
import java.util.Collection;

public interface SizeOfFilter {
    public Collection<Field> filterFields(Class<?> var1, Collection<Field> var2);

    public boolean filterClass(Class<?> var1);
}

