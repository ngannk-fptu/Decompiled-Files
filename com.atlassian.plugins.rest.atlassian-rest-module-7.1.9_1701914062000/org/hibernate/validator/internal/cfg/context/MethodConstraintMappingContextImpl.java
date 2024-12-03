/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.reflect.Method;
import org.hibernate.validator.cfg.context.MethodConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.ExecutableConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;

class MethodConstraintMappingContextImpl
extends ExecutableConstraintMappingContextImpl
implements MethodConstraintMappingContext {
    MethodConstraintMappingContextImpl(TypeConstraintMappingContextImpl<?> typeContext, Method method) {
        super(typeContext, method);
    }

    @Override
    public MethodConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.typeContext.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.executable, ignoreAnnotations);
        return this;
    }
}

