/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class StaticInjectionRequest
implements Element {
    private final Object source;
    private final Class<?> type;

    StaticInjectionRequest(Object source, Class<?> type) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.type = (Class)Preconditions.checkNotNull(type, (Object)"type");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Set<InjectionPoint> getInjectionPoints() throws ConfigurationException {
        return InjectionPoint.forStaticMethodsAndFields(this.type);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).requestStaticInjection(this.type);
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

