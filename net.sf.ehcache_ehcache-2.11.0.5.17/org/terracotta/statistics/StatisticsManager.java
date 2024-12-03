/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.terracotta.context.ContextCreationListener;
import org.terracotta.context.ContextElement;
import org.terracotta.context.ContextManager;
import org.terracotta.context.TreeNode;
import org.terracotta.statistics.GeneralOperationStatistic;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.PassThroughStatistic;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.observer.OperationObserver;

public class StatisticsManager
extends ContextManager {
    public static <T extends Enum<T>> OperationObserver<T> createOperationStatistic(Object context, String name, Set<String> tags, Class<T> eventTypes) {
        return StatisticsManager.createOperationStatistic(context, name, tags, Collections.emptyMap(), eventTypes);
    }

    public static <T extends Enum<T>> OperationObserver<T> createOperationStatistic(Object context, String name, Set<String> tags, Map<String, ? extends Object> properties, Class<T> resultType) {
        OperationStatistic<T> stat = StatisticsManager.createOperationStatistic(name, tags, properties, resultType);
        StatisticsManager.associate(context).withChild(stat);
        return stat;
    }

    private static <T extends Enum<T>> OperationStatistic<T> createOperationStatistic(String name, Set<String> tags, Map<String, ? extends Object> properties, Class<T> resultType) {
        return new GeneralOperationStatistic<T>(name, tags, properties, resultType);
    }

    public static <T extends Enum<T>> OperationStatistic<T> getOperationStatisticFor(OperationObserver<T> observer) {
        TreeNode node = ContextManager.nodeFor(observer);
        if (node == null) {
            return null;
        }
        ContextElement context = node.getContext();
        if (OperationStatistic.class.isAssignableFrom(context.identifier())) {
            return (OperationStatistic)context.attributes().get("this");
        }
        throw new AssertionError();
    }

    public static <T extends Number> void createPassThroughStatistic(Object context, String name, Set<String> tags, Callable<T> source) {
        StatisticsManager.createPassThroughStatistic(context, name, tags, Collections.emptyMap(), source);
    }

    public static <T extends Number> void createPassThroughStatistic(Object context, String name, Set<String> tags, Map<String, ? extends Object> properties, Callable<T> source) {
        PassThroughStatistic<T> stat = new PassThroughStatistic<T>(context, name, tags, properties, source);
        StatisticsManager.associate(context).withChild(stat);
    }

    private static void parseStatisticAnnotations(Object object) {
        for (Method m : object.getClass().getMethods()) {
            Statistic anno = m.getAnnotation(Statistic.class);
            if (anno == null) continue;
            Class<?> returnType = m.getReturnType();
            if (m.getParameterTypes().length != 0) {
                throw new IllegalArgumentException("Statistic methods must be no-arg: " + m);
            }
            if (!(Number.class.isAssignableFrom(returnType) || m.getReturnType().isPrimitive() && !m.getReturnType().equals(Boolean.TYPE))) {
                throw new IllegalArgumentException("Statistic methods must return a Number: " + m);
            }
            if (Modifier.isStatic(m.getModifiers())) {
                throw new IllegalArgumentException("Statistic methods must be non-static: " + m);
            }
            StatisticsManager.createPassThroughStatistic(object, anno.name(), new HashSet<String>(Arrays.asList(anno.tags())), new MethodCallable(object, m));
        }
    }

    static {
        ContextManager.registerContextCreationListener(new ContextCreationListener(){

            @Override
            public void contextCreated(Object object) {
                StatisticsManager.parseStatisticAnnotations(object);
            }
        });
    }

    static class MethodCallable<T>
    implements Callable<T> {
        private final WeakReference<Object> targetRef;
        private final Method method;

        MethodCallable(Object target, Method method) {
            this.targetRef = new WeakReference<Object>(target);
            this.method = method;
        }

        @Override
        public T call() throws Exception {
            return (T)this.method.invoke(this.targetRef.get(), new Object[0]);
        }
    }
}

