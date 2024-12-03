/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.GenerationType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.GenerationType;

public class GenerationTypeMarshalling {
    public static GenerationType fromXml(String name) {
        return GenerationType.valueOf((String)name);
    }

    public static String toXml(GenerationType accessType) {
        return accessType.name();
    }
}

