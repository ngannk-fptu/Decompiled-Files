/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.instruments;

import com.atlassian.instrumentation.driver.Instrumentation;
import com.atlassian.instrumentation.instruments.Context;
import com.atlassian.instrumentation.instruments.EventType;
import java.util.Optional;

public class Timer
implements Context {
    private final Class<?> clazz;
    private final String databaseName;
    private final Optional<String> sql;
    private final Optional<String> sqlId;
    private final Optional<EventType> eventType;
    private Instrumentation.Split split;

    public Timer(Class<?> clazz, String databaseName, Optional<String> sql, Optional<String> sqlId, Optional<EventType> eventType) {
        this.clazz = clazz;
        this.databaseName = databaseName;
        this.sql = sql;
        this.sqlId = sqlId;
        this.eventType = eventType;
    }

    public void start() {
        if (this.split != null) {
            throw new IllegalStateException("timer already started");
        }
        this.split = Instrumentation.startSplit(this);
    }

    public void stop() {
        this.split.stop();
    }

    @Override
    public Class<?> getClazz() {
        return this.clazz;
    }

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public Optional<String> getSql() {
        return this.sql;
    }

    @Override
    public Optional<String> getSqlId() {
        return this.sqlId;
    }

    @Override
    public Optional<EventType> getEventType() {
        return this.eventType;
    }
}

