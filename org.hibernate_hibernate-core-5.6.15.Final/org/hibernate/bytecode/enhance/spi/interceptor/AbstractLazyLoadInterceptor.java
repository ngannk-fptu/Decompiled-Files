/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.bytecode.enhance.spi.interceptor.AbstractInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class AbstractLazyLoadInterceptor
extends AbstractInterceptor
implements BytecodeLazyAttributeInterceptor {
    public AbstractLazyLoadInterceptor(String entityName) {
        super(entityName);
    }

    public AbstractLazyLoadInterceptor(String entityName, SharedSessionContractImplementor session) {
        super(entityName);
        this.setSession(session);
    }
}

