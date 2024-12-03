/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.admin.AbstractDelegatedDirectoryFinder;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.stream.Collectors;

public class JiraDelegatedDirectoryFinder
extends AbstractDelegatedDirectoryFinder {
    public JiraDelegatedDirectoryFinder(DirectoryManager directoryManager) {
        super(directoryManager);
    }

    @Override
    protected EntityQuery<Directory> directoryEntityQuery() {
        return QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).returningAtMost(-1);
    }

    @Override
    protected List<Directory> filter(List<Directory> directories) {
        return directories.stream().filter((? super T directory) -> directory.getType().equals((Object)DirectoryType.DELEGATING)).collect(Collectors.toList());
    }
}

