/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.crowd.common.util;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ProxyUtil {
    private ProxyUtil() {
    }

    public static <T> T runWithContextClassLoader(ClassLoader classLoader, T service) {
        return ProxyUtil.newProxy(service, (proxy, method, args) -> {
            Thread currentThread = Thread.currentThread();
            ClassLoader original = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(classLoader);
                Object object = method.invoke(service, args);
                return object;
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            finally {
                currentThread.setContextClassLoader(original);
            }
        });
    }

    public static <T> T runInTransaction(T service, Function<Supplier<?>, ?> transactionalRunner) {
        return ProxyUtil.newProxy(service, (proxy, method, args) -> {
            try {
                return transactionalRunner.apply(() -> {
                    try {
                        return method.invoke(service, args);
                    }
                    catch (InvocationTargetException e) {
                        throw new InternalException(e.getCause());
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            catch (InternalException e) {
                throw e.getCause();
            }
        });
    }

    public static <T> T cached(Map<Object, Pair<Object, Throwable>> cache, T original) {
        return ProxyUtil.newProxy(original, (proxy, method, args) -> {
            ImmutableList key = ImmutableList.of((Object)method, ProxyUtil.asList(args));
            Pair result = cache.computeIfAbsent(key, k -> ProxyUtil.invoke(original, method, args));
            if (result.getRight() != null) {
                throw (Throwable)result.getRight();
            }
            return result.getLeft();
        });
    }

    private static List<Object> asList(Object[] array) {
        if (array == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(array);
    }

    private static Pair<Object, Throwable> invoke(Object original, Method method, Object[] args) {
        try {
            return Pair.of((Object)method.invoke(original, args), null);
        }
        catch (InvocationTargetException e) {
            return Pair.of(null, (Object)e.getCause());
        }
        catch (IllegalAccessException e) {
            return Pair.of(null, (Object)new RuntimeException(e));
        }
        catch (Throwable t) {
            return Pair.of(null, (Object)t);
        }
    }

    public static <T> T newProxy(T service, InvocationHandler handler) {
        return (T)Proxy.newProxyInstance(service.getClass().getClassLoader(), ClassUtils.getAllInterfaces(service.getClass()).toArray(new Class[0]), handler);
    }

    private static class InternalException
    extends RuntimeException {
        public InternalException(Throwable cause) {
            super(cause);
        }
    }
}

