/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.util;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class Pair<S, T> {
    private final S first;
    private final T second;

    private Pair(S first, T second) {
        Assert.notNull(first, (String)"First must not be null!");
        Assert.notNull(second, (String)"Second must not be null!");
        this.first = first;
        this.second = second;
    }

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<S, T>(first, second);
    }

    public S getFirst() {
        return this.first;
    }

    public T getSecond() {
        return this.second;
    }

    public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
        return Collectors.toMap(Pair::getFirst, Pair::getSecond);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair)o;
        if (!ObjectUtils.nullSafeEquals(this.first, pair.first)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.second, pair.second);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.first);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.second);
        return result;
    }

    public String toString() {
        return String.format("%s->%s", this.first, this.second);
    }
}

