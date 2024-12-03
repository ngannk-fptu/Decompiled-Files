/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.fugue.Pair
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.user;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.internal.user.GroupSearchRequest;
import com.atlassian.confluence.internal.user.SearchRequest;
import com.atlassian.confluence.internal.user.UserSearchRequest;
import com.atlassian.confluence.internal.user.UserSearchServiceInternal;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.fugue.Pair;
import com.atlassian.user.User;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserSearchServiceInternalImpl
implements UserSearchServiceInternal {
    static final int MAX_USERS_PER_PAGE = 500;
    static final int MAX_GROUPS_PER_PAGE = 500;
    private static final String TERM_DELIM_CHARS = "[\\s,]+";
    private final PaginationService paginationService;
    private final CrowdService crowdService;
    private final PermissionManager permissionManager;
    private final SettingsManager settingsManager;

    public UserSearchServiceInternalImpl(PaginationService paginationService, CrowdService crowdService, PermissionManager permissionManager, SettingsManager settingsManager) {
        this.paginationService = paginationService;
        this.crowdService = crowdService;
        this.permissionManager = permissionManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public PageResponse<ConfluenceUser> doUserSearch(PageRequest pageRequest, UserSearchRequest searchRequest) throws ServiceException {
        return this.paginationService.performPaginationListRequest(this.limitRequest(pageRequest), input -> {
            Query<String> finalQuery = searchRequest.isSimpleSearch() && this.isSupportsSimpleSearch() ? this.buildSimpleUserQuery(input.getStart(), input.getLimit() + 1, searchRequest) : this.buildAdvancedUserQuery(input.getStart(), input.getLimit() + 1, searchRequest);
            ImmutableList usernames = ImmutableList.copyOf((Iterable)this.crowdService.search(finalQuery));
            return PageResponseImpl.filteredResponse((LimitedRequest)input, (List)usernames, this.userHasUseConfluencePermission(searchRequest));
        }, items -> Iterables.transform((Iterable)items, FindUserHelper::getUserByUsername));
    }

    @Override
    public Pair<List<String>, PageResponse<ConfluenceUser>> doMemberOfGroupsSearch(PageRequest pageRequest, GroupSearchRequest searchRequest) throws ServiceException {
        List<SearchRestriction> searchRestrictions = this.convertToSearchRestrictions(searchRequest.getGroupTerm(), (Property<String>)GroupTermKeys.NAME);
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group(), (SearchRestriction)Combine.anyOf(searchRestrictions), (int)0, (int)500);
        ImmutableList groups = ImmutableList.copyOf((Iterable)this.crowdService.search((Query)query));
        if (groups.isEmpty()) {
            return Pair.pair((Object)groups, (Object)PageResponseImpl.empty((boolean)false));
        }
        Iterator groupsIterator = groups.iterator();
        String[] currentGroup = new String[]{(String)groupsIterator.next()};
        HashSet uniqueUsernames = new HashSet();
        PageResponse resultingUserList = this.paginationService.performPaginationListRequest(this.limitRequest(pageRequest), input -> {
            int skippedThisPass;
            boolean hasMore = true;
            ArrayList collectedUsernames = new ArrayList();
            int skippedThisCall = 0;
            do {
                GroupSearchRequest groupSearchRequest = (GroupSearchRequest)GroupSearchRequest.builder().groupTerm(currentGroup[0]).showUnlicensedUsers(searchRequest.isShowUnlicensedUsers()).build();
                List<String> groupResponse = this.getAllMembersOfGroup(groupSearchRequest);
                if (groupsIterator.hasNext()) {
                    currentGroup[0] = (String)groupsIterator.next();
                } else {
                    currentGroup[0] = null;
                    hasMore = false;
                }
                List filteredList = groupResponse.stream().filter(username -> !uniqueUsernames.contains(username)).filter(this.userHasUseConfluencePermissionJava(groupSearchRequest)).collect(Collectors.toList());
                filteredList.stream().forEach(uniqueUsernames::add);
                skippedThisPass = groupResponse.size() - filteredList.size();
                collectedUsernames.addAll(filteredList);
            } while (collectedUsernames.size() + (skippedThisCall += skippedThisPass) < input.getLimit() && hasMore);
            return PageResponseImpl.from(collectedUsernames, (boolean)hasMore).pageRequest(input).build();
        }, items -> Iterables.transform((Iterable)items, FindUserHelper::getUserByUsername));
        return Pair.pair((Object)groups, (Object)resultingUserList);
    }

    private List<String> getAllMembersOfGroup(GroupSearchRequest searchRequest) {
        MembershipQuery membershipQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(searchRequest.getGroupTerm()).startingAt(0).returningAtMost(Integer.MAX_VALUE);
        return ImmutableList.copyOf((Iterable)this.crowdService.search((Query)membershipQuery));
    }

    private LimitedRequest limitRequest(PageRequest pageRequest) {
        return LimitedRequestImpl.create((PageRequest)pageRequest, (int)500);
    }

    private com.google.common.base.Predicate<String> userHasUseConfluencePermission(SearchRequest searchRequest) {
        if (searchRequest.isShowUnlicensedUsers()) {
            return Predicates.alwaysTrue();
        }
        return username -> {
            ConfluenceUser confluenceUser = FindUserHelper.getUserByUsername(username);
            return this.permissionManager.hasPermission((User)confluenceUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        };
    }

    private Predicate<String> userHasUseConfluencePermissionJava(SearchRequest searchRequest) {
        if (searchRequest.isShowUnlicensedUsers()) {
            return username -> true;
        }
        return username -> {
            ConfluenceUser confluenceUser = FindUserHelper.getUserByUsername(username);
            return this.permissionManager.hasPermission((User)confluenceUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        };
    }

    protected List<SearchRestriction> convertToSearchRestrictions(String searchTerm, Property<String> property) throws ServiceException {
        return this.convertToSearchRestrictions(searchTerm, (List<Property<String>>)ImmutableList.of(property));
    }

    protected List<SearchRestriction> convertToSearchRestrictions(String searchString, List<Property<String>> properties) throws ServiceException {
        String[] tokens;
        ArrayList<SearchRestriction> searchTerms = new ArrayList<SearchRestriction>();
        for (String token : tokens = searchString.trim().split(TERM_DELIM_CHARS)) {
            MatchMode mode = MatchMode.EXACTLY_MATCHES;
            if (this.settingsManager.getGlobalSettings().isAddWildcardsToUserAndGroupSearches()) {
                mode = MatchMode.CONTAINS;
            } else if (this.startsWithWildcard(token) && this.endsWithWildcard(token)) {
                mode = MatchMode.CONTAINS;
            } else if (this.startsWithWildcard(token)) {
                mode = MatchMode.STARTS_WITH;
            } else if (this.endsWithWildcard(token)) {
                mode = MatchMode.CONTAINS;
            }
            String cleanToken = this.removeWildcards(token);
            for (Property<String> property : properties) {
                searchTerms.add((SearchRestriction)new TermRestriction(property, mode, (Object)cleanToken));
            }
        }
        return searchTerms;
    }

    private boolean startsWithWildcard(String s) {
        return s.startsWith("*");
    }

    private boolean endsWithWildcard(String s) {
        return s.endsWith("*");
    }

    private boolean containsWildcard(String s) {
        return s.contains("*");
    }

    protected String removeWildcards(String term) throws ServiceException {
        String s = term;
        if (s.endsWith("*")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.startsWith("*")) {
            s = s.substring(1);
        }
        this.validateNoMoreWildcards(term, s);
        return s;
    }

    private void validateNoMoreWildcards(String originalTerm, String strippedTerm) {
        if (this.containsWildcard(strippedTerm)) {
            SimpleValidationResult.Builder builder = SimpleValidationResult.builder();
            builder.addError("invalid.search.contains.wildcards", new Object[]{originalTerm});
            builder.build().throwIfNotSuccessful();
        }
    }

    @Override
    public boolean isSupportsSimpleSearch() {
        ConfluenceUser authenticatedUser = this.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return false;
        }
        String term = authenticatedUser.getName();
        ArrayList<TermRestriction> searchTerms = new ArrayList<TermRestriction>();
        searchTerms.add(new TermRestriction(UserTermKeys.USERNAME, (Object)term));
        searchTerms.add(new TermRestriction(UserTermKeys.DISPLAY_NAME, (Object)term));
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Combine.anyOf(searchTerms), (int)0, (int)1);
        Iterable result = this.crowdService.search((Query)query);
        return !Iterables.isEmpty((Iterable)result);
    }

    private @Nullable ConfluenceUser getAuthenticatedUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private Query<String> buildSimpleUserQuery(int startIndex, int maxResults, UserSearchRequest searchRequest) throws ServiceException {
        ArrayList<Property<String>> properties = new ArrayList<Property<String>>();
        properties.add(UserTermKeys.USERNAME);
        properties.add(UserTermKeys.DISPLAY_NAME);
        if (searchRequest.isShowEmail()) {
            properties.add(UserTermKeys.EMAIL);
        }
        List<SearchRestriction> restrictions = this.convertToSearchRestrictions(searchRequest.getSearchTerm(), properties);
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Combine.anyOf(restrictions), (int)startIndex, (int)maxResults);
    }

    private Query<String> buildAdvancedUserQuery(int startIndex, int maxResults, UserSearchRequest searchRequest) throws ServiceException {
        String emailTerm;
        String fullnameTerm;
        ArrayList<BooleanRestriction> andRestrictions = new ArrayList<BooleanRestriction>();
        String usernameTerm = searchRequest.getUsernameTerm();
        if (StringUtils.isNotEmpty((CharSequence)usernameTerm)) {
            List<SearchRestriction> usernameRestrictions = this.convertToSearchRestrictions(usernameTerm, (Property<String>)UserTermKeys.USERNAME);
            andRestrictions.add(Combine.anyOf(usernameRestrictions));
        }
        if (StringUtils.isNotEmpty((CharSequence)(fullnameTerm = searchRequest.getFullnameTerm()))) {
            List<SearchRestriction> fullnameRestrictions = this.convertToSearchRestrictions(fullnameTerm, (Property<String>)UserTermKeys.DISPLAY_NAME);
            andRestrictions.add(Combine.anyOf(fullnameRestrictions));
        }
        if (StringUtils.isNotEmpty((CharSequence)(emailTerm = searchRequest.getEmailTerm())) && searchRequest.isShowEmail()) {
            List<SearchRestriction> emailRestrictions = this.convertToSearchRestrictions(emailTerm, (Property<String>)UserTermKeys.EMAIL);
            andRestrictions.add(Combine.anyOf(emailRestrictions));
        }
        NullRestriction restriction = andRestrictions.isEmpty() ? NullRestrictionImpl.INSTANCE : Combine.allOf(andRestrictions);
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)restriction, (int)startIndex, (int)maxResults);
    }
}

