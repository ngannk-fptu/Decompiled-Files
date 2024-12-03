/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

public abstract class OrderUtils {
    private static final Object NOT_ANNOTATED = new Object();
    @Nullable
    private static Class<? extends Annotation> priorityAnnotationType;
    private static final Map<Class<?>, Object> orderCache;
    private static final Map<Class<?>, Object> priorityCache;

    public static int getOrder(Class<?> type, int defaultOrder) {
        Integer order = OrderUtils.getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type, @Nullable Integer defaultOrder) {
        Integer order = OrderUtils.getOrder(type);
        return order != null ? order : defaultOrder;
    }

    @Nullable
    public static Integer getOrder(Class<?> type) {
        Object cached = orderCache.get(type);
        if (cached != null) {
            return cached instanceof Integer ? (Integer)cached : null;
        }
        Order order = AnnotationUtils.findAnnotation(type, Order.class);
        Integer result = order != null ? Integer.valueOf(order.value()) : OrderUtils.getPriority(type);
        orderCache.put(type, result != null ? result : NOT_ANNOTATED);
        return result;
    }

    @Nullable
    public static Integer getPriority(Class<?> type) {
        if (priorityAnnotationType == null) {
            return null;
        }
        Object cached = priorityCache.get(type);
        if (cached != null) {
            return cached instanceof Integer ? (Integer)cached : null;
        }
        Annotation priority = AnnotationUtils.findAnnotation(type, priorityAnnotationType);
        Integer result = null;
        if (priority != null) {
            result = (Integer)AnnotationUtils.getValue(priority);
        }
        priorityCache.put(type, result != null ? result : NOT_ANNOTATED);
        return result;
    }

    static {
        try {
            priorityAnnotationType = ClassUtils.forName("javax.annotation.Priority", OrderUtils.class.getClassLoader());
        }
        catch (Throwable ex) {
            priorityAnnotationType = null;
        }
        orderCache = new ConcurrentReferenceHashMap(64);
        priorityCache = new ConcurrentReferenceHashMap();
    }
}

