/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.query.entity;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.entity.EntityQuery;

public class DirectoryQuery
extends EntityQuery<Directory> {
    public DirectoryQuery(SearchRestriction searchRestriction, int startIndex, int maxResults) {
        super(Directory.class, EntityDescriptor.directory(), searchRestriction, startIndex, maxResults);
    }
}

