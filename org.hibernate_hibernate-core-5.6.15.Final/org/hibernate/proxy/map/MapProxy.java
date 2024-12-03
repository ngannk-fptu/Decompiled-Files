/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.proxy.map.MapLazyInitializer;
import org.hibernate.proxy.map.SerializableMapProxy;

public class MapProxy
implements HibernateProxy,
Map,
Serializable {
    private MapLazyInitializer li;
    private Object replacement;

    MapProxy(MapLazyInitializer li) {
        this.li = li;
    }

    @Override
    public LazyInitializer getHibernateLazyInitializer() {
        return this.li;
    }

    @Override
    public int size() {
        return this.li.getMap().size();
    }

    @Override
    public void clear() {
        this.li.getMap().clear();
    }

    @Override
    public boolean isEmpty() {
        return this.li.getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.li.getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.li.getMap().containsValue(value);
    }

    public Collection values() {
        return this.li.getMap().values();
    }

    public void putAll(Map t) {
        this.li.getMap().putAll(t);
    }

    public Set entrySet() {
        return this.li.getMap().entrySet();
    }

    public Set keySet() {
        return this.li.getMap().keySet();
    }

    public Object get(Object key) {
        return this.li.getMap().get(key);
    }

    public Object remove(Object key) {
        return this.li.getMap().remove(key);
    }

    public Object put(Object key, Object value) {
        return this.li.getMap().put(key, value);
    }

    @Override
    public Object writeReplace() {
        this.li.initializeWithoutLoadIfPossible();
        if (this.li.isUninitialized()) {
            if (this.replacement == null) {
                this.li.prepareForPossibleLoadingOutsideTransaction();
                this.replacement = this.serializableProxy();
            }
            return this.replacement;
        }
        return this.li.getImplementation();
    }

    private Object serializableProxy() {
        return new SerializableMapProxy(this.li.getEntityName(), this.li.getInternalIdentifier(), this.li.isReadOnlySettingAvailable() ? Boolean.valueOf(this.li.isReadOnly()) : this.li.isReadOnlyBeforeAttachedToSession(), this.li.getSessionFactoryUuid(), this.li.isAllowLoadOutsideTransaction());
    }
}

