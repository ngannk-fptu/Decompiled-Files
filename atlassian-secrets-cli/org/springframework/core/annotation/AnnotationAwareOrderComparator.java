/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;

public class AnnotationAwareOrderComparator
extends OrderComparator {
    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

    @Override
    @Nullable
    protected Integer findOrder(Object obj) {
        Integer order = super.findOrder(obj);
        if (order != null) {
            return order;
        }
        if (obj instanceof Class) {
            return OrderUtils.getOrder((Class)obj);
        }
        if (obj instanceof Method) {
            Order ann = AnnotationUtils.findAnnotation((Method)obj, Order.class);
            if (ann != null) {
                return ann.value();
            }
        } else if (obj instanceof AnnotatedElement) {
            Order ann = AnnotationUtils.getAnnotation((AnnotatedElement)obj, Order.class);
            if (ann != null) {
                return ann.value();
            }
        } else {
            order = OrderUtils.getOrder(obj.getClass());
            if (order == null && obj instanceof DecoratingProxy) {
                order = OrderUtils.getOrder(((DecoratingProxy)obj).getDecoratedClass());
            }
        }
        return order;
    }

    @Override
    @Nullable
    public Integer getPriority(Object obj) {
        if (obj instanceof Class) {
            return OrderUtils.getPriority((Class)obj);
        }
        Integer priority = OrderUtils.getPriority(obj.getClass());
        if (priority == null && obj instanceof DecoratingProxy) {
            priority = OrderUtils.getPriority(((DecoratingProxy)obj).getDecoratedClass());
        }
        return priority;
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            AnnotationAwareOrderComparator.sort((Object[])value);
        } else if (value instanceof List) {
            AnnotationAwareOrderComparator.sort((List)value);
        }
    }
}

