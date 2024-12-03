/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.util;

import java.util.Map;

@FunctionalInterface
public interface PropertyTransformer {
    public Map<String, Object> transformProperties(Map<String, ? extends Object> var1);

    default public PropertyTransformer andThen(PropertyTransformer after) {
        return input -> after.transformProperties(this.transformProperties(input));
    }
}

