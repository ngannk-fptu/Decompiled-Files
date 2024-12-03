/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.ResolveNaturalIdEvent;

public interface ResolveNaturalIdEventListener
extends Serializable {
    public void onResolveNaturalId(ResolveNaturalIdEvent var1) throws HibernateException;
}

