/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.ConstructionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerImpl;
import com.opensymphony.xwork2.inject.ExternalContext;
import com.opensymphony.xwork2.inject.Scope;
import java.util.HashMap;
import java.util.Map;

class InternalContext {
    final ContainerImpl container;
    final Map<Object, ConstructionContext<?>> constructionContexts = new HashMap();
    Scope.Strategy scopeStrategy;
    ExternalContext<?> externalContext;

    InternalContext(ContainerImpl container) {
        this.container = container;
    }

    public Container getContainer() {
        return this.container;
    }

    ContainerImpl getContainerImpl() {
        return this.container;
    }

    Scope.Strategy getScopeStrategy() {
        if (this.scopeStrategy == null) {
            this.scopeStrategy = (Scope.Strategy)this.container.localScopeStrategy.get();
            if (this.scopeStrategy == null) {
                throw new IllegalStateException("Scope strategy not set. Please call Container.setScopeStrategy().");
            }
        }
        return this.scopeStrategy;
    }

    <T> ConstructionContext<T> getConstructionContext(Object key) {
        ConstructionContext<Object> constructionContext = this.constructionContexts.get(key);
        if (constructionContext == null) {
            constructionContext = new ConstructionContext();
            this.constructionContexts.put(key, constructionContext);
        }
        return constructionContext;
    }

    <T> ExternalContext<T> getExternalContext() {
        return this.externalContext;
    }

    void setExternalContext(ExternalContext<?> externalContext) {
        this.externalContext = externalContext;
    }
}

