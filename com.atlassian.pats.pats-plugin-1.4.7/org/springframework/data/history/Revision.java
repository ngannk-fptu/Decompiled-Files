/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.history;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.util.Optionals;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public final class Revision<N extends Number, T>
implements Comparable<Revision<N, ?>> {
    private final RevisionMetadata<N> metadata;
    private final T entity;

    private Revision(RevisionMetadata<N> metadata, T entity) {
        this.metadata = metadata;
        this.entity = entity;
    }

    public static <N extends Number, T> Revision<N, T> of(RevisionMetadata<N> metadata, T entity) {
        return new Revision<N, T>(metadata, entity);
    }

    public Optional<N> getRevisionNumber() {
        return this.metadata.getRevisionNumber();
    }

    public N getRequiredRevisionNumber() {
        return this.metadata.getRequiredRevisionNumber();
    }

    public Optional<Instant> getRevisionInstant() {
        return this.metadata.getRevisionInstant();
    }

    public Instant getRequiredRevisionInstant() {
        return this.metadata.getRequiredRevisionInstant();
    }

    @Override
    public int compareTo(@Nullable Revision<N, ?> that) {
        if (that == null) {
            return 1;
        }
        return Optionals.mapIfAllPresent(this.getRevisionNumber(), that.getRevisionNumber(), (rec$, x$0) -> ((Comparable)rec$).compareTo(x$0)).orElse(-1);
    }

    public String toString() {
        return String.format("Revision %s of entity %s - Revision metadata %s", this.getRevisionNumber().map(Object::toString).orElse("<unknown>"), this.entity, this.metadata);
    }

    public RevisionMetadata<N> getMetadata() {
        return this.metadata;
    }

    public T getEntity() {
        return this.entity;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Revision)) {
            return false;
        }
        Revision revision = (Revision)o;
        if (!ObjectUtils.nullSafeEquals(this.metadata, revision.metadata)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.entity, revision.entity);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.metadata);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.entity);
        return result;
    }
}

