/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.SmartLifecycle
 *  org.springframework.web.util.UriComponentsBuilder
 */
package org.springframework.web.socket.client;

import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class ConnectionManagerSupport
implements SmartLifecycle {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final URI uri;
    private boolean autoStartup = false;
    private int phase = Integer.MAX_VALUE;
    private volatile boolean running;
    private final Object lifecycleMonitor = new Object();

    public ConnectionManagerSupport(String uriTemplate, Object ... uriVariables) {
        this.uri = UriComponentsBuilder.fromUriString((String)uriTemplate).buildAndExpand(uriVariables).encode().toUri();
    }

    protected URI getUri() {
        return this.uri;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start() {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            if (!this.isRunning()) {
                this.startInternal();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void startInternal() {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Starting " + this.getClass().getSimpleName()));
            }
            this.running = true;
            this.openConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stop() {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            if (this.isRunning()) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info((Object)("Stopping " + this.getClass().getSimpleName()));
                }
                try {
                    this.stopInternal();
                }
                catch (Throwable ex) {
                    this.logger.error((Object)"Failed to stop WebSocket connection", ex);
                }
                finally {
                    this.running = false;
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stop(Runnable callback) {
        Object object = this.lifecycleMonitor;
        synchronized (object) {
            this.stop();
            callback.run();
        }
    }

    protected void stopInternal() throws Exception {
        if (this.isConnected()) {
            this.closeConnection();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public abstract boolean isConnected();

    protected abstract void openConnection();

    protected abstract void closeConnection() throws Exception;
}

