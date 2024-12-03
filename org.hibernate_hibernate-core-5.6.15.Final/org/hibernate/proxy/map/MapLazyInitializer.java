/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.map;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.AbstractLazyInitializer;

public class MapLazyInitializer
extends AbstractLazyInitializer
implements Serializable {
    MapLazyInitializer(String entityName, Serializable id, SharedSessionContractImplementor session) {
        super(entityName, id, session);
    }

    public Map getMap() {
        return (Map)this.getImplementation();
    }

    @Override
    public Class getPersistentClass() {
        throw new UnsupportedOperationException("dynamic-map entity representation");
    }

    @Override
    protected void prepareForPossibleLoadingOutsideTransaction() {
        super.prepareForPossibleLoadingOutsideTransaction();
    }

    @Override
    protected boolean isAllowLoadOutsideTransaction() {
        return super.isAllowLoadOutsideTransaction();
    }

    @Override
    protected String getSessionFactoryUuid() {
        return super.getSessionFactoryUuid();
    }
}

