/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.instruments;

import com.atlassian.instrumentation.instruments.EventType;
import java.util.Optional;

public interface Context {
    public Class<?> getClazz();

    public String getDatabaseName();

    public Optional<String> getSql();

    public Optional<String> getSqlId();

    public Optional<EventType> getEventType();
}

