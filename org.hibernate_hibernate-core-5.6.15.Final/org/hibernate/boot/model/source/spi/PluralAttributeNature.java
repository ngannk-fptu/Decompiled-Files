/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum PluralAttributeNature {
    BAG(Collection.class, false),
    ID_BAG(Collection.class, false),
    SET(Set.class, false),
    LIST(List.class, true),
    ARRAY(Object[].class, true),
    MAP(Map.class, true);

    private final boolean indexed;
    private final Class<?> reportedJavaType;

    private PluralAttributeNature(Class<?> reportedJavaType, boolean indexed) {
        this.reportedJavaType = reportedJavaType;
        this.indexed = indexed;
    }

    public Class<?> reportedJavaType() {
        return this.reportedJavaType;
    }

    public boolean isIndexed() {
        return this.indexed;
    }
}

