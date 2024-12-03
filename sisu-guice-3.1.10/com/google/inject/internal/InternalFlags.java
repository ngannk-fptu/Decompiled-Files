/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import java.util.Arrays;
import java.util.logging.Logger;

public class InternalFlags {
    private static final Logger logger;
    private static final IncludeStackTraceOption INCLUDE_STACK_TRACES;

    public static IncludeStackTraceOption getIncludeStackTraceOption() {
        return INCLUDE_STACK_TRACES;
    }

    private static IncludeStackTraceOption parseIncludeStackTraceOption() {
        String flag = System.getProperty("guice_include_stack_traces");
        try {
            return flag == null || flag.length() == 0 ? IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE : IncludeStackTraceOption.valueOf(flag);
        }
        catch (IllegalArgumentException e) {
            logger.warning(flag + " is not a valid flag value for guice_include_stack_traces. " + " Values must be one of " + Arrays.asList(IncludeStackTraceOption.values()));
            return IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE;
        }
    }

    static {
        IncludeStackTraceOption includeStackTraces;
        logger = Logger.getLogger(InternalFlags.class.getName());
        try {
            includeStackTraces = InternalFlags.parseIncludeStackTraceOption();
        }
        catch (Throwable e) {
            includeStackTraces = IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE;
        }
        INCLUDE_STACK_TRACES = includeStackTraces;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IncludeStackTraceOption {
        OFF,
        ONLY_FOR_DECLARING_SOURCE,
        COMPLETE;

    }
}

