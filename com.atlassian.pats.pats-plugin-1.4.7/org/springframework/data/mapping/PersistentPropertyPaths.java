/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping;

import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.util.Streamable;

public interface PersistentPropertyPaths<T, P extends PersistentProperty<P>>
extends Streamable<PersistentPropertyPath<P>> {
    public Optional<PersistentPropertyPath<P>> getFirst();

    public boolean contains(String var1);

    public boolean contains(PropertyPath var1);

    public PersistentPropertyPaths<T, P> dropPathIfSegmentMatches(Predicate<? super P> var1);
}

