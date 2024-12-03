/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Scope;
import com.google.inject.spi.BindingScopingVisitor;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultBindingScopingVisitor<V>
implements BindingScopingVisitor<V> {
    protected V visitOther() {
        return null;
    }

    @Override
    public V visitEagerSingleton() {
        return this.visitOther();
    }

    @Override
    public V visitScope(Scope scope) {
        return this.visitOther();
    }

    @Override
    public V visitScopeAnnotation(Class<? extends Annotation> scopeAnnotation) {
        return this.visitOther();
    }

    @Override
    public V visitNoScoping() {
        return this.visitOther();
    }
}

