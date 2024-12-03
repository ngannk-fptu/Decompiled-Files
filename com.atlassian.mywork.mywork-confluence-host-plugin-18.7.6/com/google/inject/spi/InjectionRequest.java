/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class InjectionRequest<T>
implements Element {
    private final Object source;
    private final TypeLiteral<T> type;
    private final T instance;

    public InjectionRequest(Object source, TypeLiteral<T> type, T instance) {
        this.source = $Preconditions.checkNotNull(source, "source");
        this.type = $Preconditions.checkNotNull(type, "type");
        this.instance = $Preconditions.checkNotNull(instance, "instance");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public T getInstance() {
        return this.instance;
    }

    public TypeLiteral<T> getType() {
        return this.type;
    }

    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forInstanceMethodsAndFields(this.instance.getClass());
    }

    public <R> R acceptVisitor(ElementVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requestInjection(this.type, this.instance);
    }
}

