/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.query.EntityQueryException
 *  com.atlassian.user.search.query.EntityQueryParser
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.QueryContext
 *  com.atlassian.user.search.query.QueryValidator
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.atlassianuser.CrowdSearchResult;
import com.atlassian.crowd.embedded.atlassianuser.QueryRestrictionConverter;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.user.EntityException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.query.EntityQueryException;
import com.atlassian.user.search.query.EntityQueryParser;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.QueryContext;
import com.atlassian.user.search.query.QueryValidator;
import com.google.common.base.Preconditions;

@Deprecated
public final class EmbeddedCrowdEntityQueryParser
implements EntityQueryParser {
    private final RepositoryIdentifier repositoryIdentifier;
    private final CrowdService crowdService;
    private final QueryValidator queryValidator = new QueryValidator();
    private final QueryRestrictionConverter queryRestrictionConverter = new QueryRestrictionConverter();

    public EmbeddedCrowdEntityQueryParser(RepositoryIdentifier repositoryIdentifier, CrowdService crowdService) {
        this.repositoryIdentifier = (RepositoryIdentifier)Preconditions.checkNotNull((Object)repositoryIdentifier);
        this.crowdService = (CrowdService)Preconditions.checkNotNull((Object)crowdService);
    }

    public SearchResult<com.atlassian.user.User> findUsers(Query query) throws EntityQueryException {
        this.queryValidator.assertValid(query);
        SearchRestriction restriction = this.queryRestrictionConverter.toRestriction(query);
        EntityQuery crowdQuery = QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)restriction, (int)0, (int)-1);
        return CrowdSearchResult.forUsers(this.repositoryIdentifier, this.crowdService.search((com.atlassian.crowd.embedded.api.Query)crowdQuery));
    }

    public SearchResult<com.atlassian.user.Group> findGroups(Query query) throws EntityException {
        this.queryValidator.assertValid(query);
        SearchRestriction restriction = this.queryRestrictionConverter.toRestriction(query);
        EntityQuery crowdQuery = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group(), (SearchRestriction)restriction, (int)0, (int)-1);
        return CrowdSearchResult.forGroups(this.repositoryIdentifier, this.crowdService.search((com.atlassian.crowd.embedded.api.Query)crowdQuery));
    }

    public SearchResult<com.atlassian.user.User> findUsers(Query query, QueryContext context) throws EntityException {
        if (!context.contains(this.repositoryIdentifier)) {
            return null;
        }
        return this.findUsers(query);
    }

    public SearchResult<com.atlassian.user.Group> findGroups(Query query, QueryContext context) throws EntityException {
        if (!context.contains(this.repositoryIdentifier)) {
            return null;
        }
        return this.findGroups(query);
    }
}

