/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import java.util.Set;

public enum AutoescapeMode {
    FALSE,
    TRUE,
    CONTEXTUAL,
    STRICT;

    private final String attributeValue = this.name().toLowerCase(Locale.ENGLISH);

    public String getAttributeValue() {
        return this.attributeValue;
    }

    public static Set<String> getAttributeValues() {
        ImmutableSet.Builder values = ImmutableSet.builder();
        for (AutoescapeMode value : AutoescapeMode.values()) {
            values.add((Object)value.getAttributeValue());
        }
        return values.build();
    }

    public static AutoescapeMode forAttributeValue(String attributeValue) {
        return AutoescapeMode.valueOf(attributeValue.toUpperCase(Locale.ENGLISH));
    }
}

