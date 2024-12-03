/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public interface AuditableBeanWrapper<T> {
    public Object setCreatedBy(Object var1);

    public TemporalAccessor setCreatedDate(TemporalAccessor var1);

    public Object setLastModifiedBy(Object var1);

    public Optional<TemporalAccessor> getLastModifiedDate();

    public TemporalAccessor setLastModifiedDate(TemporalAccessor var1);

    public T getBean();
}

