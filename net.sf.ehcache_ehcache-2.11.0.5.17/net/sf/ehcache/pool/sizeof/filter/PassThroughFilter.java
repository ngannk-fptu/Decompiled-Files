/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof.filter;

import java.lang.reflect.Field;
import java.util.Collection;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;

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

