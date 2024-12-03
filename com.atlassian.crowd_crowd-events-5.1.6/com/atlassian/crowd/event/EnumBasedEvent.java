/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.crowd.event;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Objects;

public class EnumBasedEvent<T extends Enum> {
    protected final T data;

    protected EnumBasedEvent(T data) {
        this.data = (Enum)Objects.requireNonNull(data);
    }

    public static <E extends Enum, T> ImmutableMap<E, T> createMapByEnum(E[] enumValues, Function<E, T> constructor) {
        return Maps.toMap(Arrays.asList(enumValues), constructor);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EnumBasedEvent that = (EnumBasedEvent)o;
        return Objects.equals(this.data, that.data);
    }

    public int hashCode() {
        return Objects.hash(this.data);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("data", this.data).toString();
    }
}

