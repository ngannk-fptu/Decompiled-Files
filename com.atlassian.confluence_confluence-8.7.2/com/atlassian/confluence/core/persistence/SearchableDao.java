/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.core.persistence;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import java.util.List;
import java.util.Optional;

public interface SearchableDao {
    public List<List<HibernateHandle>> getLatestSearchableHandlesGroupedByType();

    public List<List<HibernateHandle>> getLatestSearchableHandlesGroupedByType(Optional<String> var1);

    public List<HibernateHandle> getLatestSearchableHandles(Class<? extends Searchable> var1);

    public List<HibernateHandle> getLatestSearchableHandles(Class<? extends Searchable> var1, Optional<String> var2);

    public int getCountOfLatestSearchables();

    public int getCountOfLatestSearchables(String var1);

    public int getCountOfLatestSearchables(Class<? extends Searchable> var1);

    public int getCountOfLatestSearchables(String var1, Class<? extends Searchable> var2);
}

