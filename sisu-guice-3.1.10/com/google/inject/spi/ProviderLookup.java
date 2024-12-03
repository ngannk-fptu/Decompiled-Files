/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ProviderLookup<T>
implements Element {
    private final Object source;
    private Key<T> key;
    private Provider<T> delegate;

    public ProviderLookup(Object source, Key<T> key) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.key = (Key)Preconditions.checkNotNull(key, (Object)"key");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public Key<T> getKey() {
        return this.key;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void initializeDelegate(Provider<T> delegate) {
        Preconditions.checkState((this.delegate == null ? 1 : 0) != 0, (Object)"delegate already initialized");
        this.delegate = (Provider)Preconditions.checkNotNull(delegate, (Object)"delegate");
    }

    @Override
    public void applyTo(Binder binder) {
        this.initializeDelegate(binder.withSource(this.getSource()).getProvider(this.key));
    }

    public Provider<T> getDelegate() {
        return this.delegate;
    }

    public Provider<T> getProvider() {
        return new Provider<T>(){

            @Override
            public T get() {
                Preconditions.checkState((ProviderLookup.this.delegate != null ? 1 : 0) != 0, (Object)"This Provider cannot be used until the Injector has been created.");
                return ProviderLookup.this.delegate.get();
            }

            public String toString() {
                return "Provider<" + ProviderLookup.this.key.getTypeLiteral() + ">";
            }
        };
    }

    RehashableKeys getKeyRehasher() {
        return new RehashableKeys(){

            public void rehashKeys() {
                if (RehashableKeys.Keys.needsRehashing(ProviderLookup.this.key)) {
                    ProviderLookup.this.key = RehashableKeys.Keys.rehash(ProviderLookup.this.key);
                }
            }
        };
    }
}

