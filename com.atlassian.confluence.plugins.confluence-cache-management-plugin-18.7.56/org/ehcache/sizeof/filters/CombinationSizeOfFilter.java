/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.filters;

import java.lang.reflect.Field;
import java.util.Collection;
import org.ehcache.sizeof.filters.SizeOfFilter;

public class CombinationSizeOfFilter
implements SizeOfFilter {
    private final SizeOfFilter[] filters;

    public CombinationSizeOfFilter(SizeOfFilter ... filters) {
        this.filters = filters;
    }

    @Override
    public Collection<Field> filterFields(Class<?> klazz, Collection<Field> fields) {
        Collection<Field> current = fields;
        for (SizeOfFilter filter : this.filters) {
            current = filter.filterFields(klazz, current);
        }
        return current;
    }

    @Override
    public boolean filterClass(Class<?> klazz) {
        for (SizeOfFilter filter : this.filters) {
            if (filter.filterClass(klazz)) continue;
            return false;
        }
        return true;
    }
}

