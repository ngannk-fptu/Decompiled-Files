/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.usertype;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public interface UserCollectionType {
    public PersistentCollection instantiate(SharedSessionContractImplementor var1, CollectionPersister var2) throws HibernateException;

    public PersistentCollection wrap(SharedSessionContractImplementor var1, Object var2);

    public Iterator getElementsIterator(Object var1);

    public boolean contains(Object var1, Object var2);

    public Object indexOf(Object var1, Object var2);

    public Object replaceElements(Object var1, Object var2, CollectionPersister var3, Object var4, Map var5, SharedSessionContractImplementor var6) throws HibernateException;

    public Object instantiate(int var1);
}

