/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.reflect.Constructor;
import org.hibernate.validator.cfg.context.ConstructorConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.ExecutableConstraintMappingContextImpl;
import org.hibernate.validator.internal.cfg.context.TypeConstraintMappingContextImpl;

class ConstructorConstraintMappingContextImpl
extends ExecutableConstraintMappingContextImpl
implements ConstructorConstraintMappingContext {
    <T> ConstructorConstraintMappingContextImpl(TypeConstraintMappingContextImpl<T> typeContext, Constructor<T> constructor) {
        super(typeContext, constructor);
    }

    @Override
    public ConstructorConstraintMappingContext ignoreAnnotations(boolean ignoreAnnotations) {
        this.typeContext.mapping.getAnnotationProcessingOptions().ignoreConstraintAnnotationsOnMember(this.executable, ignoreAnnotations);
        return this;
    }
}

