/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import org.ehcache.sizeof.filters.SizeOfFilter;

public class PassThroughFilter
implements SizeOfFilter {
    @Override
    public Collection<Field> filterFields(Class<?> klazz, Collection<Field> fields) {
        return fields;
    }

    @Override
    public boolean filterClass(Class<?> klazz) {
        return true;
    }
}

