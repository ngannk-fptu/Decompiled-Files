/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import org.hibernate.EntityMode;

public class EntityModeConverter {
    public static EntityMode fromXml(String name) {
        return EntityMode.parse(name);
    }

    public static String toXml(EntityMode entityMode) {
        return null == entityMode ? null : entityMode.getExternalName();
    }
}

