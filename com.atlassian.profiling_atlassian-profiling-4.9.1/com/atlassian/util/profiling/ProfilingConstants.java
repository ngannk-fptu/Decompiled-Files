/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;

@Internal
public final class ProfilingConstants {
    public static final String OPTIONAL_TAG_PROPERTY_PREFIX = "atlassian.metrics.optional.tags.";
    public static final String ACTIVATE_MEMORY_PROPERTY = "atlassian.profile.activate.memory";
    public static final String ACTIVATE_METRICS_PROPERTY = "atlassian.metrics.activate";
    public static final String ACTIVATE_PROPERTY = "atlassian.profile.activate";
    public static final String DEFAULT_ACTIVATE_PROPERTY = "false";
    public static final String DEFAULT_ACTIVATE_METRICS_PROPERTY = "true";
    public static final String DEFAULT_ACTIVATE_MEMORY_PROPERTY = "false";
    public static final int DEFAULT_MAX_FRAME_COUNT = 1000;
    public static final int DEFAULT_MAX_FRAME_LENGTH = 150;
    public static final int DEFAULT_MIN_TIME = 0;
    public static final int DEFAULT_MIN_TOTAL_TIME = 0;
    public static final String MAX_FRAME_COUNT = "atlassian.profile.maxframecount";
    public static final String MAX_FRAME_LENGTH = "atlassian.profile.maxframelength";
    public static final String MIN_TIME = "atlassian.profile.mintime";
    public static final String MIN_TOTAL_TIME = "atlassian.profile.mintotaltime";

    private ProfilingConstants() {
        throw new IllegalArgumentException("ProfilingConstants should not be instantiated");
    }
}

