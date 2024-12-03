/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public interface DateTimeProvider {
    public Optional<TemporalAccessor> getNow();
}

