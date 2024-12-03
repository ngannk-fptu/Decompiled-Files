/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.spi;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.Loadable;

public interface AfterLoadAction {
    public void afterLoad(SharedSessionContractImplementor var1, Object var2, Loadable var3);
}

