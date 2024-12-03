/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query;

import java.util.Iterator;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ParametersParameterAccessor
implements ParameterAccessor {
    private final Parameters<?, ?> parameters;
    private final Object[] values;

    public ParametersParameterAccessor(Parameters<?, ?> parameters, Object[] values) {
        Assert.notNull(parameters, (String)"Parameters must not be null!");
        Assert.notNull((Object)values, (String)"Values must not be null!");
        Assert.isTrue((parameters.getNumberOfParameters() == values.length ? 1 : 0) != 0, (String)"Invalid number of parameters given!");
        this.parameters = parameters;
        if (ParametersParameterAccessor.requiresUnwrapping(values)) {
            this.values = new Object[values.length];
            for (int i = 0; i < values.length; ++i) {
                this.values[i] = QueryExecutionConverters.unwrap(values[i]);
            }
        } else {
            this.values = values;
        }
    }

    private static boolean requiresUnwrapping(Object[] values) {
        for (Object value : values) {
            if (value == null || !QueryExecutionConverters.supports(value.getClass()) && !ReactiveWrapperConverters.supports(value.getClass())) continue;
            return true;
        }
        return false;
    }

    public Parameters<?, ?> getParameters() {
        return this.parameters;
    }

    protected Object[] getValues() {
        return this.values;
    }

    @Override
    public Pageable getPageable() {
        if (!this.parameters.hasPageableParameter()) {
            return Pageable.unpaged();
        }
        Pageable pageable = (Pageable)this.values[this.parameters.getPageableIndex()];
        return pageable == null ? Pageable.unpaged() : pageable;
    }

    @Override
    public Sort getSort() {
        if (this.parameters.hasSortParameter()) {
            Sort sort = (Sort)this.values[this.parameters.getSortIndex()];
            return sort == null ? Sort.unsorted() : sort;
        }
        if (this.parameters.hasPageableParameter()) {
            return this.getPageable().getSort();
        }
        return Sort.unsorted();
    }

    @Override
    public Optional<Class<?>> getDynamicProjection() {
        return Optional.ofNullable(this.parameters.hasDynamicProjection() ? (Class)this.values[this.parameters.getDynamicProjectionIndex()] : null);
    }

    @Override
    @Nullable
    public Class<?> findDynamicProjection() {
        return this.parameters.hasDynamicProjection() ? (Class)this.values[this.parameters.getDynamicProjectionIndex()] : null;
    }

    protected <T> T getValue(int index) {
        return (T)this.values[index];
    }

    @Override
    public Object getBindableValue(int index) {
        return this.values[((Parameter)this.parameters.getBindableParameter(index)).getIndex()];
    }

    @Override
    public boolean hasBindableNullValue() {
        for (Parameter parameter : this.parameters.getBindableParameters()) {
            if (this.values[parameter.getIndex()] != null) continue;
            return true;
        }
        return false;
    }

    public BindableParameterIterator iterator() {
        return new BindableParameterIterator(this);
    }

    private static class BindableParameterIterator
    implements Iterator<Object> {
        private final int bindableParameterCount;
        private final ParameterAccessor accessor;
        private int currentIndex = 0;

        public BindableParameterIterator(ParametersParameterAccessor accessor) {
            Assert.notNull((Object)accessor, (String)"ParametersParameterAccessor must not be null!");
            this.accessor = accessor;
            this.bindableParameterCount = ((Parameters)accessor.getParameters().getBindableParameters()).getNumberOfParameters();
        }

        @Override
        public Object next() {
            return this.accessor.getBindableValue(this.currentIndex++);
        }

        @Override
        public boolean hasNext() {
            return this.bindableParameterCount > this.currentIndex;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

