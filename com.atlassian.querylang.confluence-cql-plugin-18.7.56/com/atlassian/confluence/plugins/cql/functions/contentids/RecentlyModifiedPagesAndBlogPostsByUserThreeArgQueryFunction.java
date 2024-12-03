/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.setup.settings.CollaborativeEditingHelper
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.exceptions.GenericQueryException
 *  com.atlassian.querylang.functions.EvaluationContext
 *  com.atlassian.querylang.functions.MultiValueQueryFunction
 *  com.atlassian.querylang.literals.StringLiteralHelper
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.cql.functions.contentids;

import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.exceptions.GenericQueryException;
import com.atlassian.querylang.functions.EvaluationContext;
import com.atlassian.querylang.functions.MultiValueQueryFunction;
import com.atlassian.querylang.literals.StringLiteralHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class RecentlyModifiedPagesAndBlogPostsByUserThreeArgQueryFunction
extends MultiValueQueryFunction<EvaluationContext> {
    protected static final int MAX_LIMIT = 200;
    private static final Logger log = LoggerFactory.getLogger(RecentlyModifiedPagesAndBlogPostsByUserThreeArgQueryFunction.class);
    private final ContentEntityManager contentEntityManager;
    private final UserAccessor userAccessor;
    private final CollaborativeEditingHelper collaborativeEditingHelper;

    public RecentlyModifiedPagesAndBlogPostsByUserThreeArgQueryFunction(@ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport UserAccessor userAccessor, @ComponentImport CollaborativeEditingHelper collaborativeEditingHelper) {
        super("recentlyModifiedPagesAndBlogPostsByUser");
        this.contentEntityManager = contentEntityManager;
        this.userAccessor = userAccessor;
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    public Iterable<String> invoke(List<String> params, EvaluationContext context) {
        String username = params.get(0);
        UserKey userKey = username == null ? null : this.userAccessor.getUserByName(username).getKey();
        int offset = StringLiteralHelper.parseInt((String)params.get(1));
        int limit = StringLiteralHelper.parseInt((String)params.get(2));
        if (limit > 200) {
            throw new GenericQueryException("Limit higher than max limit");
        }
        PageResponse entitiesModifiedByUser = this.collaborativeEditingHelper.getEditMode("").equals("legacy") ? this.contentEntityManager.getPageAndBlogPostsVersionsLastEditedByUser(userKey, LimitedRequestImpl.create((int)offset, (int)limit, (int)200)) : this.contentEntityManager.getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(userKey, LimitedRequestImpl.create((int)offset, (int)limit, (int)200));
        List idsOfEntitiesModifiedByUser = Lists.transform((List)entitiesModifiedByUser.getResults(), input -> Long.toString(input.getLatestVersionId()));
        log.debug("Recently modified content IDs for user {} is {} ({} items)", new Object[]{username, idsOfEntitiesModifiedByUser, idsOfEntitiesModifiedByUser.size()});
        return idsOfEntitiesModifiedByUser;
    }

    public int paramCount() {
        return 3;
    }
}

