/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.spi.InjectionPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Dependency<T> {
    private final InjectionPoint injectionPoint;
    private final Key<T> key;
    private final boolean nullable;
    private final int parameterIndex;

    Dependency(InjectionPoint injectionPoint, Key<T> key, boolean nullable, int parameterIndex) {
        this.injectionPoint = injectionPoint;
        this.key = (Key)Preconditions.checkNotNull(key, (Object)"key");
        this.nullable = nullable;
        this.parameterIndex = parameterIndex;
    }

    public static <T> Dependency<T> get(Key<T> key) {
        return new Dependency<T>(null, key, true, -1);
    }

    public static Set<Dependency<?>> forInjectionPoints(Set<InjectionPoint> injectionPoints) {
        ArrayList dependencies = Lists.newArrayList();
        for (InjectionPoint injectionPoint : injectionPoints) {
            dependencies.addAll(injectionPoint.getDependencies());
        }
        return ImmutableSet.copyOf((Collection)dependencies);
    }

    public Key<T> getKey() {
        return this.key;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.injectionPoint, this.parameterIndex, this.key});
    }

    public boolean equals(Object o) {
        if (o instanceof Dependency) {
            Dependency dependency = (Dependency)o;
            return Objects.equal((Object)this.injectionPoint, (Object)dependency.injectionPoint) && Objects.equal((Object)this.parameterIndex, (Object)dependency.parameterIndex) && Objects.equal(this.key, dependency.key);
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.key);
        if (this.injectionPoint != null) {
            builder.append("@").append(this.injectionPoint);
            if (this.parameterIndex != -1) {
                builder.append("[").append(this.parameterIndex).append("]");
            }
        }
        return builder.toString();
    }
}

