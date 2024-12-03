/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class FilterImpl
implements Filter,
Serializable {
    public static final String MARKER = "$FILTER_PLACEHOLDER$";
    private transient FilterDefinition definition;
    private String filterName;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    void afterDeserialize(SessionFactoryImplementor factory) {
        this.definition = factory.getFilterDefinition(this.filterName);
        this.validate();
    }

    public FilterImpl(FilterDefinition configuration) {
        this.definition = configuration;
        this.filterName = this.definition.getFilterName();
    }

    @Override
    public FilterDefinition getFilterDefinition() {
        return this.definition;
    }

    @Override
    public String getName() {
        return this.definition.getFilterName();
    }

    public Map<String, ?> getParameters() {
        return this.parameters;
    }

    @Override
    public Filter setParameter(String name, Object value) throws IllegalArgumentException {
        Type type = this.definition.getParameterType(name);
        if (type == null) {
            throw new IllegalArgumentException("Undefined filter parameter [" + name + "]");
        }
        if (value != null && !type.getReturnedClass().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type for parameter [" + name + "]");
        }
        this.parameters.put(name, value);
        return this;
    }

    @Override
    public Filter setParameterList(String name, Collection values) throws HibernateException {
        if (values == null) {
            throw new IllegalArgumentException("Collection must be not null!");
        }
        Type type = this.definition.getParameterType(name);
        if (type == null) {
            throw new HibernateException("Undefined filter parameter [" + name + "]");
        }
        if (!values.isEmpty()) {
            Class<?> elementClass = values.iterator().next().getClass();
            if (!type.getReturnedClass().isAssignableFrom(elementClass)) {
                throw new HibernateException("Incorrect type for parameter [" + name + "]");
            }
        }
        this.parameters.put(name, values);
        return this;
    }

    @Override
    public Filter setParameterList(String name, Object[] values) throws IllegalArgumentException {
        return this.setParameterList(name, Arrays.asList(values));
    }

    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    @Override
    public void validate() throws HibernateException {
        for (String parameterName : this.definition.getParameterNames()) {
            if (this.parameters.get(parameterName) != null) continue;
            throw new HibernateException("Filter [" + this.getName() + "] parameter [" + parameterName + "] value not set");
        }
    }
}

