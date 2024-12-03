/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 */
package com.atlassian.confluence.core.persistence;

import com.atlassian.bonnie.Handle;
import java.util.List;

@Deprecated
public interface AnyTypeDao {
    public Object findByHandle(Handle var1);

    public Object getByIdAndType(long var1, Class var3);

    public List findByIdsAndClassName(List<Long> var1, String var2);

    @Deprecated
    public List<Handle> findAllPersistentObjectsHandles();

    @Deprecated
    public List findAllPersistentObjects();

    @Deprecated
    public <T> int removeAllPersistentObjectsByType(Class<T> var1);
}

