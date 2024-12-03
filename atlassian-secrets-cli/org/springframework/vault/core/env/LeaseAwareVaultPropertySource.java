/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.env;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.env.VaultPropertySourceNotFoundException;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.BeforeSecretLeaseRevocationEvent;
import org.springframework.vault.core.lease.event.LeaseListenerAdapter;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;
import org.springframework.vault.core.lease.event.SecretLeaseRotatedEvent;
import org.springframework.vault.core.lease.event.SecretNotFoundEvent;
import org.springframework.vault.core.util.PropertyTransformer;
import org.springframework.vault.core.util.PropertyTransformers;
import org.springframework.vault.support.JsonMapFlattener;

public class LeaseAwareVaultPropertySource
extends EnumerablePropertySource<VaultOperations> {
    private static Log logger = LogFactory.getLog(LeaseAwareVaultPropertySource.class);
    private final SecretLeaseContainer secretLeaseContainer;
    private final RequestedSecret requestedSecret;
    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
    private final PropertyTransformer propertyTransformer;
    private final boolean ignoreSecretNotFound;
    private final LeaseListenerAdapter leaseListener;
    private volatile boolean notFound = false;
    @Nullable
    private volatile Exception loadError;

    public LeaseAwareVaultPropertySource(SecretLeaseContainer secretLeaseContainer, RequestedSecret requestedSecret) {
        this(requestedSecret.getPath(), secretLeaseContainer, requestedSecret);
    }

    public LeaseAwareVaultPropertySource(String name, SecretLeaseContainer secretLeaseContainer, RequestedSecret requestedSecret) {
        this(name, secretLeaseContainer, requestedSecret, PropertyTransformers.noop());
    }

    public LeaseAwareVaultPropertySource(String name, SecretLeaseContainer secretLeaseContainer, RequestedSecret requestedSecret, PropertyTransformer propertyTransformer) {
        this(name, secretLeaseContainer, requestedSecret, propertyTransformer, true);
    }

    public LeaseAwareVaultPropertySource(String name, SecretLeaseContainer secretLeaseContainer, RequestedSecret requestedSecret, PropertyTransformer propertyTransformer, boolean ignoreSecretNotFound) {
        super(name);
        Assert.notNull((Object)secretLeaseContainer, "Path name must contain at least one character");
        Assert.notNull((Object)requestedSecret, "SecretLeaseContainer must not be null");
        Assert.notNull((Object)propertyTransformer, "PropertyTransformer must not be null");
        this.secretLeaseContainer = secretLeaseContainer;
        this.requestedSecret = requestedSecret;
        this.propertyTransformer = propertyTransformer.andThen(PropertyTransformers.removeNullProperties());
        this.ignoreSecretNotFound = ignoreSecretNotFound;
        this.leaseListener = new LeaseListenerAdapter(){

            @Override
            public void onLeaseEvent(SecretLeaseEvent leaseEvent) {
                LeaseAwareVaultPropertySource.this.handleLeaseEvent(leaseEvent, LeaseAwareVaultPropertySource.this.properties);
            }

            @Override
            public void onLeaseError(SecretLeaseEvent leaseEvent, Exception exception) {
                LeaseAwareVaultPropertySource.this.handleLeaseErrorEvent(leaseEvent, exception);
            }
        };
        this.loadProperties();
    }

    private void loadProperties() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Requesting secrets from Vault at %s using %s", new Object[]{this.requestedSecret.getPath(), this.requestedSecret.getMode()}));
        }
        this.secretLeaseContainer.addLeaseListener(this.leaseListener);
        this.secretLeaseContainer.addErrorListener(this.leaseListener);
        this.secretLeaseContainer.addRequestedSecret(this.requestedSecret);
        Exception loadError = this.loadError;
        if (this.notFound || loadError != null) {
            String msg = String.format("Vault location [%s] not resolvable", this.requestedSecret.getPath());
            if (this.ignoreSecretNotFound) {
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("%s: %s", msg, loadError != null ? loadError.getMessage() : "Not found"));
                }
            } else {
                if (loadError != null) {
                    throw new VaultPropertySourceNotFoundException(msg, loadError);
                }
                throw new VaultPropertySourceNotFoundException(msg);
            }
        }
    }

    public RequestedSecret getRequestedSecret() {
        return this.requestedSecret;
    }

    @Override
    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = this.properties.keySet();
        return strings.toArray(new String[strings.size()]);
    }

    protected void handleLeaseEvent(SecretLeaseEvent leaseEvent, Map<String, Object> properties) {
        if (leaseEvent.getSource() != this.getRequestedSecret()) {
            return;
        }
        if (leaseEvent instanceof SecretNotFoundEvent) {
            this.notFound = true;
        }
        if (leaseEvent instanceof SecretLeaseExpiredEvent || leaseEvent instanceof BeforeSecretLeaseRevocationEvent || leaseEvent instanceof SecretLeaseCreatedEvent) {
            properties.clear();
        }
        if (leaseEvent instanceof SecretLeaseCreatedEvent) {
            SecretLeaseCreatedEvent created = (SecretLeaseCreatedEvent)leaseEvent;
            Map<String, Object> secrets = this.doTransformProperties(this.flattenMap(created.getSecrets()));
            if (leaseEvent instanceof SecretLeaseRotatedEvent) {
                ArrayList<String> removedKeys = new ArrayList<String>(properties.keySet());
                removedKeys.removeAll(secrets.keySet());
                removedKeys.forEach(properties::remove);
            }
            properties.putAll(secrets);
        }
    }

    protected void handleLeaseErrorEvent(SecretLeaseEvent leaseEvent, Exception exception) {
        if (leaseEvent.getSource() != this.getRequestedSecret()) {
            return;
        }
        this.loadError = exception;
    }

    protected Map<String, Object> doTransformProperties(Map<String, Object> properties) {
        return this.propertyTransformer.transformProperties(properties);
    }

    @Deprecated
    protected Map<String, String> toStringMap(Map<String, Object> data) {
        return JsonMapFlattener.flattenToStringMap(data);
    }

    protected Map<String, Object> flattenMap(Map<String, Object> data) {
        return JsonMapFlattener.flatten(data);
    }
}

