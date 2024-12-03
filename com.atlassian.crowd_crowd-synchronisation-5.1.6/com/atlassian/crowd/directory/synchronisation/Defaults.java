/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.synchronisation;

import java.time.Duration;

public final class Defaults {
    public static final Duration READ_TIMEOUT = Duration.ofSeconds(120L);
    public static final Duration SEARCH_TIMEOUT = Duration.ofSeconds(60L);
    public static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10L);

    private Defaults() {
    }
}

