/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentEx;
import com.sun.xml.ws.api.ComponentRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Container
implements ComponentRegistry,
ComponentEx {
    private final Set<Component> components = new CopyOnWriteArraySet<Component>();
    public static final Container NONE = new NoneContainer();

    protected Container() {
    }

    @Override
    public <S> S getSPI(Class<S> spiType) {
        if (this.components == null) {
            return null;
        }
        for (Component c : this.components) {
            S s = c.getSPI(spiType);
            if (s == null) continue;
            return s;
        }
        return null;
    }

    @Override
    public Set<Component> getComponents() {
        return this.components;
    }

    @NotNull
    public <E> Iterable<E> getIterableSPI(Class<E> spiType) {
        E item = this.getSPI(spiType);
        if (item != null) {
            List<E> c = Collections.singletonList(item);
            return c;
        }
        return Collections.emptySet();
    }

    private static final class NoneContainer
    extends Container {
        private NoneContainer() {
        }
    }
}

