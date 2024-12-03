/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Strings
 */
package com.atlassian.ratelimiting.internal.user;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.ratelimiting.internal.user.CommonUserService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class CrowdUserService
extends CommonUserService {
    private final CrowdService crowdService;

    public CrowdUserService(UserManager userManager, CrowdService crowdService) {
        super(userManager);
        this.crowdService = crowdService;
    }

    @Override
    public List<UserProfile> searchUsersForUserPicker(String criteria, int offset, int maxNumberOfResults) {
        ArrayList<UserProfile> searchResults = new ArrayList<UserProfile>();
        boolean resultsShouldReturnAnonymousUser = this.resultsShouldReturnAnonymousUser(criteria);
        if (resultsShouldReturnAnonymousUser) {
            searchResults.add(ANONYMOUS_REPRESENTATIVE_USER);
        }
        UserQuery query = new UserQuery(User.class, this.createUserSearchRestriction(criteria), offset, this.determineResultSetSizeForSearch(resultsShouldReturnAnonymousUser, maxNumberOfResults));
        StreamSupport.stream(this.crowdService.search((Query)query).spliterator(), false).map(this::crowdUserToSalUser).forEach(searchResults::add);
        return searchResults;
    }

    private SearchRestriction createUserSearchRestriction(String criteria) {
        if (Strings.isNullOrEmpty((String)criteria)) {
            return NullRestrictionImpl.INSTANCE;
        }
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, new SearchRestriction[]{new TermRestriction(UserTermKeys.USERNAME, MatchMode.CONTAINS, (Object)criteria), new TermRestriction(UserTermKeys.DISPLAY_NAME, MatchMode.CONTAINS, (Object)criteria), new TermRestriction(UserTermKeys.EMAIL, MatchMode.CONTAINS, (Object)criteria)});
    }

    private UserProfile crowdUserToSalUser(User crowdUser) {
        return this.userManager.getUserProfile(crowdUser.getName());
    }
}

