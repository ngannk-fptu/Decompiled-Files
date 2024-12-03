/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.UnfilteredCrowdService
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.component.ComponentLocator
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.service;

import com.atlassian.confluence.extra.calendar3.service.UserSearchRequest;
import com.atlassian.confluence.extra.calendar3.service.UserSearchService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.UnfilteredCrowdService;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.component.ComponentLocator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="userSearchService")
public class DefaultUserSearchService
implements UserSearchService {
    private static final String TERM_DELIM_CHARS = "[\\s,]+";
    private UserAccessor userAccessor;
    private CrowdService crowdService;

    @Autowired
    public DefaultUserSearchService(@Qualifier(value="userAccessor") UserAccessor userAccessor, @ComponentImport ComponentLocator componentLocator) {
        this.userAccessor = userAccessor;
        this.crowdService = (CrowdService)ComponentLocator.getComponent(UnfilteredCrowdService.class, (String)"unfilteredCrowdService");
    }

    @Override
    public Collection<ConfluenceUser> search(UserSearchRequest userSearchRequest) {
        Objects.nonNull(userSearchRequest);
        Query<String> query = this.convert(userSearchRequest);
        Iterable usernames = this.crowdService.search(query);
        Stream<String> usernameStream = StreamSupport.stream(usernames.spliterator(), false);
        Set<ConfluenceUser> foundUserSet = usernameStream.map(username -> this.userAccessor.getUserByName(username)).filter(Objects::nonNull).collect(Collectors.toSet());
        return foundUserSet;
    }

    private Query<String> convert(UserSearchRequest userSearchRequest) {
        String searchString = userSearchRequest.getSearchTerms();
        String[] searchItems = searchString.trim().split(TERM_DELIM_CHARS);
        ArrayList<TermRestriction> searchTerms = new ArrayList<TermRestriction>();
        for (String searchItem : searchItems) {
            searchTerms.add(new TermRestriction(UserTermKeys.USERNAME, (Object)searchItem));
            searchTerms.add(new TermRestriction(UserTermKeys.DISPLAY_NAME, (Object)searchItem));
            searchTerms.add(new TermRestriction(UserTermKeys.EMAIL, (Object)searchItem));
        }
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Combine.anyOf(searchTerms), (int)userSearchRequest.getStartIndex(), (int)userSearchRequest.getMaxResult());
        return query;
    }
}

