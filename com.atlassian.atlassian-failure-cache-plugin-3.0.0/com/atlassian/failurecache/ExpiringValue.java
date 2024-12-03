/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.util.date.Clock;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ExpiringValue<V> {
    private final V value;
    private final long staleAfterInMillis;
    private final long expiredAfterInMillis;

    public ExpiringValue(@Nullable V value, long staleAfterInMillis, long expiredAfterInMillis) {
        this.value = value;
        this.staleAfterInMillis = staleAfterInMillis;
        this.expiredAfterInMillis = expiredAfterInMillis;
    }

    public static <V> ExpiringValue<V> expiredNullValue() {
        return ExpiringValue.expiredValue(null);
    }

    public static <V> ExpiringValue<V> expiredValue(@Nullable V value) {
        long now = System.currentTimeMillis();
        return new ExpiringValue<V>(value, now, now);
    }

    @Nullable
    public V getValue() {
        return this.value;
    }

    public boolean isStale(Clock clock) {
        Preconditions.checkNotNull((Object)clock, (Object)"clock");
        return clock.getCurrentDate().getTime() >= this.staleAfterInMillis;
    }

    public boolean isExpired(Clock clock) {
        Preconditions.checkNotNull((Object)clock, (Object)"clock");
        return clock.getCurrentDate().getTime() >= this.expiredAfterInMillis;
    }

    public boolean isValid(Clock clock) {
        Preconditions.checkNotNull((Object)clock, (Object)"clock");
        return !this.isExpired(clock);
    }

    public <V> ExpiringValue<V> withSameExpirationDate(@Nullable V value) {
        return new ExpiringValue<V>(value, this.staleAfterInMillis, this.expiredAfterInMillis);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExpiringValue that = (ExpiringValue)o;
        return this.expiredAfterInMillis == that.expiredAfterInMillis && this.staleAfterInMillis == that.staleAfterInMillis && (this.value != null ? this.value.equals(that.value) : that.value == null);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.value, this.staleAfterInMillis, this.expiredAfterInMillis});
    }

    public String toString() {
        return "ExpiringValue{value=" + this.value + ", staleAfterInMillis=" + this.staleAfterInMillis + ", expiredAfterInMillis=" + this.expiredAfterInMillis + '}';
    }

    public static <V> Function<ExpiringValue<V>, V> extractValue() {
        return new Function<ExpiringValue<V>, V>(){

            public V apply(@Nullable ExpiringValue<V> from) {
                return from != null ? (Object)from.getValue() : null;
            }
        };
    }
}

