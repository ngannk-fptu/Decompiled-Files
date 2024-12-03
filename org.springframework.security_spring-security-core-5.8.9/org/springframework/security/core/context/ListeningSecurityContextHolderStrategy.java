/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextChangedEvent;
import org.springframework.security.core.context.SecurityContextChangedListener;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy;
import org.springframework.util.Assert;

public final class ListeningSecurityContextHolderStrategy
implements SecurityContextHolderStrategy {
    private final Collection<SecurityContextChangedListener> listeners;
    private final SecurityContextHolderStrategy delegate;

    public ListeningSecurityContextHolderStrategy(Collection<SecurityContextChangedListener> listeners) {
        this((SecurityContextHolderStrategy)new ThreadLocalSecurityContextHolderStrategy(), listeners);
    }

    public ListeningSecurityContextHolderStrategy(SecurityContextChangedListener ... listeners) {
        this((SecurityContextHolderStrategy)new ThreadLocalSecurityContextHolderStrategy(), listeners);
    }

    public ListeningSecurityContextHolderStrategy(SecurityContextHolderStrategy delegate, Collection<SecurityContextChangedListener> listeners) {
        Assert.notNull((Object)delegate, (String)"securityContextHolderStrategy cannot be null");
        Assert.notNull(listeners, (String)"securityContextChangedListeners cannot be null");
        Assert.notEmpty(listeners, (String)"securityContextChangedListeners cannot be empty");
        Assert.noNullElements(listeners, (String)"securityContextChangedListeners cannot contain null elements");
        this.delegate = delegate;
        this.listeners = listeners;
    }

    public ListeningSecurityContextHolderStrategy(SecurityContextHolderStrategy delegate, SecurityContextChangedListener ... listeners) {
        Assert.notNull((Object)delegate, (String)"securityContextHolderStrategy cannot be null");
        Assert.notNull((Object)listeners, (String)"securityContextChangedListeners cannot be null");
        Assert.notEmpty((Object[])listeners, (String)"securityContextChangedListeners cannot be empty");
        Assert.noNullElements((Object[])listeners, (String)"securityContextChangedListeners cannot contain null elements");
        this.delegate = delegate;
        this.listeners = Arrays.asList(listeners);
    }

    @Override
    public void clearContext() {
        Supplier<SecurityContext> deferred = this.delegate.getDeferredContext();
        this.delegate.clearContext();
        this.publish(new SecurityContextChangedEvent(deferred, SecurityContextChangedEvent.NO_CONTEXT));
    }

    @Override
    public SecurityContext getContext() {
        return this.delegate.getContext();
    }

    @Override
    public Supplier<SecurityContext> getDeferredContext() {
        return this.delegate.getDeferredContext();
    }

    @Override
    public void setContext(SecurityContext context) {
        this.setDeferredContext(() -> context);
    }

    @Override
    public void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        this.delegate.setDeferredContext(new PublishOnceSupplier(this.getDeferredContext(), deferredContext));
    }

    @Override
    public SecurityContext createEmptyContext() {
        return this.delegate.createEmptyContext();
    }

    private void publish(SecurityContextChangedEvent event) {
        for (SecurityContextChangedListener listener : this.listeners) {
            listener.securityContextChanged(event);
        }
    }

    class PublishOnceSupplier
    implements Supplier<SecurityContext> {
        private final AtomicBoolean isPublished = new AtomicBoolean(false);
        private final Supplier<SecurityContext> old;
        private final Supplier<SecurityContext> updated;

        PublishOnceSupplier(Supplier<SecurityContext> old, Supplier<SecurityContext> updated) {
            this.old = old instanceof PublishOnceSupplier ? ((PublishOnceSupplier)old).updated : old;
            this.updated = updated;
        }

        @Override
        public SecurityContext get() {
            SecurityContext old;
            SecurityContext updated = this.updated.get();
            if (this.isPublished.compareAndSet(false, true) && (old = this.old.get()) != updated) {
                ListeningSecurityContextHolderStrategy.this.publish(new SecurityContextChangedEvent(old, updated));
            }
            return updated;
        }
    }
}

