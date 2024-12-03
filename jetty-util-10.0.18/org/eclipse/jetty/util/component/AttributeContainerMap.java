/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.thread.AutoLock;

public class AttributeContainerMap
extends ContainerLifeCycle
implements Attributes {
    private final AutoLock _lock = new AutoLock();
    private final Map<String, Object> _map = new HashMap<String, Object>();

    @Override
    public void setAttribute(String name, Object attribute) {
        try (AutoLock l = this._lock.lock();){
            Object old = this._map.put(name, attribute);
            this.updateBean(old, attribute);
        }
    }

    @Override
    public void removeAttribute(String name) {
        try (AutoLock l = this._lock.lock();){
            Object removed = this._map.remove(name);
            if (removed != null) {
                this.removeBean(removed);
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        try (AutoLock l = this._lock.lock();){
            Object object = this._map.get(name);
            return object;
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        try (AutoLock l = this._lock.lock();){
            Enumeration<String> enumeration = Collections.enumeration(this._map.keySet());
            return enumeration;
        }
    }

    @Override
    public Set<String> getAttributeNameSet() {
        try (AutoLock l = this._lock.lock();){
            Set<String> set = this._map.keySet();
            return set;
        }
    }

    @Override
    public void clearAttributes() {
        try (AutoLock l = this._lock.lock();){
            this._map.clear();
            this.removeBeans();
        }
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObject(out, this);
        Dumpable.dumpMapEntries(out, indent, this._map, true);
    }

    @Override
    public String toString() {
        return String.format("%s@%x{size=%d}", this.getClass().getSimpleName(), this.hashCode(), this._map.size());
    }
}

