/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FetchType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.FetchType;

public class FetchTypeMarshalling {
    public static FetchType fromXml(String name) {
        return FetchType.valueOf((String)name);
    }

    public static String toXml(FetchType fetchType) {
        return fetchType.name();
    }
}

