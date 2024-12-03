/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

public class EventUtil {
    public static String getLoggableName(String entityName, Object entity) {
        return entityName == null ? entity.getClass().getName() : entityName;
    }
}

