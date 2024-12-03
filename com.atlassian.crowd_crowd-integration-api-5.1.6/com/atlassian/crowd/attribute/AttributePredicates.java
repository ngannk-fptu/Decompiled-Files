/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.attribute;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class AttributePredicates {
    public static final Predicate<String> SYNCING_ATTRIBUTE = key -> key.startsWith("synch.");
    public static final Predicate<Map.Entry<String, Set<String>>> SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE = entry -> SYNCING_ATTRIBUTE.test((String)entry.getKey());

    private AttributePredicates() {
    }
}

