/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import java.io.Serializable;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface UniqueEntityLoader {
    @Deprecated
    public Object load(Serializable var1, Object var2, SharedSessionContractImplementor var3);

    public Object load(Serializable var1, Object var2, SharedSessionContractImplementor var3, LockOptions var4);

    default public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, Boolean readOnly) {
        return this.load(id, optionalObject, session);
    }

    default public Object load(Serializable id, Object optionalObject, SharedSessionContractImplementor session, LockOptions lockOptions, Boolean readOnly) {
        return this.load(id, optionalObject, session, lockOptions);
    }

    default public Object load(Object id, SharedSessionContractImplementor session, LockOptions lockOptions) {
        throw new UnsupportedOperationException();
    }
}

