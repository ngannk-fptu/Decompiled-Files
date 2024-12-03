/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import org.hibernate.HibernateException;

public enum MetadataSourceType {
    HBM("hbm"),
    CLASS("class");

    private final String name;

    private MetadataSourceType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static MetadataSourceType parsePrecedence(String value) {
        if (MetadataSourceType.HBM.name.equalsIgnoreCase(value)) {
            return HBM;
        }
        if (MetadataSourceType.CLASS.name.equalsIgnoreCase(value)) {
            return CLASS;
        }
        throw new HibernateException("Unknown metadata source type value [" + value + "]");
    }
}

