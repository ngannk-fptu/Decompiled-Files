/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.admin.AbstractDelegatedDirectoryFinder;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;

public class DefaultDelegatedDirectoryFinder
extends AbstractDelegatedDirectoryFinder {
    public DefaultDelegatedDirectoryFinder(DirectoryManager directoryManager) {
        super(directoryManager);
    }

    @Override
    protected EntityQuery<Directory> directoryEntityQuery() {
        return QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).with((SearchRestriction)Restriction.on((Property)DirectoryTermKeys.TYPE).exactlyMatching((Object)DirectoryType.DELEGATING)).returningAtMost(-1);
    }
}

