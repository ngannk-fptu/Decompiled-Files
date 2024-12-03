/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hibernate.type.Type;

public class FilterDefinition
implements Serializable {
    private final String filterName;
    private final String defaultFilterCondition;
    private final Map<String, Type> parameterTypes = new HashMap<String, Type>();

    public FilterDefinition(String name, String defaultCondition, Map<String, Type> parameterTypes) {
        this.filterName = name;
        this.defaultFilterCondition = defaultCondition;
        if (parameterTypes != null) {
            this.parameterTypes.putAll(parameterTypes);
        }
    }

    public String getFilterName() {
        return this.filterName;
    }

    public Set<String> getParameterNames() {
        return this.parameterTypes.keySet();
    }

    public Type getParameterType(String parameterName) {
        return this.parameterTypes.get(parameterName);
    }

    public String getDefaultFilterCondition() {
        return this.defaultFilterCondition;
    }

    public Map<String, Type> getParameterTypes() {
        return this.parameterTypes;
    }
}

