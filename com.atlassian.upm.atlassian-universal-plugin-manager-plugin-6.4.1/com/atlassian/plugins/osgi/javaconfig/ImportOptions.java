/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.eclipse.gemini.blueprint.service.importer.support.Availability
 */
package com.atlassian.plugins.osgi.javaconfig;

import com.atlassian.annotations.PublicApi;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;

@PublicApi
public class ImportOptions {
    private Availability availability = Availability.MANDATORY;
    private Duration timeout = Duration.ofMinutes(5L);
    private String filter;

    public static ImportOptions defaultOptions() {
        return new ImportOptions();
    }

    private ImportOptions() {
    }

    public ImportOptions optional() {
        this.availability = Availability.OPTIONAL;
        return this;
    }

    public ImportOptions withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public ImportOptions withTimeout(Duration timeout) {
        this.timeout = Objects.requireNonNull(timeout);
        return this;
    }

    @Nonnull
    public Availability getAvailability() {
        return this.availability;
    }

    @Nullable
    public String getFilter() {
        return this.filter;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImportOptions that = (ImportOptions)o;
        return this.availability == that.availability && Objects.equals(this.timeout, that.timeout) && Objects.equals(this.filter, that.filter);
    }

    public int hashCode() {
        return Objects.hash(this.availability, this.timeout, this.filter);
    }
}

