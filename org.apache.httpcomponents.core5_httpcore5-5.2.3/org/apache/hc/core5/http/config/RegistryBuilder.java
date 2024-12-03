/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

public final class RegistryBuilder<I> {
    private final Map<String, I> items = new HashMap<String, I>();

    public static <I> RegistryBuilder<I> create() {
        return new RegistryBuilder<I>();
    }

    RegistryBuilder() {
    }

    public RegistryBuilder<I> register(String id, I item) {
        Args.notEmpty(id, "ID");
        Args.notNull(item, "Item");
        this.items.put(TextUtils.toLowerCase(id), item);
        return this;
    }

    public Registry<I> build() {
        return new Registry<I>(this.items);
    }

    public String toString() {
        return this.items.toString();
    }
}

