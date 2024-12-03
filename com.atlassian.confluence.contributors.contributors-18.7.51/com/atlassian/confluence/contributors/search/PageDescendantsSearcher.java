/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.contributors.search.Doc;
import java.util.Set;

@Internal
public interface PageDescendantsSearcher {
    public Iterable<Doc> getDirectChildren(Set<Long> var1);

    public Iterable<Doc> getDescendants(Set<Long> var1);
}

