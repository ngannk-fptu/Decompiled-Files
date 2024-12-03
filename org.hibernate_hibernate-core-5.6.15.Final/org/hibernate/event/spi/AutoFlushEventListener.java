/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.AutoFlushEvent;

public interface AutoFlushEventListener
extends Serializable {
    public void onAutoFlush(AutoFlushEvent var1) throws HibernateException;
}

