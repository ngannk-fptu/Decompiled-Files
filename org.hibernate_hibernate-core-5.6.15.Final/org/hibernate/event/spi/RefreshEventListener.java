/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.RefreshEvent;

public interface RefreshEventListener
extends Serializable {
    public void onRefresh(RefreshEvent var1) throws HibernateException;

    public void onRefresh(RefreshEvent var1, Map var2) throws HibernateException;
}

