/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Binding;
import com.google.inject.ProvisionException;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InternalContext;
import com.google.inject.spi.DependencyAndSource;
import com.google.inject.spi.ProvisionListener;
import java.util.LinkedHashSet;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ProvisionListenerStackCallback<T> {
    private static final ProvisionListener[] EMPTY_LISTENER = new ProvisionListener[0];
    private static final ProvisionListenerStackCallback<?> EMPTY_CALLBACK = new ProvisionListenerStackCallback(null, (List<ProvisionListener>)ImmutableList.of());
    private final ProvisionListener[] listeners;
    private final Binding<T> binding;

    public static <T> ProvisionListenerStackCallback<T> emptyListener() {
        return EMPTY_CALLBACK;
    }

    public ProvisionListenerStackCallback(Binding<T> binding, List<ProvisionListener> listeners) {
        this.binding = binding;
        if (listeners.isEmpty()) {
            this.listeners = EMPTY_LISTENER;
        } else {
            LinkedHashSet deDuplicated = Sets.newLinkedHashSet(listeners);
            this.listeners = deDuplicated.toArray(new ProvisionListener[deDuplicated.size()]);
        }
    }

    public boolean hasListeners() {
        return this.listeners.length > 0;
    }

    public T provision(Errors errors, InternalContext context, ProvisionCallback<T> callable) throws ErrorsException {
        Provision provision = new Provision(errors, context, callable);
        RuntimeException caught = null;
        try {
            provision.provision();
        }
        catch (RuntimeException t) {
            caught = t;
        }
        if (provision.exceptionDuringProvision != null) {
            throw provision.exceptionDuringProvision;
        }
        if (caught != null) {
            String listener = provision.erredListener != null ? provision.erredListener.getClass() : "(unknown)";
            throw errors.errorInUserCode(caught, "Error notifying ProvisionListener %s of %s.%n Reason: %s", listener, this.binding.getKey(), caught).toException();
        }
        return provision.result;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class Provision
    extends ProvisionListener.ProvisionInvocation<T> {
        final Errors errors;
        final InternalContext context;
        final ProvisionCallback<T> callable;
        int index = -1;
        T result;
        ErrorsException exceptionDuringProvision;
        ProvisionListener erredListener;

        public Provision(Errors errors, InternalContext context, ProvisionCallback<T> callable) {
            this.callable = callable;
            this.context = context;
            this.errors = errors;
        }

        @Override
        public T provision() {
            ++this.index;
            if (this.index == ProvisionListenerStackCallback.this.listeners.length) {
                try {
                    this.result = this.callable.call();
                }
                catch (ErrorsException ee) {
                    this.exceptionDuringProvision = ee;
                    throw new ProvisionException(this.errors.merge(ee.getErrors()).getMessages());
                }
            }
            if (this.index < ProvisionListenerStackCallback.this.listeners.length) {
                int currentIdx = this.index;
                try {
                    ProvisionListenerStackCallback.this.listeners[this.index].onProvision(this);
                }
                catch (RuntimeException re) {
                    this.erredListener = ProvisionListenerStackCallback.this.listeners[currentIdx];
                    throw re;
                }
                if (currentIdx == this.index) {
                    this.provision();
                }
            } else {
                throw new IllegalStateException("Already provisioned in this listener.");
            }
            return this.result;
        }

        @Override
        public Binding<T> getBinding() {
            return ProvisionListenerStackCallback.this.binding;
        }

        @Override
        public List<DependencyAndSource> getDependencyChain() {
            return this.context.getDependencyChain();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ProvisionCallback<T> {
        public T call() throws ErrorsException;
    }
}

