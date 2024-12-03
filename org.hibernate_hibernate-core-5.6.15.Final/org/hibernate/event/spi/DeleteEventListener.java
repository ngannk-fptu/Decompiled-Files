/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.DeleteEvent;

public interface DeleteEventListener
extends Serializable {
    public void onDelete(DeleteEvent var1) throws HibernateException;

    public void onDelete(DeleteEvent var1, Set var2) throws HibernateException;
}

