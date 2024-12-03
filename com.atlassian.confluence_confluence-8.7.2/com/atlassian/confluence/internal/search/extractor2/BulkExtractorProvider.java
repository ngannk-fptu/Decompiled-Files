/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.extractor2;

import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.Collection;

public interface BulkExtractorProvider {
    public Collection<BulkExtractor<?>> findBulkExtractors(SearchIndex var1);
}

