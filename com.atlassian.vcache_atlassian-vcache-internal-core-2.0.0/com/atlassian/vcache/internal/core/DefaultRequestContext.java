/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestContext
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.RequestContext;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DefaultRequestContext
implements RequestContext {
    @Nullable
    private String partitionId;
    private final Supplier<String> partitionIdSupplier;
    private final Map<Object, Object> map = new ConcurrentHashMap<Object, Object>();

    public DefaultRequestContext(Supplier<String> partitionIdSupplier) {
        this.partitionIdSupplier = Objects.requireNonNull(partitionIdSupplier);
    }

    public String partitionIdentifier() {
        if (this.partitionId == null) {
            this.partitionId = this.partitionIdSupplier.get();
        }
        return this.partitionId;
    }

    public <T> T computeIfAbsent(Object key, Supplier<T> supplier) {
        return (T)this.map.computeIfAbsent(Objects.requireNonNull(key), (? super K o) -> Objects.requireNonNull(supplier.get()));
    }

    public <T> Optional<T> get(Object key) {
        return Optional.ofNullable(this.map.get(key));
    }
}

