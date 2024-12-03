/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import java.io.Serializable;
import org.hibernate.proxy.AbstractLazyInitializer;

public abstract class AbstractSerializableProxy
implements Serializable {
    private String entityName;
    private Serializable id;
    private Boolean readOnly;
    private String sessionFactoryUuid;
    private boolean allowLoadOutsideTransaction;

    @Deprecated
    protected AbstractSerializableProxy() {
    }

    @Deprecated
    protected AbstractSerializableProxy(String entityName, Serializable id, Boolean readOnly) {
        this(entityName, id, readOnly, null, false);
    }

    protected AbstractSerializableProxy(String entityName, Serializable id, Boolean readOnly, String sessionFactoryUuid, boolean allowLoadOutsideTransaction) {
        this.entityName = entityName;
        this.id = id;
        this.readOnly = readOnly;
        this.sessionFactoryUuid = sessionFactoryUuid;
        this.allowLoadOutsideTransaction = allowLoadOutsideTransaction;
    }

    protected String getEntityName() {
        return this.entityName;
    }

    protected Serializable getId() {
        return this.id;
    }

    @Deprecated
    protected void setReadOnlyBeforeAttachedToSession(AbstractLazyInitializer li) {
        li.afterDeserialization(this.readOnly, null, false);
    }

    protected void afterDeserialization(AbstractLazyInitializer li) {
        li.afterDeserialization(this.readOnly, this.sessionFactoryUuid, this.allowLoadOutsideTransaction);
    }
}

