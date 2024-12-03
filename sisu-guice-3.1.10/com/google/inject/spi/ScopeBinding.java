/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Scope;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ScopeBinding
implements Element {
    private final Object source;
    private final Class<? extends Annotation> annotationType;
    private final Scope scope;

    ScopeBinding(Object source, Class<? extends Annotation> annotationType, Scope scope) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.annotationType = (Class)Preconditions.checkNotNull(annotationType, (Object)"annotationType");
        this.scope = (Scope)Preconditions.checkNotNull((Object)scope, (Object)"scope");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    public Scope getScope() {
        return this.scope;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).bindScope(this.annotationType, this.scope);
    }
}

