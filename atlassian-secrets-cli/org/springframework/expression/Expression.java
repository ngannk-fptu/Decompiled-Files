/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

public interface Expression {
    public String getExpressionString();

    @Nullable
    public Object getValue() throws EvaluationException;

    @Nullable
    public <T> T getValue(@Nullable Class<T> var1) throws EvaluationException;

    @Nullable
    public Object getValue(Object var1) throws EvaluationException;

    @Nullable
    public <T> T getValue(Object var1, @Nullable Class<T> var2) throws EvaluationException;

    @Nullable
    public Object getValue(EvaluationContext var1) throws EvaluationException;

    @Nullable
    public Object getValue(EvaluationContext var1, Object var2) throws EvaluationException;

    @Nullable
    public <T> T getValue(EvaluationContext var1, @Nullable Class<T> var2) throws EvaluationException;

    @Nullable
    public <T> T getValue(EvaluationContext var1, Object var2, @Nullable Class<T> var3) throws EvaluationException;

    @Nullable
    public Class<?> getValueType() throws EvaluationException;

    @Nullable
    public Class<?> getValueType(Object var1) throws EvaluationException;

    @Nullable
    public Class<?> getValueType(EvaluationContext var1) throws EvaluationException;

    @Nullable
    public Class<?> getValueType(EvaluationContext var1, Object var2) throws EvaluationException;

    @Nullable
    public TypeDescriptor getValueTypeDescriptor() throws EvaluationException;

    @Nullable
    public TypeDescriptor getValueTypeDescriptor(Object var1) throws EvaluationException;

    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext var1) throws EvaluationException;

    @Nullable
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext var1, Object var2) throws EvaluationException;

    public boolean isWritable(Object var1) throws EvaluationException;

    public boolean isWritable(EvaluationContext var1) throws EvaluationException;

    public boolean isWritable(EvaluationContext var1, Object var2) throws EvaluationException;

    public void setValue(Object var1, @Nullable Object var2) throws EvaluationException;

    public void setValue(EvaluationContext var1, @Nullable Object var2) throws EvaluationException;

    public void setValue(EvaluationContext var1, Object var2, @Nullable Object var3) throws EvaluationException;
}

