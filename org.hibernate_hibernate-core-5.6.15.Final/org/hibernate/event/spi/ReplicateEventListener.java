/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.ReplicateEvent;

public interface ReplicateEventListener
extends Serializable {
    public void onReplicate(ReplicateEvent var1) throws HibernateException;
}

