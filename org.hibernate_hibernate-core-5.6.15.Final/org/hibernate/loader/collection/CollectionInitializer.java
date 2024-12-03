/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.collection;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface CollectionInitializer {
    public void initialize(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;
}

