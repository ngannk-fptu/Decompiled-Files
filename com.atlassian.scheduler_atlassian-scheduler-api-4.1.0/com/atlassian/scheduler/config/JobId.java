/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.config;

import com.atlassian.annotations.PublicApi;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class JobId
implements Serializable,
Comparable<JobId> {
    private static final long serialVersionUID = 1L;
    private final String id;

    public static JobId of(String id) {
        return new JobId(id);
    }

    private JobId(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        return o != null && o.getClass() == this.getClass() && ((JobId)o).id.equals(this.id);
    }

    @Override
    public int compareTo(JobId o) {
        return this.id.compareTo(o.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id;
    }
}

