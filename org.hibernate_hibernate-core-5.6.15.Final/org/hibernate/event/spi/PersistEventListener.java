/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;

public interface PersistEventListener
extends Serializable {
    public void onPersist(PersistEvent var1) throws HibernateException;

    public void onPersist(PersistEvent var1, Map var2) throws HibernateException;
}

