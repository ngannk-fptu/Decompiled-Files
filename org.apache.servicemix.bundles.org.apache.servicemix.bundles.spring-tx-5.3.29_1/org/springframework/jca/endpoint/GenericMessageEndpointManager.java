/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.ResourceException
 *  javax.resource.spi.ActivationSpec
 *  javax.resource.spi.ResourceAdapter
 *  javax.resource.spi.endpoint.MessageEndpointFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jca.endpoint;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class GenericMessageEndpointManager
implements SmartLifecycle,
InitializingBean,
DisposableBean {
    @Nullable
    private ResourceAdapter resourceAdapter;
    @Nullable
    private MessageEndpointFactory messageEndpointFactory;
    @Nullable
    private ActivationSpec activationSpec;
    private boolean autoStartup = true;
    private int phase = Integer.MAX_VALUE;
    private volatile boolean running;
    private final Object lifecycleMonitor = new Object();

    public void setResourceAdapter(@Nullable ResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }

    @Nullable
    public ResourceAdapter getResourceAdapter() {
        return this.resourceAdapter;
    }

    public void setMessageEndpointFactory(@Nullable MessageEndpointFactory messageEndpointFactory) {
        this.messageEndpointFactory = messageEndpointFactory;
    }

    @Nullable
    public MessageEndpointFactory getMessageEndpointFactory() {
        return this.messageEndpointFactory;
    }

    public void setActivationSpec(@Nullable ActivationSpec activationSpec) {
        this.activationSpec = activationSpec;
    }

    @Nullable
    public ActivationSpec getActivationSpec() {
        return this.activationSpec;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPhase() {
        return this.phase;
    }

    public void afterPropertiesSet() throws ResourceException {
        if (this.getResourceAdapter() == null) {
            throw new IllegalArgumentException("Property 'resourceAdapter' is required");
        }
        if (this.getMessageEndpointFactory() == null) {
            throw new IllegalArgumentException("Property 'messageEndpointFactory' is required");
        }
        ActivationSpec activationSpec = this.getActivationSpec();
        if (activationSpec == null) {
            throw new IllegalArgumentException("Property 'activationSpec' is required");
        }
        if (activationSpec.getResourceAdapter() == null) {
            activationSpec.setResourceAdapter(this.getResourceAdapter());
        } else if (activationSpec.getResourceAdapter() != this.getResourceAdapter()) {
            throw new IllegalArgumentException("ActivationSpec [" + activationSpec + "] is associated with a different ResourceAdapter: " + activationSpec.getResourceAdapter());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            if (!this.running) {
                ResourceAdapter resourceAdapter = this.getResourceAdapter();
                Assert.state((resourceAdapter != null ? 1 : 0) != 0, (String)"No ResourceAdapter set");
                try {
                    resourceAdapter.endpointActivation(this.getMessageEndpointFactory(), this.getActivationSpec());
                }
                catch (ResourceException ex) {
                    throw new IllegalStateException("Could not activate message endpoint", ex);
                }
                this.running = true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop() {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            if (this.running) {
                ResourceAdapter resourceAdapter = this.getResourceAdapter();
                Assert.state((resourceAdapter != null ? 1 : 0) != 0, (String)"No ResourceAdapter set");
                resourceAdapter.endpointDeactivation(this.getMessageEndpointFactory(), this.getActivationSpec());
                this.running = false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(Runnable callback) {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.stop();
            callback.run();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void destroy() {
        this.stop();
    }
}

