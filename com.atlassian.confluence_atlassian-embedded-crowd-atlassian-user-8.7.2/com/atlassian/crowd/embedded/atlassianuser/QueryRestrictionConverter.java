/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.user.search.query.BooleanQuery
 *  com.atlassian.user.search.query.EmailTermQuery
 *  com.atlassian.user.search.query.FullNameTermQuery
 *  com.atlassian.user.search.query.GroupNameTermQuery
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.search.query.TermQuery
 *  com.atlassian.user.search.query.UserNameTermQuery
 *  com.atlassian.user.util.Assert
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.user.search.query.BooleanQuery;
import com.atlassian.user.search.query.EmailTermQuery;
import com.atlassian.user.search.query.FullNameTermQuery;
import com.atlassian.user.search.query.GroupNameTermQuery;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.TermQuery;
import com.atlassian.user.search.query.UserNameTermQuery;
import com.atlassian.user.util.Assert;
import java.util.ArrayList;
import java.util.List;

@Deprecated
final class QueryRestrictionConverter {
    QueryRestrictionConverter() {
    }

    SearchRestriction toRestriction(Query query) {
        if (query instanceof BooleanQuery) {
            return this.toBooleanRestriction((BooleanQuery)query);
        }
        Assert.isTrue((boolean)(query instanceof TermQuery), (String)"There are only two basic types of queries: TermQuery and BooleanQuery");
        return this.toTermRestriction((TermQuery)query);
    }

    SearchRestriction toBooleanRestriction(BooleanQuery booleanQuery) {
        List nestedQueries = booleanQuery.getQueries();
        ArrayList<SearchRestriction> restrictions = new ArrayList<SearchRestriction>(nestedQueries.size());
        for (Query nestedQuery : nestedQueries) {
            restrictions.add(this.toRestriction(nestedQuery));
        }
        BooleanRestriction.BooleanLogic logic = booleanQuery.isAND() ? BooleanRestriction.BooleanLogic.AND : BooleanRestriction.BooleanLogic.OR;
        return new BooleanRestrictionImpl(logic, restrictions);
    }

    SearchRestriction toTermRestriction(TermQuery termQuery) {
        return new TermRestriction(this.getTermQueryProperty(termQuery), this.getTermQueryMatchMode(termQuery), (Object)termQuery.getTerm());
    }

    private MatchMode getTermQueryMatchMode(TermQuery query) {
        String matchingRule = query.getMatchingRule();
        if ("starts_with".equals(matchingRule)) {
            return MatchMode.STARTS_WITH;
        }
        if ("ends_with".equals(matchingRule)) {
            return MatchMode.CONTAINS;
        }
        if ("contains".equals(matchingRule)) {
            return MatchMode.CONTAINS;
        }
        if ("*".equals(matchingRule)) {
            throw new IllegalArgumentException("Wildcard queries are not accepted by Crowd");
        }
        return MatchMode.EXACTLY_MATCHES;
    }

    private Property<String> getTermQueryProperty(TermQuery query) {
        if (query instanceof UserNameTermQuery) {
            return UserTermKeys.USERNAME;
        }
        if (query instanceof EmailTermQuery) {
            return UserTermKeys.EMAIL;
        }
        if (query instanceof FullNameTermQuery) {
            return UserTermKeys.DISPLAY_NAME;
        }
        if (query instanceof GroupNameTermQuery) {
            return GroupTermKeys.NAME;
        }
        throw new IllegalArgumentException("Unknown query type: " + query);
    }
}

