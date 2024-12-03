/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.internal.search.extractor2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.List;

@FunctionalInterface
@Internal
public interface Extractor2Provider {
    public List<Extractor2> get(SearchIndex var1, boolean var2);
}

