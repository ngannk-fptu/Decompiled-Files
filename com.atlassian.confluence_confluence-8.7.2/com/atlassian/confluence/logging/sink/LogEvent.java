/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.logging.sink;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public interface LogEvent {
    public Instant getTimestamp();

    public String getMessage();

    public Optional<Throwable> getThrowable();

    public Level getLevel();

    public String getLoggerName();

    public String getThreadName();

    public Map<String, String> getThreadContext();

    public static enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR;

    }
}

