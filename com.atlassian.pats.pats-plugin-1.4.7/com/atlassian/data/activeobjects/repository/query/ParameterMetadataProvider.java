/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.EscapeCharacter;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.dsl.Param;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class ParameterMetadataProvider {
    private final Iterator<? extends Parameter> parameters;
    private final List<ParameterMetadata<?>> expressions;
    @Nullable
    private final Iterator<Object> bindableParameterValues;
    private final EscapeCharacter escape;

    public ParameterMetadataProvider(ParametersParameterAccessor accessor, EscapeCharacter escape) {
        this(accessor.iterator(), accessor.getParameters(), escape);
    }

    public ParameterMetadataProvider(Parameters<?, ?> parameters, EscapeCharacter escape) {
        this(null, parameters, escape);
    }

    private ParameterMetadataProvider(@Nullable Iterator<Object> bindableParameterValues, Parameters<?, ?> parameters, EscapeCharacter escape) {
        Assert.notNull(parameters, (String)"Parameters must not be null!");
        this.parameters = ((Parameters)parameters.getBindableParameters()).iterator();
        this.expressions = new ArrayList();
        this.bindableParameterValues = bindableParameterValues;
        this.escape = escape;
    }

    public List<ParameterMetadata<?>> getExpressions() {
        return Collections.unmodifiableList(this.expressions);
    }

    public <T> ParameterMetadata<T> next(Part part) {
        Assert.isTrue((boolean)this.parameters.hasNext(), () -> String.format("No parameter available for part %s.", part));
        Parameter parameter = this.parameters.next();
        return this.next(part, parameter.getType(), parameter);
    }

    public <T> ParameterMetadata<? extends T> next(Part part, Class<T> type) {
        Parameter parameter = this.parameters.next();
        Class<Object> typeToUse = ClassUtils.isAssignable(type, parameter.getType()) ? parameter.getType() : type;
        return this.next(part, typeToUse, parameter);
    }

    private <T> ParameterMetadata<T> next(Part part, Class<T> type, Parameter parameter) {
        Assert.notNull(type, (String)"Type must not be null!");
        Class reifiedType = Expression.class.equals(type) ? Object.class : type;
        Supplier<String> name = () -> parameter.getName().orElseThrow(() -> new IllegalArgumentException("o_O Parameter needs to be named"));
        Param<Object> expression = parameter.isExplicitlyNamed() ? new Param<Object>(reifiedType, name.get()) : new Param<Object>(reifiedType);
        Object value = this.bindableParameterValues == null ? ParameterMetadata.PLACEHOLDER : this.bindableParameterValues.next();
        ParameterMetadata<Object> metadata = new ParameterMetadata<Object>(expression, part.getType(), value, this.escape);
        this.expressions.add(metadata);
        return metadata;
    }

    EscapeCharacter getEscape() {
        return this.escape;
    }

    static class ParameterMetadata<T> {
        static final Object PLACEHOLDER = new Object();
        private final Part.Type type;
        private final ParamExpression<T> expression;
        private final EscapeCharacter escape;

        public ParameterMetadata(ParamExpression<T> expression, Part.Type type, @Nullable Object value, EscapeCharacter escape) {
            this.expression = expression;
            this.type = value == null && Part.Type.SIMPLE_PROPERTY.equals((Object)type) ? Part.Type.IS_NULL : type;
            this.escape = escape;
        }

        public ParamExpression<T> getExpression() {
            return this.expression;
        }

        public boolean isIsNullParameter() {
            return Part.Type.IS_NULL.equals((Object)this.type);
        }

        @Nullable
        public Object prepare(Object value) {
            Assert.notNull((Object)value, (String)"Value must not be null!");
            Class expressionType = this.expression.getType();
            if (String.class.equals(expressionType)) {
                switch (this.type) {
                    case STARTING_WITH: {
                        return String.format("%s%%", this.escape.escape(value.toString()));
                    }
                    case ENDING_WITH: {
                        return String.format("%%%s", this.escape.escape(value.toString()));
                    }
                    case CONTAINING: 
                    case NOT_CONTAINING: {
                        return String.format("%%%s%%", this.escape.escape(value.toString()));
                    }
                }
                return value;
            }
            return Collection.class.isAssignableFrom(expressionType) ? this.potentiallyConvertEmptyCollection(ParameterMetadata.toCollection(value)) : value;
        }

        @Nullable
        private static Collection<?> toCollection(@Nullable Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof Collection) {
                return (Collection)value;
            }
            if (ObjectUtils.isArray((Object)value)) {
                return Arrays.asList(ObjectUtils.toObjectArray((Object)value));
            }
            return Collections.singleton(value);
        }

        @Nullable
        public <T> Collection<T> potentiallyConvertEmptyCollection(@Nullable Collection<T> collection) {
            return collection;
        }
    }
}

