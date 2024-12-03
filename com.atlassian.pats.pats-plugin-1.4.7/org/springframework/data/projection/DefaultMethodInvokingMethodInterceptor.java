/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.ProxyMethodInvocation
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.projection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

public class DefaultMethodInvokingMethodInterceptor
implements MethodInterceptor {
    private final MethodHandleLookup methodHandleLookup = MethodHandleLookup.getMethodHandleLookup();
    private final Map<Method, MethodHandle> methodHandleCache = new ConcurrentReferenceHashMap(10, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    public static boolean hasDefaultMethods(Class<?> interfaceClass) {
        Method[] methods;
        for (Method method : methods = ReflectionUtils.getAllDeclaredMethods(interfaceClass)) {
            if (!method.isDefault()) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (!method.isDefault()) {
            return invocation.proceed();
        }
        Object[] arguments = invocation.getArguments();
        Object proxy = ((ProxyMethodInvocation)invocation).getProxy();
        return this.getMethodHandle(method).bindTo(proxy).invokeWithArguments(arguments);
    }

    private MethodHandle getMethodHandle(Method method) throws Exception {
        MethodHandle handle = this.methodHandleCache.get(method);
        if (handle == null) {
            handle = this.methodHandleLookup.lookup(method);
            this.methodHandleCache.put(method, handle);
        }
        return handle;
    }

    static enum MethodHandleLookup {
        ENCAPSULATED{
            @Nullable
            private final Method privateLookupIn = ReflectionUtils.findMethod(MethodHandles.class, (String)"privateLookupIn", (Class[])new Class[]{Class.class, MethodHandles.Lookup.class});

            @Override
            MethodHandle lookup(Method method) throws ReflectiveOperationException {
                if (this.privateLookupIn == null) {
                    throw new IllegalStateException("Could not obtain MethodHandles.privateLookupIn!");
                }
                return MethodHandleLookup.doLookup(method, this.getLookup(method.getDeclaringClass(), this.privateLookupIn));
            }

            @Override
            boolean isAvailable() {
                return this.privateLookupIn != null;
            }

            private MethodHandles.Lookup getLookup(Class<?> declaringClass, Method privateLookupIn) {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                try {
                    return (MethodHandles.Lookup)privateLookupIn.invoke(MethodHandles.class, declaringClass, lookup);
                }
                catch (ReflectiveOperationException e) {
                    return lookup;
                }
            }
        }
        ,
        OPEN{
            private final Lazy<Constructor<MethodHandles.Lookup>> constructor = Lazy.of(() -> MethodHandleLookup.access$200());

            @Override
            MethodHandle lookup(Method method) throws ReflectiveOperationException {
                if (!this.isAvailable()) {
                    throw new IllegalStateException("Could not obtain MethodHandles.lookup constructor!");
                }
                Constructor<MethodHandles.Lookup> constructor = this.constructor.get();
                return constructor.newInstance(method.getDeclaringClass()).unreflectSpecial(method, method.getDeclaringClass());
            }

            @Override
            boolean isAvailable() {
                return this.constructor.orElse(null) != null;
            }
        }
        ,
        FALLBACK{

            @Override
            MethodHandle lookup(Method method) throws ReflectiveOperationException {
                return MethodHandleLookup.doLookup(method, MethodHandles.lookup());
            }

            @Override
            boolean isAvailable() {
                return true;
            }
        };


        private static MethodHandle doLookup(Method method, MethodHandles.Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
            MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
            if (Modifier.isStatic(method.getModifiers())) {
                return lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType);
            }
            return lookup.findSpecial(method.getDeclaringClass(), method.getName(), methodType, method.getDeclaringClass());
        }

        abstract MethodHandle lookup(Method var1) throws ReflectiveOperationException;

        abstract boolean isAvailable();

        public static MethodHandleLookup getMethodHandleLookup() {
            for (MethodHandleLookup it : MethodHandleLookup.values()) {
                if (!it.isAvailable()) continue;
                return it;
            }
            throw new IllegalStateException("No MethodHandleLookup available!");
        }

        @Nullable
        private static Constructor<MethodHandles.Lookup> getLookupConstructor() {
            try {
                Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                ReflectionUtils.makeAccessible(constructor);
                return constructor;
            }
            catch (Exception ex) {
                if (ex.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
                    return null;
                }
                throw new IllegalStateException(ex);
            }
        }
    }
}

