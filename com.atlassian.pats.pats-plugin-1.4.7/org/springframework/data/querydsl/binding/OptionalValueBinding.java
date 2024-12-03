/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import java.util.Optional;

@FunctionalInterface
public interface OptionalValueBinding<T extends Path<? extends S>, S> {
    public Optional<Predicate> bind(T var1, Optional<? extends S> var2);
}

