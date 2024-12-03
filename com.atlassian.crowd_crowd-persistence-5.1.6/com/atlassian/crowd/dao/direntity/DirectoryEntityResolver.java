/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.DirectoryEntity
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.dao.direntity;

import com.atlassian.crowd.model.DirectoryEntity;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public interface DirectoryEntityResolver {
    @Nullable
    public <T extends DirectoryEntity> T resolve(long var1, String var3, Class<T> var4);

    public <T extends DirectoryEntity> void put(T var1);

    @Nullable
    public <T extends DirectoryEntity> List<T> resolveAllOrNothing(long var1, Collection<String> var3, Class<T> var4);

    public void putAll(List<? extends DirectoryEntity> var1);
}

