/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.util;

import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Manager;

public class ToStringUtil {
    private ToStringUtil() {
    }

    public static final String toString(Contained contained) {
        return ToStringUtil.toString((Object)contained, contained.getContainer());
    }

    public static final String toString(Object obj, Container container) {
        return ToStringUtil.containedToString(obj, container, "Container");
    }

    public static final String toString(Object obj, Manager manager) {
        return ToStringUtil.containedToString(obj, manager, "Manager");
    }

    private static String containedToString(Object contained, Object container, String containerTypeName) {
        StringBuilder sb = new StringBuilder(contained.getClass().getSimpleName());
        sb.append('[');
        if (container == null) {
            sb.append(containerTypeName);
            sb.append(" is null");
        } else {
            sb.append(container.toString());
        }
        sb.append(']');
        return sb.toString();
    }
}

