/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.crowd.model.group.Group;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class NameUtils {
    private static final Function<String, String> KEY_FUNCTION = entityName -> entityName.toLowerCase(Locale.ENGLISH);

    public static String getCanonicalName(Group group) {
        return NameUtils.getCanonicalName(group.getName());
    }

    public static String getCanonicalName(String name) {
        return (String)KEY_FUNCTION.apply((Object)name);
    }

    public static Map<String, String> canonicalMappingForNames(Iterable<String> names) {
        LinkedHashMap result = Maps.newLinkedHashMap();
        for (String name : names) {
            result.put(NameUtils.getCanonicalName(name), name);
        }
        return ImmutableMap.copyOf((Map)result);
    }

    public static <G extends Group> Map<String, G> canonicalMappingForGroups(Iterable<G> groups) {
        LinkedHashMap result = Maps.newLinkedHashMap();
        for (Group group : groups) {
            result.put(NameUtils.getCanonicalName(group), group);
        }
        return ImmutableMap.copyOf((Map)result);
    }
}

