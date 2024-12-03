/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.service.journal;

import com.atlassian.annotations.PublicApi;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

@PublicApi
public class EntryProcessorResult<V> {
    private final V result;
    private final Long lastSuccessfulId;
    private final Long failedEntryId;

    private EntryProcessorResult(V result, Long lastSuccessfulId, Long failedEntryId) {
        this.result = result;
        this.lastSuccessfulId = lastSuccessfulId;
        this.failedEntryId = failedEntryId;
    }

    public V getResult() {
        return this.result;
    }

    public Long getLastSuccessfulId() {
        return this.lastSuccessfulId;
    }

    public Long getFailedEntryId() {
        return this.failedEntryId;
    }

    public static <V> EntryProcessorResult<V> success(@Nullable V result) {
        return new EntryProcessorResult<V>(result, null, null);
    }

    public static <V> EntryProcessorResult<V> partial(@Nullable V result, long lastSuccessfulId) {
        return new EntryProcessorResult<V>(result, lastSuccessfulId, null);
    }

    public static <V> EntryProcessorResult<V> failure(@Nullable V result, long failedEntryId) {
        return new EntryProcessorResult<V>(result, null, failedEntryId);
    }

    public boolean equals(Object o) {
        if (o instanceof EntryProcessorResult) {
            EntryProcessorResult other = (EntryProcessorResult)o;
            return Objects.equals(this.result, other.result) && Objects.equals(this.lastSuccessfulId, other.lastSuccessfulId) && Objects.equals(this.failedEntryId, other.failedEntryId);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.result, this.lastSuccessfulId, this.failedEntryId);
    }

    public String toString() {
        return "EntryProcessorResult{result=" + this.result + ", lastSuccessfulId=" + this.lastSuccessfulId + ", failedEntryId=" + this.failedEntryId + '}';
    }
}

