/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanExpressionContext
 *  org.springframework.expression.AccessException
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypedValue
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.expression;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BeanExpressionContextAccessor
implements PropertyAccessor {
    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return target instanceof BeanExpressionContext && ((BeanExpressionContext)target).containsObject(name);
    }

    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        Assert.state((boolean)(target instanceof BeanExpressionContext), (String)"Target must be of type BeanExpressionContext");
        return new TypedValue(((BeanExpressionContext)target).getObject(name));
    }

    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return false;
    }

    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
        throw new AccessException("Beans in a BeanFactory are read-only");
    }

    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{BeanExpressionContext.class};
    }
}

