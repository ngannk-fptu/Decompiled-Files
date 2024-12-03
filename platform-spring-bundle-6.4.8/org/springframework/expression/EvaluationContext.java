/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

public interface EvaluationContext {
    public TypedValue getRootObject();

    public List<PropertyAccessor> getPropertyAccessors();

    public List<ConstructorResolver> getConstructorResolvers();

    public List<MethodResolver> getMethodResolvers();

    @Nullable
    public BeanResolver getBeanResolver();

    public TypeLocator getTypeLocator();

    public TypeConverter getTypeConverter();

    public TypeComparator getTypeComparator();

    public OperatorOverloader getOperatorOverloader();

    default public TypedValue assignVariable(String name, Supplier<TypedValue> valueSupplier) {
        TypedValue typedValue = valueSupplier.get();
        this.setVariable(name, typedValue.getValue());
        return typedValue;
    }

    public void setVariable(String var1, @Nullable Object var2);

    @Nullable
    public Object lookupVariable(String var1);
}

