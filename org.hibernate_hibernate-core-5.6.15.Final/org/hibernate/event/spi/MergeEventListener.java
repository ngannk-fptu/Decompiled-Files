/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.MergeEvent;

public interface MergeEventListener
extends Serializable {
    public void onMerge(MergeEvent var1) throws HibernateException;

    public void onMerge(MergeEvent var1, Map var2) throws HibernateException;
}

