/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface SessionAssociableInterceptor
extends PersistentAttributeInterceptor {
    public SharedSessionContractImplementor getLinkedSession();

    public void setSession(SharedSessionContractImplementor var1);

    public void unsetSession();

    public boolean allowLoadOutsideTransaction();

    public String getSessionFactoryUuid();
}

