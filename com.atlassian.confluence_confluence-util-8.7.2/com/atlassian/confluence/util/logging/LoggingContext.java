/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.log4j.MDC
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.logging;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.MDC;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public final class LoggingContext {
    private static final Logger log = LoggerFactory.getLogger(LoggingContext.class);
    private static final String URL_KEY = "url";
    private static final String USER_NAME_KEY = "userName";
    private static final String REFERER_KEY = "referer";

    private LoggingContext() {
    }

    public static void setUrl(@Nullable String url) {
        if (url != null) {
            LoggingContext.put(URL_KEY, url);
        }
    }

    public static void setUserName(@Nullable String userName) {
        LoggingContext.put(USER_NAME_KEY, userName == null ? "anonymous" : userName);
    }

    public static String getUserName() {
        return (String)MDC.get((String)USER_NAME_KEY);
    }

    public static void setReferer(@Nullable String referer) {
        if (referer != null) {
            LoggingContext.put(REFERER_KEY, referer);
        }
    }

    public static void put(String key, Object value) {
        MDC.put((String)key, (Object)value);
    }

    public static void remove(String ... keys) {
        for (String key : keys) {
            MDC.remove((String)key);
        }
    }

    public static boolean isEmpty() {
        return MDC.getContext() == null || MDC.getContext().isEmpty();
    }

    public static Map<String, Object> getContext() {
        return Collections.unmodifiableMap(new LinkedHashMap(MDC.getContext()));
    }

    public static void clear() {
        LoggingContext.remove(URL_KEY, USER_NAME_KEY, REFERER_KEY);
    }

    public static void clearUsername() {
        LoggingContext.remove(USER_NAME_KEY);
    }

    public static void clearAll() {
        MDC.clear();
    }

    public static void executeWithContext(Map<String, Object> context, Runnable runnable) {
        try {
            context.entrySet().forEach(e -> MDC.put((String)((String)e.getKey()), e.getValue()));
        }
        catch (RuntimeException e2) {
            log.warn("Failed to put all of the following values to MDC: {}", context, (Object)e2);
        }
        try {
            runnable.run();
        }
        finally {
            try {
                context.keySet().forEach(MDC::remove);
            }
            catch (RuntimeException e3) {
                log.warn("Failed to remove all of the following keys from MDC: {}", context.keySet(), (Object)e3);
            }
        }
    }

    public static void executeWithContext(String key, Object value, Runnable runnable) {
        LoggingContext.executeWithContext(Collections.singletonMap(key, value), runnable);
    }
}

