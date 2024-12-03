/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.FlushEntityEvent;

public interface FlushEntityEventListener
extends Serializable {
    public void onFlushEntity(FlushEntityEvent var1) throws HibernateException;
}

