/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.LockEvent;

public interface LockEventListener
extends Serializable {
    public void onLock(LockEvent var1) throws HibernateException;
}

