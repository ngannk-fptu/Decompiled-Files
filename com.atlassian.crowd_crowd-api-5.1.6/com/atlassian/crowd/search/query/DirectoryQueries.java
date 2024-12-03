/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.search.query;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;

public class DirectoryQueries {
    public static EntityQuery<Directory> allDirectories() {
        return QueryBuilder.queryFor(Directory.class, EntityDescriptor.directory()).returningAtMost(-1);
    }
}

