/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.auditing.DateTimeProvider;

public enum CurrentDateTimeProvider implements DateTimeProvider
{
    INSTANCE;


    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(LocalDateTime.now());
    }
}

