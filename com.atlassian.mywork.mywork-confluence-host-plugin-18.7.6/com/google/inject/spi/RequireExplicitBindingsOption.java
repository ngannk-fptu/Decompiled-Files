/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RequireExplicitBindingsOption
implements Element {
    private final Object source;

    RequireExplicitBindingsOption(Object source) {
        this.source = $Preconditions.checkNotNull(source, "source");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requireExplicitBindings();
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

