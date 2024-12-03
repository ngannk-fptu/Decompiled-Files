/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ThrowingMapMergeOperatorUtil {
    private ThrowingMapMergeOperatorUtil() {
    }

    private static String mergeDuplicatedNames(String entityType, String name, String id1, String id2) {
        throw new IllegalStateException(String.format("Found %ss with duplicate name '%s', ids: '%s', '%s'", entityType, name, id1, id2));
    }

    public static <T> Map<String, String> mapUniqueNamesToIds(Collection<T> entities, Function<T, String> nameMapper, Function<T, String> idMapper, String entityName) {
        HashMap<String, String> uniqueNames = new HashMap<String, String>();
        for (T entity : entities) {
            String id = idMapper.apply(entity);
            String name = nameMapper.apply(entity);
            uniqueNames.merge(name, id, (id1, id2) -> ThrowingMapMergeOperatorUtil.mergeDuplicatedNames(entityName, name, id1, id2));
        }
        return uniqueNames;
    }
}

