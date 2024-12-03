/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.SaveOrUpdateEvent;

public interface SaveOrUpdateEventListener
extends Serializable {
    public void onSaveOrUpdate(SaveOrUpdateEvent var1) throws HibernateException;
}

