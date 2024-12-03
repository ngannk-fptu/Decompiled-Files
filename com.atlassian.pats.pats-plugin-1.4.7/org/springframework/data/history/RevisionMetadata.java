/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.history;

import java.time.Instant;
import java.util.Optional;

public interface RevisionMetadata<N extends Number> {
    public Optional<N> getRevisionNumber();

    default public N getRequiredRevisionNumber() {
        return (N)((Number)this.getRevisionNumber().orElseThrow(() -> new IllegalStateException(String.format("No revision number found on %s!", this.getDelegate()))));
    }

    public Optional<Instant> getRevisionInstant();

    default public Instant getRequiredRevisionInstant() {
        return this.getRevisionInstant().orElseThrow(() -> new IllegalStateException(String.format("No revision date found on %s!", this.getDelegate())));
    }

    public <T> T getDelegate();

    default public RevisionType getRevisionType() {
        return RevisionType.UNKNOWN;
    }

    public static enum RevisionType {
        UNKNOWN,
        INSERT,
        UPDATE,
        DELETE;

    }
}

