/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

public interface EventSource
extends SessionImplementor {
    @Override
    public ActionQueue getActionQueue();

    @Override
    public Object instantiate(EntityPersister var1, Serializable var2) throws HibernateException;

    @Override
    public void forceFlush(EntityEntry var1) throws HibernateException;

    @Override
    public void merge(String var1, Object var2, Map var3) throws HibernateException;

    @Override
    public void persist(String var1, Object var2, Map var3) throws HibernateException;

    @Override
    public void persistOnFlush(String var1, Object var2, Map var3);

    @Override
    public void refresh(String var1, Object var2, Map var3) throws HibernateException;

    @Override
    public void delete(String var1, Object var2, boolean var3, Set var4);

    @Override
    public void removeOrphanBeforeUpdates(String var1, Object var2);
}

