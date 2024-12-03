/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.SystemUtils
 *  org.apache.commons.lang.exception.ExceptionUtils
 *  org.apache.log4j.Logger
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.dispatcher;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationError {
    private static final Logger log = Logger.getLogger(NotificationError.class);
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String stackTrace;

    public NotificationError(String message) {
        this.message = message;
        this.stackTrace = null;
    }

    public NotificationError(String message, Throwable throwable) {
        this.message = message;
        String stackTrace = ExceptionUtils.getMessage((Throwable)throwable);
        if (log.isDebugEnabled()) {
            stackTrace = stackTrace + SystemUtils.LINE_SEPARATOR + ExceptionUtils.getFullStackTrace((Throwable)throwable);
        }
        this.stackTrace = stackTrace;
    }

    public String getMessage() {
        return this.message;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }
}

