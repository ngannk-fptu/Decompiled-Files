/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.graph;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DependencyEdge<T> {
    private T source;
    private T target;

    @Nonnull
    public T getSource() {
        return this.source;
    }

    void setSource(@Nonnull T source) {
        this.source = Objects.requireNonNull(source, "The source vertex is mandatory.");
    }

    @Nonnull
    public T getTarget() {
        return this.target;
    }

    void setTarget(@Nonnull T target) {
        this.target = Objects.requireNonNull(target, "The target vertex is mandatory.");
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof DependencyEdge) {
            DependencyEdge otherDependency = (DependencyEdge)other;
            return otherDependency.source.equals(this.source) && otherDependency.target.equals(this.target);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.source, this.target);
    }

    public String toString() {
        return "(" + this.source + " : " + this.target + ')';
    }
}

