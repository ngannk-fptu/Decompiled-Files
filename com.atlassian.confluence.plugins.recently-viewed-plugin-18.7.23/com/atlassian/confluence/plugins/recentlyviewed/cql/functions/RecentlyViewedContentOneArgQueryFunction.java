/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.functions;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction;
import com.atlassian.confluence.plugins.recentlyviewed.cql.functions.RecentlyViewedContentTwoArgsQueryFunction;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class RecentlyViewedContentOneArgQueryFunction
extends CQLMultiValueQueryFunction {
    private final RecentlyViewedContentTwoArgsQueryFunction delegate;

    public RecentlyViewedContentOneArgQueryFunction(RecentlyViewedDao recentlyViewedDao, @ComponentImport UserAccessor userAccessor) {
        super("recentlyViewedContent");
        this.delegate = new RecentlyViewedContentTwoArgsQueryFunction(recentlyViewedDao, userAccessor);
    }

    public Iterable<String> invoke(List<String> list, CQLEvaluationContext cqlEvaluationContext) {
        ImmutableList params = ImmutableList.of((Object)list.get(0), (Object)"0");
        return this.delegate.invoke((List<String>)params, cqlEvaluationContext);
    }

    public int paramCount() {
        return 1;
    }
}

