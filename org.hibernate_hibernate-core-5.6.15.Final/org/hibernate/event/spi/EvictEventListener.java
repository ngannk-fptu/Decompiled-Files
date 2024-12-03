/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.EvictEvent;

public interface EvictEventListener
extends Serializable {
    public void onEvict(EvictEvent var1) throws HibernateException;
}

