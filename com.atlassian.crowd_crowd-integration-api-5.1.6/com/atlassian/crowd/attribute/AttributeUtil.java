/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.SetMultimap
 */
package com.atlassian.crowd.attribute;

import com.atlassian.crowd.embedded.api.Attributes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class AttributeUtil {
    public static SetMultimap<String, String> toMultimap(Attributes attributes) {
        return AttributeUtil.toMultimap(attributes.getKeys(), key -> key, arg_0 -> ((Attributes)attributes).getValues(arg_0));
    }

    public static SetMultimap<String, String> toMultimap(Map<String, Set<String>> map) {
        return AttributeUtil.toMultimap(map.entrySet(), Map.Entry::getKey, Map.Entry::getValue);
    }

    private static <T> SetMultimap<String, String> toMultimap(Iterable<T> entities, Function<T, String> key, Function<T, Collection<String>> value) {
        HashMultimap result = HashMultimap.create();
        for (T entity : entities) {
            result.putAll((Object)key.apply(entity), (Iterable)value.apply(entity));
        }
        return result;
    }
}

