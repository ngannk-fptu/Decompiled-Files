/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.Filter;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.FilterImpl;
import org.hibernate.type.Type;

public final class FilterKey
implements Serializable {
    private final String filterName;
    private final Map<String, TypedValue> filterParameters = new HashMap<String, TypedValue>();

    FilterKey(String name, Map<String, ?> params, Map<String, Type> types) {
        this.filterName = name;
        for (Map.Entry<String, ?> paramEntry : params.entrySet()) {
            Type type = types.get(paramEntry.getKey());
            this.filterParameters.put(paramEntry.getKey(), new TypedValue(type, paramEntry.getValue()));
        }
    }

    public int hashCode() {
        int result = 13;
        result = 37 * result + this.filterName.hashCode();
        result = 37 * result + this.filterParameters.hashCode();
        return result;
    }

    public boolean equals(Object other) {
        if (!(other instanceof FilterKey)) {
            return false;
        }
        FilterKey that = (FilterKey)other;
        return that.filterName.equals(this.filterName) && that.filterParameters.equals(this.filterParameters);
    }

    public String toString() {
        return "FilterKey[" + this.filterName + this.filterParameters + ']';
    }

    public static Set<FilterKey> createFilterKeys(Map<String, Filter> enabledFilters) {
        if (enabledFilters.size() == 0) {
            return null;
        }
        HashSet<FilterKey> result = new HashSet<FilterKey>();
        for (Filter filter : enabledFilters.values()) {
            FilterKey key = new FilterKey(filter.getName(), ((FilterImpl)filter).getParameters(), filter.getFilterDefinition().getParameterTypes());
            result.add(key);
        }
        return result;
    }
}

