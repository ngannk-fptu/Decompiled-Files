/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.TypedNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public class DependencyInjectionFactory {
    static /* synthetic */ Class class$com$thoughtworks$xstream$core$util$TypedNull;

    public static Object newInstance(Class type, Object[] dependencies) {
        return DependencyInjectionFactory.newInstance(type, dependencies, null);
    }

    public static Object newInstance(Class type, Object[] dependencies, BitSet usedDependencies) {
        if (dependencies != null && dependencies.length > 63) {
            throw new IllegalArgumentException("More than 63 arguments are not supported");
        }
        Constructor<?> bestMatchingCtor = null;
        ArrayList<Object> matchingDependencies = new ArrayList<Object>();
        List possibleMatchingDependencies = null;
        long usedDeps = 0L;
        long possibleUsedDeps = 0L;
        if (dependencies != null && dependencies.length > 0) {
            Constructor<?>[] ctors = type.getConstructors();
            if (ctors.length > 1) {
                Arrays.sort(ctors, new Comparator(){

                    public int compare(Object o1, Object o2) {
                        return ((Constructor)o2).getParameterTypes().length - ((Constructor)o1).getParameterTypes().length;
                    }
                });
            }
            TypedValue[] typedDependencies = new TypedValue[dependencies.length];
            for (int i = 0; i < dependencies.length; ++i) {
                Object dependency = dependencies[i];
                Class depType = dependency.getClass();
                if (depType.isPrimitive()) {
                    depType = Primitives.box(depType);
                } else if (depType == (class$com$thoughtworks$xstream$core$util$TypedNull == null ? DependencyInjectionFactory.class$("com.thoughtworks.xstream.core.util.TypedNull") : class$com$thoughtworks$xstream$core$util$TypedNull)) {
                    depType = ((TypedNull)dependency).getType();
                    dependency = null;
                }
                typedDependencies[i] = new TypedValue(depType, dependency);
            }
            Constructor<?> possibleCtor = null;
            int arity = Integer.MAX_VALUE;
            for (int i = 0; bestMatchingCtor == null && i < ctors.length; ++i) {
                int j;
                Constructor<?> constructor = ctors[i];
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length > dependencies.length) continue;
                if (parameterTypes.length == 0) {
                    if (possibleCtor != null) break;
                    bestMatchingCtor = constructor;
                    break;
                }
                if (arity > parameterTypes.length) {
                    if (possibleCtor != null) continue;
                    arity = parameterTypes.length;
                }
                for (j = 0; j < parameterTypes.length; ++j) {
                    if (!parameterTypes[j].isPrimitive()) continue;
                    parameterTypes[j] = Primitives.box(parameterTypes[j]);
                }
                matchingDependencies.clear();
                usedDeps = 0L;
                j = 0;
                int k = 0;
                while (j < parameterTypes.length && parameterTypes.length + k - j <= typedDependencies.length) {
                    if (parameterTypes[j].isAssignableFrom(typedDependencies[k].type)) {
                        matchingDependencies.add(typedDependencies[k].value);
                        usedDeps |= 1L << k;
                        if (++j == parameterTypes.length) {
                            bestMatchingCtor = constructor;
                            break;
                        }
                    }
                    ++k;
                }
                if (bestMatchingCtor != null) continue;
                boolean possible = true;
                TypedValue[] deps = new TypedValue[typedDependencies.length];
                System.arraycopy(typedDependencies, 0, deps, 0, deps.length);
                matchingDependencies.clear();
                usedDeps = 0L;
                for (int j2 = 0; j2 < parameterTypes.length; ++j2) {
                    int assignable = -1;
                    for (int k2 = 0; k2 < deps.length; ++k2) {
                        if (deps[k2] == null) continue;
                        if (deps[k2].type == parameterTypes[j2]) {
                            assignable = k2;
                            break;
                        }
                        if (!parameterTypes[j2].isAssignableFrom(deps[k2].type) || assignable >= 0 && (deps[assignable].type == deps[k2].type || !deps[assignable].type.isAssignableFrom(deps[k2].type))) continue;
                        assignable = k2;
                    }
                    if (assignable >= 0) {
                        matchingDependencies.add(deps[assignable].value);
                        usedDeps |= 1L << assignable;
                    } else {
                        possible = false;
                        break;
                    }
                    deps[assignable] = null;
                }
                if (!possible || possibleCtor != null && usedDeps >= possibleUsedDeps) continue;
                possibleCtor = constructor;
                possibleMatchingDependencies = (List)matchingDependencies.clone();
                possibleUsedDeps = usedDeps;
            }
            if (bestMatchingCtor == null) {
                if (possibleCtor == null) {
                    usedDeps = 0L;
                    ObjectAccessException ex = new ObjectAccessException("Cannot construct type, none of the arguments match any constructor's parameters");
                    ex.add("construction-type", type.getName());
                    throw ex;
                }
                bestMatchingCtor = possibleCtor;
                matchingDependencies.clear();
                matchingDependencies.addAll(possibleMatchingDependencies);
                usedDeps = possibleUsedDeps;
            }
        }
        Throwable th = null;
        try {
            Object instance = bestMatchingCtor == null ? type.newInstance() : bestMatchingCtor.newInstance(matchingDependencies.toArray());
            if (usedDependencies != null) {
                usedDependencies.clear();
                int i = 0;
                long l = 1L;
                while (l < usedDeps) {
                    if ((usedDeps & l) > 0L) {
                        usedDependencies.set(i);
                    }
                    l <<= 1;
                    ++i;
                }
            }
            return instance;
        }
        catch (InstantiationException e) {
            th = e;
        }
        catch (IllegalAccessException e) {
            th = e;
        }
        catch (InvocationTargetException e) {
            th = e.getCause();
        }
        catch (SecurityException e) {
            th = e;
        }
        catch (ExceptionInInitializerError e) {
            th = e;
        }
        ObjectAccessException ex = new ObjectAccessException("Cannot construct type", th);
        ex.add("construction-type", type.getName());
        throw ex;
    }

    private static class TypedValue {
        final Class type;
        final Object value;

        public TypedValue(Class type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return this.type.getName() + ":" + this.value;
        }
    }
}

