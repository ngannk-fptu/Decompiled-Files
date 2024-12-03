/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.StashComponent
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.StashComponent;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.AbstractJitDirectoriesFinder;
import java.util.List;
import javax.inject.Inject;

@BitbucketComponent
@StashComponent
@ConfluenceComponent
@BambooComponent
@FecruComponent
@RefappComponent
public class DirectoryQueryJitDirectoriesFinder
extends AbstractJitDirectoriesFinder {
    private final DirectoryManager directoryManager;

    @Inject
    public DirectoryQueryJitDirectoriesFinder(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    protected List<Directory> executeDirectoriesQuery() {
        return this.directoryManager.searchDirectories(this.activeInternalDirectoriesQuery());
    }

    private EntityQuery<Directory> activeInternalDirectoriesQuery() {
        return QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).with((SearchRestriction)Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)DirectoryTermKeys.ACTIVE).exactlyMatching((Object)true), Restriction.on((Property)DirectoryTermKeys.TYPE).exactlyMatching((Object)DirectoryType.INTERNAL)})).returningAtMost(-1);
    }
}

