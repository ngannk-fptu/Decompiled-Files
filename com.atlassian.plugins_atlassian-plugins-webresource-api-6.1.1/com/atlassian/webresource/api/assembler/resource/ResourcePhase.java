/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.webresource.api.assembler.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public enum ResourcePhase {
    INLINE("inline"),
    REQUIRE("require"),
    DEFER("defer"),
    INTERACTION("interaction");

    private final String label;
    private static final Map<String, ResourcePhase> BY_LABEL;

    private ResourcePhase(String label) {
        this.label = label;
    }

    public static ResourcePhase getPhaseOrDefault(@Nullable String phase) {
        String key = Optional.ofNullable(phase).map(p -> p.toLowerCase(Locale.ROOT)).orElse(null);
        return BY_LABEL.getOrDefault(key, ResourcePhase.defaultPhase());
    }

    public static ResourcePhase defaultPhase() {
        return REQUIRE;
    }

    static {
        BY_LABEL = new HashMap<String, ResourcePhase>();
        for (ResourcePhase resourcePhase : ResourcePhase.values()) {
            BY_LABEL.put(resourcePhase.label, resourcePhase);
        }
    }
}

