/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.lang.Nullable;

public class MethodOverrides {
    private final Set<MethodOverride> overrides = new CopyOnWriteArraySet<MethodOverride>();

    public MethodOverrides() {
    }

    public MethodOverrides(MethodOverrides other) {
        this.addOverrides(other);
    }

    public void addOverrides(@Nullable MethodOverrides other) {
        if (other != null) {
            this.overrides.addAll(other.overrides);
        }
    }

    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }

    public Set<MethodOverride> getOverrides() {
        return this.overrides;
    }

    public boolean isEmpty() {
        return this.overrides.isEmpty();
    }

    @Nullable
    public MethodOverride getOverride(Method method) {
        MethodOverride match = null;
        for (MethodOverride candidate : this.overrides) {
            if (!candidate.matches(method)) continue;
            match = candidate;
        }
        return match;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodOverrides)) {
            return false;
        }
        MethodOverrides that = (MethodOverrides)other;
        return this.overrides.equals(that.overrides);
    }

    public int hashCode() {
        return this.overrides.hashCode();
    }
}

