/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.ThreadContext$ContextStack
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.util.ReadOnlyStringMap
 */
package org.apache.logging.log4j.core;

import java.io.Serializable;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

public interface LogEvent
extends Serializable {
    public LogEvent toImmutable();

    @Deprecated
    public Map<String, String> getContextMap();

    public ReadOnlyStringMap getContextData();

    public ThreadContext.ContextStack getContextStack();

    public String getLoggerFqcn();

    public Level getLevel();

    public String getLoggerName();

    public Marker getMarker();

    public Message getMessage();

    public long getTimeMillis();

    public Instant getInstant();

    public StackTraceElement getSource();

    public String getThreadName();

    public long getThreadId();

    public int getThreadPriority();

    public Throwable getThrown();

    public ThrowableProxy getThrownProxy();

    public boolean isEndOfBatch();

    public boolean isIncludeLocation();

    public void setEndOfBatch(boolean var1);

    public void setIncludeLocation(boolean var1);

    public long getNanoTime();
}

