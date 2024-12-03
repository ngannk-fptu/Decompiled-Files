/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.Priority;
import java.util.Comparator;

public class PriorityUtil {
    public static final int DEFAULT_PRIORITY = 100;
    public static final InstanceComparator INSTANCE_COMPARATOR = new InstanceComparator();

    public static class TypeComparator
    implements Comparator<Class<?>> {
        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            return this.priorityOf(o2) - this.priorityOf(o1);
        }

        private int priorityOf(Class<?> o) {
            Priority priorityAnnotation = o.getAnnotation(Priority.class);
            return priorityAnnotation == null ? 100 : priorityAnnotation.value();
        }
    }

    public static final class InstanceComparator
    implements Comparator {
        public int compare(Object o1, Object o2) {
            return this.priorityOf(o2) - this.priorityOf(o1);
        }

        private int priorityOf(Object o) {
            Priority priorityAnnotation = o.getClass().getAnnotation(Priority.class);
            return priorityAnnotation == null ? 100 : priorityAnnotation.value();
        }
    }
}

