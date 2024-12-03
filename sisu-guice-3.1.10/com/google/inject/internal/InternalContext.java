/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.internal.ConstructionContext;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.DependencyAndSource;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InternalContext {
    private Map<Object, ConstructionContext<?>> constructionContexts = Maps.newHashMap();
    private Dependency<?> dependency;
    private final List<Object> state = Lists.newArrayList();

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

    public Dependency<?> getDependency() {
        return this.dependency;
    }

    public Dependency<?> pushDependency(Dependency<?> dependency, Object source) {
        Dependency<?> previous = this.dependency;
        this.dependency = dependency;
        this.state.add(dependency);
        this.state.add(source);
        return previous;
    }

    public void popStateAndSetDependency(Dependency<?> newDependency) {
        this.popState();
        this.dependency = newDependency;
    }

    public void pushState(Key<?> key, Object source) {
        this.state.add(key == null ? null : Dependency.get(key));
        this.state.add(source);
    }

    public void popState() {
        this.state.remove(this.state.size() - 1);
        this.state.remove(this.state.size() - 1);
    }

    public List<DependencyAndSource> getDependencyChain() {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < this.state.size(); i += 2) {
            builder.add((Object)new DependencyAndSource((Dependency)this.state.get(i), this.state.get(i + 1)));
        }
        return builder.build();
    }
}

