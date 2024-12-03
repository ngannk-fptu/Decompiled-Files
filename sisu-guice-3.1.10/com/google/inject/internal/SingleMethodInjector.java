/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.SingleMemberInjector;
import com.google.inject.internal.SingleParameterInjector;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.reflect.$FastMethod;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class SingleMethodInjector
implements SingleMemberInjector {
    private final InjectorImpl.MethodInvoker methodInvoker;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final InjectionPoint injectionPoint;

    SingleMethodInjector(InjectorImpl injector, InjectionPoint injectionPoint, Errors errors) throws ErrorsException {
        this.injectionPoint = injectionPoint;
        Method method = (Method)injectionPoint.getMember();
        this.methodInvoker = this.createMethodInvoker(method);
        this.parameterInjectors = injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
    }

    private InjectorImpl.MethodInvoker createMethodInvoker(final Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
            try {
                final $FastMethod fastMethod = BytecodeGen.newFastClass(method.getDeclaringClass(), BytecodeGen.Visibility.forMember(method)).getMethod(method);
                return new InjectorImpl.MethodInvoker(){

                    public Object invoke(Object target, Object ... parameters) throws IllegalAccessException, InvocationTargetException {
                        return fastMethod.invoke(target, parameters);
                    }
                };
            }
            catch ($CodeGenerationException $CodeGenerationException) {
                // empty catch block
            }
        }
        if (!Modifier.isPublic(modifiers) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
        return new InjectorImpl.MethodInvoker(){

            public Object invoke(Object target, Object ... parameters) throws IllegalAccessException, InvocationTargetException {
                return method.invoke(target, parameters);
            }
        };
    }

    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }

    public void inject(Errors errors, InternalContext context, Object o) {
        Object[] parameters;
        try {
            parameters = SingleParameterInjector.getAll(errors, context, this.parameterInjectors);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
            return;
        }
        try {
            this.methodInvoker.invoke(o, parameters);
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvocationTargetException userException) {
            Throwable cause = userException.getCause() != null ? userException.getCause() : userException;
            errors.withSource(this.injectionPoint).errorInjectingMethod(cause);
        }
    }
}

