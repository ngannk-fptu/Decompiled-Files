/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.LockOptions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.Loadable;

public interface UniqueKeyLoadable
extends Loadable {
    public Object loadByUniqueKey(String var1, Object var2, SharedSessionContractImplementor var3);

    public Object loadByNaturalId(Object[] var1, LockOptions var2, SharedSessionContractImplementor var3);

    public int getPropertyIndex(String var1);
}

