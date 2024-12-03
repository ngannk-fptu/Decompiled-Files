/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.functions;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction;
import com.atlassian.confluence.plugins.recentlyviewed.cql.functions.Helpers.ParameterHelper;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.List;

public class RecentlyViewedSpacesQueryFunction
extends CQLMultiValueQueryFunction {
    private final RecentlyViewedDao recentlyViewedDao;
    private final UserAccessor userAccessor;

    public RecentlyViewedSpacesQueryFunction(RecentlyViewedDao recentlyViewedDao, @ComponentImport UserAccessor userAccessor) {
        super("recentlyViewedSpaces");
        this.recentlyViewedDao = recentlyViewedDao;
        this.userAccessor = userAccessor;
    }

    public int paramCount() {
        return 1;
    }

    public Iterable<String> invoke(List<String> list, CQLEvaluationContext cqlEvaluationContext) {
        Option currentUser = cqlEvaluationContext.getCurrentUser();
        if (!currentUser.isDefined()) {
            return new ArrayList<String>();
        }
        String userKey = this.userAccessor.getUserByName((String)currentUser.get()).getKey().getStringValue();
        int pageSizeLimit = ParameterHelper.getIntegerParameter(list.get(0), "size limit");
        return this.recentlyViewedDao.findRecentlyViewedSpaceKeys(userKey, pageSizeLimit);
    }
}

