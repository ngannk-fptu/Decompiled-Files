/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package net.java.ao.sql;

import java.time.Clock;
import java.util.Collections;
import java.util.Map;
import net.java.ao.sql.CallStackProvider;
import org.slf4j.Logger;

public class LoggingInterceptor {
    private final Logger logger;
    private final CallStackProvider callStackProvider;
    private final Clock clock;
    private final boolean callStackLogging;
    private long executionStartTime;

    public LoggingInterceptor(Logger logger, CallStackProvider callStackProvider, Clock clock) {
        this(logger, callStackProvider, clock, false);
    }

    public LoggingInterceptor(Logger logger, CallStackProvider callStackProvider, Clock clock, boolean callStackLogging) {
        this.callStackProvider = callStackProvider;
        this.logger = logger;
        this.clock = clock;
        this.callStackLogging = callStackLogging;
    }

    public void beforeExecution() {
        this.executionStartTime = this.clock.millis();
    }

    public void afterSuccessfulExecution(String query) {
        this.afterSuccessfulExecution(query, Collections.emptyMap());
    }

    public void afterSuccessfulExecution(String query, Map<Integer, String> params) {
        query = this.getFullQuery(query, params);
        if (this.isCallStackLoggingEnabled()) {
            this.logger.debug(query + "\ncall stack\n\t...\n" + this.callStackProvider.getCallStack() + "\t...");
        } else {
            this.logger.debug(query);
        }
    }

    private boolean isCallStackLoggingEnabled() {
        return this.callStackLogging;
    }

    public void onException(Exception exception) {
        this.logger.error(exception.getMessage(), (Throwable)exception);
    }

    public void onException(String query, Exception exception) {
        this.onException(query, Collections.emptyMap(), exception);
    }

    public void onException(String query, Map<Integer, String> params, Exception exception) {
        this.logger.error(this.getFullQuery(query, params), (Throwable)exception);
    }

    private String getFullQuery(String query, Map<Integer, String> params) {
        return this.joinQueryAndParams(this.getQueryWithExecutionTime(query), params);
    }

    private String joinQueryAndParams(String query, Map<Integer, String> params) {
        if (params.isEmpty()) {
            return query;
        }
        return query + " " + params.toString();
    }

    private String getQueryWithExecutionTime(String query) {
        return this.getExecutionTime() + " ms \"" + query + "\"";
    }

    private long getExecutionTime() {
        return this.clock.millis() - this.executionStartTime;
    }
}

