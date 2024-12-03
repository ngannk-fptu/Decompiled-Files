/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import org.hibernate.FetchMode;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.PropertyMapping;

public interface QueryableCollection
extends PropertyMapping,
Joinable,
CollectionPersister {
    public String selectFragment(String var1, String var2);

    public String[] getIndexColumnNames();

    public String[] getIndexFormulas();

    public String[] getIndexColumnNames(String var1);

    public String[] getElementColumnNames(String var1);

    public String[] getElementColumnNames();

    public String getSQLOrderByString(String var1);

    public String getManyToManyOrderByString(String var1);

    public boolean hasWhere();

    public EntityPersister getElementPersister();

    public FetchMode getFetchMode();
}

