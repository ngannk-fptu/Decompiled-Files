/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.Dependency;
import java.lang.reflect.Member;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DependencyAndSource {
    private final Dependency<?> dependency;
    private final Object source;

    public DependencyAndSource(Dependency<?> dependency, Object source) {
        this.dependency = dependency;
        this.source = source;
    }

    public Dependency<?> getDependency() {
        return this.dependency;
    }

    public String getBindingSource() {
        if (this.source instanceof Class) {
            return StackTraceElements.forType((Class)this.source).toString();
        }
        if (this.source instanceof Member) {
            return StackTraceElements.forMember((Member)this.source).toString();
        }
        return this.source.toString();
    }

    public String toString() {
        Dependency<?> dep = this.getDependency();
        String source = this.getBindingSource();
        if (dep != null) {
            return "Dependency: " + dep + ", source: " + source;
        }
        return "Source: " + source;
    }
}

