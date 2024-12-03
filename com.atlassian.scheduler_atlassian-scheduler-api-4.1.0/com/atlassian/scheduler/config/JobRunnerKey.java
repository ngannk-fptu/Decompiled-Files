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
public final class JobRunnerKey
implements Serializable,
Comparable<JobRunnerKey> {
    private static final long serialVersionUID = 1L;
    private final String key;

    public static JobRunnerKey of(String key) {
        return new JobRunnerKey(key);
    }

    private JobRunnerKey(String key) {
        this.key = Objects.requireNonNull(key, "key");
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        return o != null && o.getClass() == this.getClass() && ((JobRunnerKey)o).key.equals(this.key);
    }

    @Override
    public int compareTo(JobRunnerKey o) {
        return this.key.compareTo(o.key);
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public String toString() {
        return this.key;
    }
}

