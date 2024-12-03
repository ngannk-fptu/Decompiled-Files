/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public interface DirectoryDao {
    public Directory findById(long var1) throws DirectoryNotFoundException;

    public Directory findByName(String var1) throws DirectoryNotFoundException;

    @Deprecated
    public List<Directory> findAll();

    public Directory add(Directory var1);

    public Directory update(Directory var1) throws DirectoryNotFoundException;

    public void remove(Directory var1) throws DirectoryNotFoundException;

    public List<Directory> search(EntityQuery<Directory> var1);
}

