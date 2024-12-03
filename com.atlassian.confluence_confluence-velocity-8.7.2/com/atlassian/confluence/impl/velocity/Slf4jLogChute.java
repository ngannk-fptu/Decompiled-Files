/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.log.LogChute
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.velocity;

import com.atlassian.confluence.impl.velocity.Slf4jLogSink;
import java.util.Map;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slf4jLogChute
implements LogChute {
    private Map<Integer, Slf4jLogSink> sinks;

    public void init(RuntimeServices runtimeServices) throws Exception {
        String loggerName = runtimeServices.getString("runtime.log.logsystem.slf4j.logger", this.getClass().getName());
        Logger logger = LoggerFactory.getLogger((String)loggerName);
        this.sinks = Slf4jLogSink.sinks(logger);
    }

    public void log(int level, String message) {
        this.sinks.get(level).log(message);
    }

    public void log(int level, String message, Throwable t) {
        this.sinks.get(level).log(message, t);
    }

    public boolean isLevelEnabled(int level) {
        return this.sinks.get(level).isEnabled();
    }
}

