/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.admin.DelegatedDirectoryFinder;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;

public abstract class AbstractDelegatedDirectoryFinder
implements DelegatedDirectoryFinder {
    private final DirectoryManager directoryManager;

    protected AbstractDelegatedDirectoryFinder(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    public List<Directory> findDirectories() {
        List delegatedDirectories = this.directoryManager.searchDirectories(this.directoryEntityQuery());
        return this.filter(delegatedDirectories);
    }

    protected abstract EntityQuery<Directory> directoryEntityQuery();

    protected List<Directory> filter(List<Directory> directories) {
        return directories;
    }
}

