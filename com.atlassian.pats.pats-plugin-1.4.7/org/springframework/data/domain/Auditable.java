/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.domain;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.domain.Persistable;

public interface Auditable<U, ID, T extends TemporalAccessor>
extends Persistable<ID> {
    public Optional<U> getCreatedBy();

    public void setCreatedBy(U var1);

    public Optional<T> getCreatedDate();

    public void setCreatedDate(T var1);

    public Optional<U> getLastModifiedBy();

    public void setLastModifiedBy(U var1);

    public Optional<T> getLastModifiedDate();

    public void setLastModifiedDate(T var1);
}

