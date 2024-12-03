/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;

public interface Model {
    public Model addAttribute(String var1, @Nullable Object var2);

    public Model addAttribute(Object var1);

    public Model addAllAttributes(Collection<?> var1);

    public Model addAllAttributes(Map<String, ?> var1);

    public Model mergeAttributes(Map<String, ?> var1);

    public boolean containsAttribute(String var1);

    @Nullable
    public Object getAttribute(String var1);

    public Map<String, Object> asMap();
}

