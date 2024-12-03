/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.SiteSearchPermissionsQuery;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;

@Internal
public class SiteSearchPermissionsFilteredQueryFactory
implements SiteSearchPermissionsQueryFactory {
    @Override
    public SearchQuery create() {
        return new SiteSearchPermissionsQuery();
    }
}

