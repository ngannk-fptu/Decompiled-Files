/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.server;

import java.util.HashSet;
import java.util.Set;

public enum LazyMOMProvider {
    INSTANCE;

    private final Set<WSEndpointScopeChangeListener> endpointsWaitingForMOM = new HashSet<WSEndpointScopeChangeListener>();
    private final Set<DefaultScopeChangeListener> listeners = new HashSet<DefaultScopeChangeListener>();
    private volatile Scope scope = Scope.STANDALONE;

    public void initMOMForScope(Scope scope) {
        if (this.scope == Scope.GLASSFISH_JMX || scope == Scope.STANDALONE && (this.scope == Scope.GLASSFISH_JMX || this.scope == Scope.GLASSFISH_NO_JMX) || this.scope == scope) {
            return;
        }
        this.scope = scope;
        this.fireScopeChanged();
    }

    private void fireScopeChanged() {
        for (ScopeChangeListener scopeChangeListener : this.endpointsWaitingForMOM) {
            scopeChangeListener.scopeChanged(this.scope);
        }
        for (ScopeChangeListener scopeChangeListener : this.listeners) {
            scopeChangeListener.scopeChanged(this.scope);
        }
    }

    public void registerListener(DefaultScopeChangeListener listener) {
        this.listeners.add(listener);
        if (!this.isProviderInDefaultScope()) {
            listener.scopeChanged(this.scope);
        }
    }

    private boolean isProviderInDefaultScope() {
        return this.scope == Scope.STANDALONE;
    }

    public Scope getScope() {
        return this.scope;
    }

    public void registerEndpoint(WSEndpointScopeChangeListener wsEndpoint) {
        this.endpointsWaitingForMOM.add(wsEndpoint);
        if (!this.isProviderInDefaultScope()) {
            wsEndpoint.scopeChanged(this.scope);
        }
    }

    public void unregisterEndpoint(WSEndpointScopeChangeListener wsEndpoint) {
        this.endpointsWaitingForMOM.remove(wsEndpoint);
    }

    public static interface WSEndpointScopeChangeListener
    extends ScopeChangeListener {
    }

    public static interface DefaultScopeChangeListener
    extends ScopeChangeListener {
    }

    public static interface ScopeChangeListener {
        public void scopeChanged(Scope var1);
    }

    public static enum Scope {
        STANDALONE,
        GLASSFISH_NO_JMX,
        GLASSFISH_JMX;

    }
}

