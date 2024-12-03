/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.FlushEvent;

public interface FlushEventListener
extends Serializable {
    public void onFlush(FlushEvent var1) throws HibernateException;
}

