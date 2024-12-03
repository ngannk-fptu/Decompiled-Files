/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MembersInjectorLookup<T>
implements Element {
    private final Object source;
    private final TypeLiteral<T> type;
    private MembersInjector<T> delegate;

    public MembersInjectorLookup(Object source, TypeLiteral<T> type) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.type = (TypeLiteral)Preconditions.checkNotNull(type, (Object)"type");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public TypeLiteral<T> getType() {
        return this.type;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void initializeDelegate(MembersInjector<T> delegate) {
        Preconditions.checkState((this.delegate == null ? 1 : 0) != 0, (Object)"delegate already initialized");
        this.delegate = (MembersInjector)Preconditions.checkNotNull(delegate, (Object)"delegate");
    }

    @Override
    public void applyTo(Binder binder) {
        this.initializeDelegate(binder.withSource(this.getSource()).getMembersInjector(this.type));
    }

    public MembersInjector<T> getDelegate() {
        return this.delegate;
    }

    public MembersInjector<T> getMembersInjector() {
        return new MembersInjector<T>(){

            @Override
            public void injectMembers(T instance) {
                Preconditions.checkState((MembersInjectorLookup.this.delegate != null ? 1 : 0) != 0, (Object)"This MembersInjector cannot be used until the Injector has been created.");
                MembersInjectorLookup.this.delegate.injectMembers(instance);
            }

            public String toString() {
                return "MembersInjector<" + MembersInjectorLookup.this.type + ">";
            }
        };
    }
}

