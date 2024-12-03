/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ConstraintMode
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.ConstraintMode;

public class ConstraintModeMarshalling {
    public static ConstraintMode fromXml(String name) {
        return ConstraintMode.valueOf((String)name);
    }

    public static String toXml(ConstraintMode accessType) {
        return accessType.name();
    }
}

