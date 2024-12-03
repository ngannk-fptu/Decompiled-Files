/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ClassToInstanceMap
 *  com.google.common.collect.ImmutableClassToInstanceMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Primitives
 *  javax.annotation.Nullable
 */
package com.querydsl.core.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.util.ArrayUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

public final class ConstructorUtils {
    private static final Class<?>[] NO_ARGS = new Class[0];
    private static final ClassToInstanceMap<Object> defaultPrimitives = ImmutableClassToInstanceMap.builder().put(Boolean.TYPE, (Object)false).put(Byte.TYPE, (Object)0).put(Character.TYPE, (Object)Character.valueOf('\u0000')).put(Short.TYPE, (Object)0).put(Integer.TYPE, (Object)0).put(Long.TYPE, (Object)0L).put(Float.TYPE, (Object)Float.valueOf(0.0f)).put(Double.TYPE, (Object)0.0).build();
    private static final Predicate<ArgumentTransformer> applicableFilter = new Predicate<ArgumentTransformer>(){

        public boolean apply(ArgumentTransformer transformer) {
            return transformer != null ? transformer.isApplicable() : false;
        }
    };

    private ConstructorUtils() {
    }

    public static <C> Constructor<C> getConstructor(Class<C> type, Class<?>[] givenTypes) throws NoSuchMethodException {
        return type.getConstructor(givenTypes);
    }

    public static Class<?>[] getConstructorParameters(Class<?> type, Class<?>[] givenTypes) {
        block0: for (Constructor<?> constructor : type.getConstructors()) {
            int matches = 0;
            Object[] parameters = constructor.getParameterTypes();
            Iterator<Class<?>> parameterIterator = Arrays.asList(parameters).iterator();
            if (!ArrayUtils.isEmpty(givenTypes) && !ArrayUtils.isEmpty(parameters)) {
                Class<?> parameter = null;
                for (Class<?> argument : givenTypes) {
                    if (parameterIterator.hasNext()) {
                        parameter = parameterIterator.next();
                        if (!ConstructorUtils.compatible(parameter, argument)) continue block0;
                        ++matches;
                        continue;
                    }
                    if (!constructor.isVarArgs() || !ConstructorUtils.compatible(parameter, argument)) continue block0;
                }
                if (matches != parameters.length) continue;
                return parameters;
            }
            if (!ArrayUtils.isEmpty(givenTypes) || !ArrayUtils.isEmpty(parameters)) continue;
            return NO_ARGS;
        }
        throw new ExpressionException("No constructor found for " + type.toString() + " with parameters: " + Arrays.deepToString(givenTypes));
    }

    public static Iterable<Function<Object[], Object[]>> getTransformers(Constructor<?> constructor) {
        ArrayList transformers = Lists.newArrayList((Object[])new ArgumentTransformer[]{new PrimitiveAwareVarArgsTransformer(constructor), new PrimitiveTransformer(constructor), new VarArgsTransformer(constructor)});
        return ImmutableList.copyOf((Iterable)Iterables.filter((Iterable)transformers, applicableFilter));
    }

    private static Class<?> normalize(Class<?> clazz) {
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return Primitives.wrap(clazz);
    }

    private static boolean compatible(Class<?> parameter, Class<?> argument) {
        return ConstructorUtils.normalize(parameter).isAssignableFrom(ConstructorUtils.normalize(argument));
    }

    private static class PrimitiveAwareVarArgsTransformer
    extends VarArgsTransformer {
        private final Object defaultInstance;

        public PrimitiveAwareVarArgsTransformer(Constructor<?> constructor) {
            super(constructor);
            this.defaultInstance = this.componentType != null ? defaultPrimitives.getInstance(this.componentType) : null;
        }

        @Override
        public boolean isApplicable() {
            return super.isApplicable() && this.componentType != null && this.componentType.isPrimitive();
        }

        @Override
        public Object[] apply(Object[] args) {
            if (ArrayUtils.isEmpty(args)) {
                return args;
            }
            for (int i = this.paramTypes.length - 1; i < args.length; ++i) {
                if (args[i] != null) continue;
                args[i] = this.defaultInstance;
            }
            return args;
        }
    }

    private static class PrimitiveTransformer
    extends ArgumentTransformer {
        private final Set<Integer> primitiveLocations;

        public PrimitiveTransformer(Constructor<?> constructor) {
            super(constructor);
            ImmutableSet.Builder builder = ImmutableSet.builder();
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int location = 0; location < parameterTypes.length; ++location) {
                Class<?> parameterType = parameterTypes[location];
                if (!parameterType.isPrimitive()) continue;
                builder.add((Object)location);
            }
            this.primitiveLocations = builder.build();
        }

        @Override
        public boolean isApplicable() {
            return !this.primitiveLocations.isEmpty();
        }

        public Object[] apply(Object[] args) {
            if (ArrayUtils.isEmpty(args)) {
                return args;
            }
            for (Integer location : this.primitiveLocations) {
                if (args[location] != null) continue;
                Class primitiveClass = this.paramTypes[location];
                args[location.intValue()] = defaultPrimitives.getInstance(primitiveClass);
            }
            return args;
        }
    }

    private static class VarArgsTransformer
    extends ArgumentTransformer {
        protected final Class<?> componentType;

        public VarArgsTransformer(Constructor<?> constructor) {
            super(constructor);
            this.componentType = this.paramTypes.length > 0 ? this.paramTypes[this.paramTypes.length - 1].getComponentType() : null;
        }

        @Override
        public boolean isApplicable() {
            return this.constructor != null && this.constructor.isVarArgs();
        }

        public Object[] apply(Object[] args) {
            Object vargs;
            if (ArrayUtils.isEmpty(args)) {
                return args;
            }
            int current = 0;
            Object[] cargs = new Object[this.paramTypes.length];
            for (int i = 0; i < cargs.length - 1; ++i) {
                this.set(cargs, i, args[current++]);
            }
            int size = args.length - cargs.length + 1;
            cargs[cargs.length - 1] = vargs = Array.newInstance(this.componentType, size);
            for (int i = 0; i < Array.getLength(vargs); ++i) {
                this.set(vargs, i, args[current++]);
            }
            return cargs;
        }

        private void set(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
            Array.set(array, index, value);
        }
    }

    protected static abstract class ArgumentTransformer
    implements Function<Object[], Object[]> {
        @Nullable
        protected Constructor<?> constructor;
        protected final Class<?>[] paramTypes;

        public ArgumentTransformer(Constructor<?> constructor) {
            this(constructor.getParameterTypes());
            this.constructor = constructor;
        }

        public ArgumentTransformer(Class<?>[] paramTypes) {
            this.paramTypes = paramTypes;
        }

        public abstract boolean isApplicable();
    }
}

