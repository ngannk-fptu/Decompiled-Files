/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.internal.util.StringHelper
 */
package org.hibernate.cache.jcache;

import org.hibernate.internal.util.StringHelper;

public enum MissingCacheStrategy {
    FAIL("fail"),
    CREATE_WARN("create-warn"),
    CREATE("create");

    private final String externalRepresentation;

    private MissingCacheStrategy(String externalRepresentation) {
        this.externalRepresentation = externalRepresentation;
    }

    public String getExternalRepresentation() {
        return this.externalRepresentation;
    }

    public static MissingCacheStrategy interpretSetting(Object value) {
        String externalRepresentation;
        if (value instanceof MissingCacheStrategy) {
            return (MissingCacheStrategy)((Object)value);
        }
        String string = externalRepresentation = value == null ? null : value.toString().trim();
        if (StringHelper.isEmpty((String)externalRepresentation)) {
            return CREATE_WARN;
        }
        for (MissingCacheStrategy strategy : MissingCacheStrategy.values()) {
            if (!strategy.externalRepresentation.equals(externalRepresentation)) continue;
            return strategy;
        }
        throw new IllegalArgumentException("Unrecognized missing cache strategy value : `" + value + '`');
    }
}

