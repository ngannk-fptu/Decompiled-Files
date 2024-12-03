/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.search.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.List;

@Internal
public class SiteSearchPermissionsQuery
implements SearchQuery {
    public static final String KEY = "siteSearchPermissions";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.emptyList();
    }
}

