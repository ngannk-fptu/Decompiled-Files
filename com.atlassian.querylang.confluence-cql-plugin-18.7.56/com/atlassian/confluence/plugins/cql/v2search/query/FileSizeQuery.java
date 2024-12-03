/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.Range
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.LongRangeQuery
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.cql.v2search.query;

import com.atlassian.confluence.plugins.cql.v2search.query.AbstractRangeQuery;
import com.atlassian.confluence.search.v2.Range;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.LongRangeQuery;
import javax.annotation.Nonnull;

public class FileSizeQuery
extends AbstractRangeQuery<Long> {
    public FileSizeQuery(@Nonnull Long from, @Nonnull Long to, boolean includeFrom, boolean includeTo) {
        super("fileSize", from, to, includeFrom, includeTo);
    }

    public SearchQuery expand() {
        return new LongRangeQuery("filesize", new Range((Object)((Long)this.getFrom()), (Object)((Long)this.getTo()), this.isIncludeFrom(), this.isIncludeTo()));
    }
}

