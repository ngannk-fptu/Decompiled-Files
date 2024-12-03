/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.DiscriminatorType;

public class DiscriminatorTypeMarshalling {
    public static DiscriminatorType fromXml(String name) {
        return DiscriminatorType.valueOf((String)name);
    }

    public static String toXml(DiscriminatorType discriminatorType) {
        return discriminatorType.name();
    }
}

