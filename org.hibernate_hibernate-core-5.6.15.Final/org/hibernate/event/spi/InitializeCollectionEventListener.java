/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.InitializeCollectionEvent;

public interface InitializeCollectionEventListener
extends Serializable {
    public void onInitializeCollection(InitializeCollectionEvent var1) throws HibernateException;
}

