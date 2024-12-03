/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.ConstructionContext;
import com.google.inject.internal.util.$Maps;
import com.google.inject.spi.Dependency;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InternalContext {
    private Map<Object, ConstructionContext<?>> constructionContexts = $Maps.newHashMap();
    private Dependency dependency;

    InternalContext() {
    }

    public <T> ConstructionContext<T> getConstructionContext(Object key) {
        ConstructionContext<Object> constructionContext = this.constructionContexts.get(key);
        if (constructionContext == null) {
            constructionContext = new ConstructionContext();
            this.constructionContexts.put(key, constructionContext);
        }
        return constructionContext;
    }

    public Dependency getDependency() {
        return this.dependency;
    }

    public Dependency setDependency(Dependency dependency) {
        Dependency previous = this.dependency;
        this.dependency = dependency;
        return previous;
    }
}

