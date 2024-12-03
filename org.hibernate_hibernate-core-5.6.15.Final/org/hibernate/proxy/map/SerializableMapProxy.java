/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.map;

import java.io.Serializable;
import org.hibernate.proxy.AbstractSerializableProxy;
import org.hibernate.proxy.map.MapLazyInitializer;
import org.hibernate.proxy.map.MapProxy;

public final class SerializableMapProxy
extends AbstractSerializableProxy {
    public SerializableMapProxy(String entityName, Serializable id, Boolean readOnly, String sessionFactoryUuid, boolean allowLoadOutsideTransaction) {
        super(entityName, id, readOnly, sessionFactoryUuid, allowLoadOutsideTransaction);
    }

    private Object readResolve() {
        MapLazyInitializer initializer = new MapLazyInitializer(this.getEntityName(), this.getId(), null);
        this.afterDeserialization(initializer);
        return new MapProxy(initializer);
    }
}

