/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.Supplier
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.Deferred;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeferredCachedReference<V>
implements CachedReference<V>,
Deferred {
    private static final Logger log = LoggerFactory.getLogger(DeferredCachedReference.class);
    private final String name;
    private final CachedReference<V> delegate;
    private final ResettableLazyReference<V> reference;
    private Operation lastOperation = null;

    public static <V> DeferredCachedReference<V> create(String name, final Supplier<V> supplier, CachedReference<V> backingCachedReference) {
        ResettableLazyReference reference = new ResettableLazyReference<V>(){

            protected V create() {
                return supplier.get();
            }
        };
        return new DeferredCachedReference<V>(name, backingCachedReference, reference);
    }

    private DeferredCachedReference(String name, CachedReference<V> delegate, ResettableLazyReference<V> reference) {
        this.name = (String)Preconditions.checkNotNull((Object)name);
        this.reference = (ResettableLazyReference)Preconditions.checkNotNull(reference);
        this.delegate = (CachedReference)Preconditions.checkNotNull(delegate);
    }

    @Nonnull
    public V get() {
        if (this.reference.isInitialized()) {
            this.lastOperation = Operation.GET;
            return (V)this.reference.get();
        }
        Optional val = this.delegate.getIfPresent();
        if (val.isPresent()) {
            return (V)val.get();
        }
        Object value = this.reference.get();
        log.debug("Deferring getting from cached reference [{}]", (Object)this.name);
        this.lastOperation = Operation.GET;
        return (V)value;
    }

    public void reset() {
        this.reference.reset();
        log.debug("Deferring resetting cached reference [{}]", (Object)this.name);
        this.lastOperation = Operation.RESET;
    }

    public boolean isPresent() {
        return this.lastOperation != Operation.RESET && (this.reference.isInitialized() || this.delegate.isPresent());
    }

    @Nonnull
    public Optional<V> getIfPresent() {
        if (this.lastOperation == Operation.RESET) {
            return Optional.empty();
        }
        if (this.reference.isInitialized()) {
            return Optional.of(this.reference.get());
        }
        return this.delegate.getIfPresent();
    }

    public void addListener(@NonNull CachedReferenceListener<V> vCachedReferenceListener, boolean b) {
        this.delegate.addListener(vCachedReferenceListener, b);
    }

    public void removeListener(@NonNull CachedReferenceListener<V> vCachedReferenceListener) {
        this.delegate.removeListener(vCachedReferenceListener);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return "cached reference";
    }

    @Override
    public boolean hasDeferredOperations() {
        return this.lastOperation != null;
    }

    @Override
    public void clear() {
        this.delegate.reset();
    }

    @Override
    public void sync() {
        if (this.lastOperation == Operation.GET) {
            this.delegate.get();
        } else if (this.lastOperation == Operation.RESET) {
            this.delegate.reset();
        } else if (this.lastOperation != null) {
            throw new UnsupportedOperationException("I don't know how to synchronise " + this.lastOperation + " operation");
        }
    }

    private static enum Operation {
        GET,
        RESET;

    }
}

