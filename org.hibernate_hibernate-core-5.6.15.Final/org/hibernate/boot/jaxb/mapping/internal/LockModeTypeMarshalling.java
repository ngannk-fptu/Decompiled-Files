/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.LockModeType;

public class LockModeTypeMarshalling {
    public static LockModeType fromXml(String name) {
        return LockModeType.valueOf((String)name);
    }

    public static String toXml(LockModeType lockModeType) {
        return lockModeType.name();
    }
}

