/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RequireExactBindingAnnotationsOption
implements Element {
    private final Object source;

    RequireExactBindingAnnotationsOption(Object source) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requireExactBindingAnnotations();
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

