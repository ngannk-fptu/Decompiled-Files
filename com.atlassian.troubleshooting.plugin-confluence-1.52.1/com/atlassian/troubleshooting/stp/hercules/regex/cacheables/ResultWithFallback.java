/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.stp.hercules.regex.cacheables;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ResultWithFallback<T> {
    private boolean fallback;
    private T value;

    public ResultWithFallback(boolean fallback, T value) {
        this.fallback = fallback;
        this.value = value;
    }

    public static <T> ResultWithFallback<T> success(T value) {
        return new ResultWithFallback<T>(false, value);
    }

    public static <T> ResultWithFallback<T> fallback(T value) {
        return new ResultWithFallback<T>(true, value);
    }

    public boolean isFallback() {
        return this.fallback;
    }

    public T getValue() {
        return this.value;
    }

    public <R> ResultWithFallback<R> map(Function<? super T, ? extends R> mapper) {
        return new ResultWithFallback<R>(this.isFallback(), Objects.requireNonNull(mapper).apply(this.getValue()));
    }

    public static <T> ResultWithFallback<Iterable<T>> allOf(Iterable<ResultWithFallback<T>> results) {
        Objects.requireNonNull(results);
        ArrayList<T> values = new ArrayList<T>();
        boolean fallback = false;
        for (ResultWithFallback<T> result : results) {
            fallback |= result.isFallback();
            values.add(result.getValue());
        }
        return new ResultWithFallback<Iterable<T>>(fallback, values);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResultWithFallback that = (ResultWithFallback)o;
        return new EqualsBuilder().append(this.fallback, that.fallback).append(this.value, that.value).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.fallback).append(this.value).toHashCode();
    }
}

