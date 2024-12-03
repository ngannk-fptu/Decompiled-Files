/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.env.Environment
 *  org.springframework.expression.AccessException
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypedValue
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.expression;

import org.springframework.core.env.Environment;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EnvironmentAccessor
implements PropertyAccessor {
    public Class<?>[] getSpecificTargetClasses() {
        return new Class[]{Environment.class};
    }

    public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return true;
    }

    public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        Assert.state((boolean)(target instanceof Environment), (String)"Target must be of type Environment");
        return new TypedValue((Object)((Environment)target).getProperty(name));
    }

    public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
        return false;
    }

    public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException {
    }
}

