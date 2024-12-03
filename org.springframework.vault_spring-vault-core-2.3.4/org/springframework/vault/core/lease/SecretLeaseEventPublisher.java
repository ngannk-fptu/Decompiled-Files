/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.core.lease;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.AfterSecretLeaseRenewedEvent;
import org.springframework.vault.core.lease.event.AfterSecretLeaseRevocationEvent;
import org.springframework.vault.core.lease.event.BeforeSecretLeaseRevocationEvent;
import org.springframework.vault.core.lease.event.LeaseErrorListener;
import org.springframework.vault.core.lease.event.LeaseListener;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseErrorEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;
import org.springframework.vault.core.lease.event.SecretLeaseRotatedEvent;
import org.springframework.vault.core.lease.event.SecretNotFoundEvent;

public class SecretLeaseEventPublisher
implements InitializingBean {
    private final Set<LeaseListener> leaseListeners = new CopyOnWriteArraySet<LeaseListener>();
    private final Set<LeaseErrorListener> leaseErrorListeners = new CopyOnWriteArraySet<LeaseErrorListener>();

    public void addLeaseListener(LeaseListener listener) {
        Assert.notNull((Object)listener, (String)"LeaseListener must not be null");
        this.leaseListeners.add(listener);
    }

    public void removeLeaseListener(LeaseListener listener) {
        this.leaseListeners.remove(listener);
    }

    public void addErrorListener(LeaseErrorListener listener) {
        Assert.notNull((Object)listener, (String)"LeaseListener must not be null");
        this.leaseErrorListeners.add(listener);
    }

    public void removeLeaseErrorListener(LeaseErrorListener listener) {
        this.leaseErrorListeners.remove(listener);
    }

    public void afterPropertiesSet() {
        if (this.leaseErrorListeners.isEmpty()) {
            this.addErrorListener(LoggingErrorListener.INSTANCE);
        }
    }

    protected void onSecretsObtained(RequestedSecret requestedSecret, Lease lease, Map<String, Object> body) {
        this.dispatch(new SecretLeaseCreatedEvent(requestedSecret, lease, body));
    }

    protected void onSecretsRotated(RequestedSecret requestedSecret, Lease previousLease, Lease lease, Map<String, Object> body) {
        this.dispatch(new SecretLeaseRotatedEvent(requestedSecret, previousLease, lease, body));
    }

    protected void onSecretsNotFound(RequestedSecret requestedSecret) {
        this.dispatch(new SecretNotFoundEvent(requestedSecret, Lease.none()));
    }

    protected void onAfterLeaseRenewed(RequestedSecret requestedSecret, Lease lease) {
        this.dispatch(new AfterSecretLeaseRenewedEvent(requestedSecret, lease));
    }

    protected void onBeforeLeaseRevocation(RequestedSecret requestedSecret, Lease lease) {
        this.dispatch(new BeforeSecretLeaseRevocationEvent(requestedSecret, lease));
    }

    protected void onAfterLeaseRevocation(RequestedSecret requestedSecret, Lease lease) {
        this.dispatch(new AfterSecretLeaseRevocationEvent(requestedSecret, lease));
    }

    protected void onLeaseExpired(RequestedSecret requestedSecret, Lease lease) {
        this.dispatch(new SecretLeaseExpiredEvent(requestedSecret, lease));
    }

    protected void onError(RequestedSecret requestedSecret, @Nullable Lease lease, Exception e) {
        this.dispatch(new SecretLeaseErrorEvent(requestedSecret, lease, e));
    }

    void dispatch(SecretLeaseEvent leaseEvent) {
        for (LeaseListener listener : this.leaseListeners) {
            listener.onLeaseEvent(leaseEvent);
        }
    }

    void dispatch(SecretLeaseErrorEvent errorEvent) {
        for (LeaseErrorListener listener : this.leaseErrorListeners) {
            listener.onLeaseError(errorEvent, (Exception)errorEvent.getException());
        }
    }

    public static enum LoggingErrorListener implements LeaseErrorListener
    {
        INSTANCE;

        private static Log logger;

        @Override
        public void onLeaseError(SecretLeaseEvent leaseEvent, Exception exception) {
            logger.warn((Object)String.format("[%s] %s %s", leaseEvent.getSource(), leaseEvent.getLease(), exception.getMessage()), (Throwable)exception);
        }

        static {
            logger = LogFactory.getLog(LoggingErrorListener.class);
        }
    }
}

