/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.functions;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLMultiValueQueryFunction;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.cql.functions.Helpers.ParameterHelper;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentlyViewedContentTwoArgsQueryFunction
extends CQLMultiValueQueryFunction {
    private static final Logger log = LoggerFactory.getLogger(RecentlyViewedContentTwoArgsQueryFunction.class);
    private final RecentlyViewedDao recentlyViewedDao;
    private final UserAccessor userAccessor;

    public RecentlyViewedContentTwoArgsQueryFunction(RecentlyViewedDao recentlyViewedDao, @ComponentImport UserAccessor userAccessor) {
        super("recentlyViewedContent");
        this.recentlyViewedDao = recentlyViewedDao;
        this.userAccessor = userAccessor;
    }

    public int paramCount() {
        return 2;
    }

    public Iterable<String> invoke(List<String> list, CQLEvaluationContext cqlEvaluationContext) {
        Option currentUser = cqlEvaluationContext.getCurrentUser();
        if (!currentUser.isDefined()) {
            log.debug("Returning empty result for anonymous user.");
            return Collections.emptyList();
        }
        String username = (String)currentUser.get();
        UserKey userKey = this.userAccessor.getUserByName(username).getKey();
        int pageSizeLimit = ParameterHelper.getIntegerParameter(list.get(0), "size limit");
        int rowOffset = ParameterHelper.getIntegerParameter(list.get(1), "offset");
        List<String> listOfIds = this.recentlyViewedDao.findRecentlyViewed(userKey, pageSizeLimit, rowOffset).stream().map(RecentlyViewed::getId).map(String::valueOf).collect(Collectors.toList());
        log.debug("Recently viewed content IDs for user {} is {} ({} items)", new Object[]{username, listOfIds, listOfIds.size()});
        return listOfIds;
    }
}

