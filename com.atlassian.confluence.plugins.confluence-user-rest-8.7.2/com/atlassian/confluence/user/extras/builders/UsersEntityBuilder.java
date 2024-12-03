/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user.extras.builders;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UsersEntityBuilder {
    public static final int DEFAULT_MAX_RESULTS = 50;
    private final CrowdService crowdService;

    public UsersEntityBuilder(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public List<User> getUsers(String query, int start, int limit) {
        return ImmutableList.copyOf((Iterable)this.crowdService.search(this.buildSimpleUserQuery(query, start, limit)));
    }

    private Query<User> buildSimpleUserQuery(String query, int start, int limit) {
        if (StringUtils.isBlank((CharSequence)query)) {
            return QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).startingAt(start).returningAtMost(limit);
        }
        ArrayList<Property<String>> properties = new ArrayList<Property<String>>();
        properties.add(UserTermKeys.USERNAME);
        properties.add(UserTermKeys.DISPLAY_NAME);
        List<SearchRestriction> restrictions = this.convertToSearchRestrictions(query, properties);
        return QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Combine.anyOf(restrictions), (int)start, (int)limit);
    }

    private List<SearchRestriction> convertToSearchRestrictions(String query, List<Property<String>> properties) {
        String[] tokens;
        ArrayList<SearchRestriction> searchTerms = new ArrayList<SearchRestriction>();
        for (String token : tokens = query.trim().split("[\\s,]+")) {
            MatchMode mode = MatchMode.CONTAINS;
            if (this.startsWithWildcard(token)) {
                mode = MatchMode.STARTS_WITH;
            }
            String cleanToken = this.removeWildcards(token);
            for (Property<String> property : properties) {
                searchTerms.add((SearchRestriction)new TermRestriction(property, mode, (Object)cleanToken));
            }
        }
        return searchTerms;
    }

    private boolean startsWithWildcard(String term) {
        return term.startsWith("*");
    }

    private String removeWildcards(String term) {
        String s = term;
        if (s.endsWith("*")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.startsWith("*")) {
            s = s.substring(1);
        }
        return s;
    }
}

