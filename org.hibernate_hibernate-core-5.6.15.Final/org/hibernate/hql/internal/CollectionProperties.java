/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CollectionProperties {
    public static final Map HQL_COLLECTION_PROPERTIES;
    private static final String COLLECTION_INDEX_LOWER;

    private CollectionProperties() {
    }

    public static boolean isCollectionProperty(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        if (COLLECTION_INDEX_LOWER.equals(key)) {
            return false;
        }
        return HQL_COLLECTION_PROPERTIES.containsKey(key);
    }

    public static String getNormalizedPropertyName(String name) {
        return (String)HQL_COLLECTION_PROPERTIES.get(name);
    }

    public static boolean isAnyCollectionProperty(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        return HQL_COLLECTION_PROPERTIES.containsKey(key);
    }

    static {
        COLLECTION_INDEX_LOWER = "index".toLowerCase(Locale.ROOT);
        HQL_COLLECTION_PROPERTIES = new HashMap();
        HQL_COLLECTION_PROPERTIES.put("elements".toLowerCase(Locale.ROOT), "elements");
        HQL_COLLECTION_PROPERTIES.put("indices".toLowerCase(Locale.ROOT), "indices");
        HQL_COLLECTION_PROPERTIES.put("size".toLowerCase(Locale.ROOT), "size");
        HQL_COLLECTION_PROPERTIES.put("maxIndex".toLowerCase(Locale.ROOT), "maxIndex");
        HQL_COLLECTION_PROPERTIES.put("minIndex".toLowerCase(Locale.ROOT), "minIndex");
        HQL_COLLECTION_PROPERTIES.put("maxElement".toLowerCase(Locale.ROOT), "maxElement");
        HQL_COLLECTION_PROPERTIES.put("minElement".toLowerCase(Locale.ROOT), "minElement");
        HQL_COLLECTION_PROPERTIES.put(COLLECTION_INDEX_LOWER, "index");
    }
}

