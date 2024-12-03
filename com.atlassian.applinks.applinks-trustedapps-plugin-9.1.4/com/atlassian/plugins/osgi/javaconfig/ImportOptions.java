/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.osgi.javaconfig;

import com.atlassian.annotations.PublicApi;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nullable;

@PublicApi
public class ImportOptions {
    private Duration timeout = Duration.ofMinutes(5L);
    private String filter;

    public static ImportOptions defaultOptions() {
        return new ImportOptions();
    }

    private ImportOptions() {
    }

    public ImportOptions withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public ImportOptions withTimeout(Duration timeout) {
        this.timeout = Objects.requireNonNull(timeout);
        return this;
    }

    @Nullable
    public String getFilter() {
        return this.filter;
    }

    public Duration getTimeout() {
        return this.timeout;
    }
}

