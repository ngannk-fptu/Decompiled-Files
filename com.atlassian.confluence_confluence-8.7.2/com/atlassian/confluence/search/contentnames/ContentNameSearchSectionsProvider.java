/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSection;
import com.atlassian.confluence.search.contentnames.QueryToken;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContentNameSearchSectionsProvider {
    public @Nullable Collection<ContentNameSearchSection> getSections(List<QueryToken> var1, ContentNameSearchContext var2);
}

