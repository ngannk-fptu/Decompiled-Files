/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.SingleMemberInjector;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Field;

final class SingleFieldInjector
implements SingleMemberInjector {
    final Field field;
    final InjectionPoint injectionPoint;
    final Dependency<?> dependency;
    final InternalFactory<?> factory;

    public SingleFieldInjector(InjectorImpl injector, InjectionPoint injectionPoint, Errors errors) throws ErrorsException {
        this.injectionPoint = injectionPoint;
        this.field = (Field)injectionPoint.getMember();
        this.dependency = injectionPoint.getDependencies().get(0);
        this.field.setAccessible(true);
        this.factory = injector.getInternalFactory(this.dependency.getKey(), errors, InjectorImpl.JitLimitation.NO_JIT);
    }

    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }

    public void inject(Errors errors, InternalContext context, Object o) {
        errors = errors.withSource(this.dependency);
        Dependency previous = context.setDependency(this.dependency);
        try {
            Object value = this.factory.get(errors, context, this.dependency, false);
            this.field.set(o, value);
        }
        catch (ErrorsException e) {
            errors.withSource(this.injectionPoint).merge(e.getErrors());
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
        finally {
            context.setDependency(previous);
        }
    }
}

