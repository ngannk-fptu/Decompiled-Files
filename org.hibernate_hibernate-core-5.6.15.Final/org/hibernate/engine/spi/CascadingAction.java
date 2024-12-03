/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public interface CascadingAction {
    public void cascade(EventSource var1, Object var2, String var3, Object var4, boolean var5) throws HibernateException;

    public Iterator getCascadableChildrenIterator(EventSource var1, CollectionType var2, Object var3);

    public boolean deleteOrphans();

    public boolean requiresNoCascadeChecking();

    public void noCascade(EventSource var1, Object var2, EntityPersister var3, Type var4, int var5);

    public boolean performOnLazyProperty();
}

