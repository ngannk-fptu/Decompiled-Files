/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.reflect.FieldAccess;
import org.apache.avro.reflect.FieldAccessor;

public class ReflectionUtil {
    private static FieldAccess fieldAccess;

    private ReflectionUtil() {
    }

    static void resetFieldAccess() {
        FieldAccess access = null;
        try {
            FieldAccess unsafeAccess;
            if (null == System.getProperty("avro.disable.unsafe") && ReflectionUtil.validate(unsafeAccess = ReflectionUtil.load("org.apache.avro.reflect.FieldAccessUnsafe", FieldAccess.class))) {
                access = unsafeAccess;
            }
        }
        catch (Throwable unsafeAccess) {
            // empty catch block
        }
        if (access == null) {
            try {
                FieldAccess reflectAccess = ReflectionUtil.load("org.apache.avro.reflect.FieldAccessReflect", FieldAccess.class);
                if (ReflectionUtil.validate(reflectAccess)) {
                    access = reflectAccess;
                }
            }
            catch (Throwable oops) {
                throw new AvroRuntimeException("Unable to load a functional FieldAccess class!");
            }
        }
        fieldAccess = access;
    }

    private static <T> T load(String name, Class<T> type) throws Exception {
        return ReflectionUtil.class.getClassLoader().loadClass(name).asSubclass(type).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
    }

    public static FieldAccess getFieldAccess() {
        return fieldAccess;
    }

    private static boolean validate(FieldAccess access) throws Exception {
        return new AccessorTestClass().validate(access);
    }

    protected static Map<TypeVariable<?>, Type> resolveTypeVariables(Class<?> iface) {
        return ReflectionUtil.resolveTypeVariables(iface, new IdentityHashMap());
    }

    private static Map<TypeVariable<?>, Type> resolveTypeVariables(Class<?> iface, Map<TypeVariable<?>, Type> reuse) {
        for (Type type : iface.getGenericInterfaces()) {
            ParameterizedType parameterizedType;
            Type rawType;
            if (!(type instanceof ParameterizedType) || !((rawType = (parameterizedType = (ParameterizedType)type).getRawType()) instanceof Class)) continue;
            Class classType = (Class)rawType;
            TypeVariable<Class<T>>[] typeParameters = classType.getTypeParameters();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeParameters.length; ++i) {
                reuse.putIfAbsent(typeParameters[i], reuse.getOrDefault(actualTypeArguments[i], actualTypeArguments[i]));
            }
            ReflectionUtil.resolveTypeVariables(classType, reuse);
        }
        return reuse;
    }

    private static <D> Supplier<D> getConstructorAsSupplier(Class<D> clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle constructorHandle = lookup.findConstructor(clazz, MethodType.methodType(Void.TYPE));
            CallSite site = LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(Supplier.class), constructorHandle.type().generic(), constructorHandle, constructorHandle.type());
            return site.getTarget().invokeExact();
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static <V, R> Supplier<R> getOneArgConstructorAsSupplier(Class<R> clazz, Class<V> argumentClass, V argument) {
        Function supplierFunction = ReflectionUtil.getConstructorAsFunction(argumentClass, clazz);
        if (supplierFunction != null) {
            return () -> supplierFunction.apply(argument);
        }
        return null;
    }

    public static <V, R> Function<V, R> getConstructorAsFunction(Class<V> parameterClass, Class<R> clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle constructorHandle = lookup.findConstructor(clazz, MethodType.methodType(Void.TYPE, parameterClass));
            CallSite site = LambdaMetafactory.metafactory(lookup, "apply", MethodType.methodType(Function.class), constructorHandle.type().generic(), constructorHandle, constructorHandle.type());
            return site.getTarget().invokeExact();
        }
        catch (Throwable t) {
            return null;
        }
    }

    static {
        ReflectionUtil.resetFieldAccess();
    }

    private static final class AccessorTestClass {
        private boolean b = true;
        protected byte by = (byte)15;
        public char c = (char)99;
        short s = (short)123;
        int i = 999;
        long l = 12345L;
        float f = 2.2f;
        double d = 4.4;
        Object o = "foo";
        Integer i2 = 555;

        private AccessorTestClass() {
        }

        private boolean validate(FieldAccess access) throws Exception {
            boolean valid = true;
            valid &= this.validField(access, "b", this.b, false);
            valid &= this.validField(access, "by", this.by, (byte)-81);
            valid &= this.validField(access, "c", Character.valueOf(this.c), Character.valueOf('C'));
            valid &= this.validField(access, "s", this.s, (short)321);
            valid &= this.validField(access, "i", this.i, 111);
            valid &= this.validField(access, "l", this.l, 54321L);
            valid &= this.validField(access, "f", Float.valueOf(this.f), Float.valueOf(0.2f));
            valid &= this.validField(access, "d", this.d, 0.4);
            valid &= this.validField(access, "o", this.o, new Object());
            return valid &= this.validField(access, "i2", this.i2, -555);
        }

        private boolean validField(FieldAccess access, String name, Object original, Object toSet) throws Exception {
            FieldAccessor a = this.accessor(access, name);
            boolean valid = original.equals(a.get(this));
            a.set(this, toSet);
            return valid &= !original.equals(a.get(this));
        }

        private FieldAccessor accessor(FieldAccess access, String name) throws Exception {
            return access.getAccessor(this.getClass().getDeclaredField(name));
        }
    }
}

