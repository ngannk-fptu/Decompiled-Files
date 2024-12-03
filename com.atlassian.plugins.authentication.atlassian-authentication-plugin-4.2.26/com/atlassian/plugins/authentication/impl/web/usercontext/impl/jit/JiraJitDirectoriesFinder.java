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
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.AbstractJitDirectoriesFinder;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

@JiraComponent
public class JiraJitDirectoriesFinder
extends AbstractJitDirectoriesFinder {
    private final DirectoryManager directoryManager;

    @Inject
    public JiraJitDirectoriesFinder(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    protected List<Directory> executeDirectoriesQuery() {
        return this.directoryManager.searchDirectories(JiraJitDirectoriesFinder.allDirectoriesQuery()).stream().filter(d -> d.getType().equals((Object)DirectoryType.INTERNAL)).filter(Directory::isActive).collect(Collectors.toList());
    }

    static EntityQuery<Directory> allDirectoriesQuery() {
        return QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).returningAtMost(-1);
    }
}

