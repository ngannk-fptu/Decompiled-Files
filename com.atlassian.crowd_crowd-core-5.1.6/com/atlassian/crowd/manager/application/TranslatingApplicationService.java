/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.util.ProxyUtil
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.UserCapabilities
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.AliasManager
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.manager.application.ApplicationService$MembershipsIterable
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.manager.application.PagingNotSupportedException
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.event.AliasEvent
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.BaseImmutableGroup
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.atlassian.crowd.model.group.ImmutableGroupWithAttributes
 *  com.atlassian.crowd.model.group.ImmutableGroupWithAttributes$Builder
 *  com.atlassian.crowd.model.group.ImmutableMembership
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.BaseImmutableUser
 *  com.atlassian.crowd.model.user.ImmutableUserWithAttributes
 *  com.atlassian.crowd.model.user.ImmutableUserWithAttributes$Builder
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.QueryUtils
 *  com.atlassian.crowd.search.query.entity.AliasQuery
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.AliasTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.ResultsAggregator
 *  com.atlassian.crowd.search.util.ResultsAggregators
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Sets
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.common.util.ProxyUtil;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.AbstractDelegatingApplicationService;
import com.atlassian.crowd.manager.application.AliasManager;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.MembershipsIterableImpl;
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.event.AliasEvent;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.BaseImmutableGroup;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.atlassian.crowd.model.group.ImmutableGroupWithAttributes;
import com.atlassian.crowd.model.group.ImmutableMembership;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.BaseImmutableUser;
import com.atlassian.crowd.model.user.ImmutableUserWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.AliasQuery;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.AliasTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.ResultsAggregator;
import com.atlassian.crowd.search.util.ResultsAggregators;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TranslatingApplicationService
extends AbstractDelegatingApplicationService {
    private final AliasManager aliasManager;

    public TranslatingApplicationService(ApplicationService applicationService, AliasManager aliasManager) {
        super(applicationService);
        this.aliasManager = aliasManager;
    }

    @Override
    public User authenticateUser(Application application, String username, PasswordCredential passwordCredential) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        User user = this.getApplicationService().authenticateUser(application, unaliasedUsername, passwordCredential);
        return this.buildUserForApplication(application, user);
    }

    @Override
    public boolean isUserAuthorised(Application application, String username) {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        return this.getApplicationService().isUserAuthorised(application, unaliasedUsername);
    }

    @Override
    public boolean isUserAuthorised(Application application, User user) {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, user.getName());
        return this.getApplicationService().isUserAuthorised(application, unaliasedUsername);
    }

    @Override
    public User findUserByName(Application application, String name) throws UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, name);
        User user = this.getApplicationService().findUserByName(application, unaliasedUsername);
        return this.buildUserForApplication(application, user);
    }

    @Override
    public UserWithAttributes findUserWithAttributesByName(Application application, String name) throws UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, name);
        UserWithAttributes user = this.getApplicationService().findUserWithAttributesByName(application, unaliasedUsername);
        return this.buildUserWithAttributesForApplication(application, user);
    }

    @Override
    public User addUser(Application application, UserTemplate user, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        return this.addUser(application, UserTemplateWithAttributes.toUserWithNoAttributes((User)user), credential);
    }

    @Override
    public UserWithAttributes addUser(Application application, UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        return this.buildUserWithAttributesForApplication(application, this.getApplicationService().addUser(application, user, credential));
    }

    @Override
    public User updateUser(Application application, UserTemplate user) throws InvalidUserException, OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, user.getName());
        UserTemplate unaliasedUser = new UserTemplate((User)user);
        unaliasedUser.setName(unaliasedUsername);
        User updatedUser = this.getApplicationService().updateUser(application, unaliasedUser);
        return this.buildUserForApplication(application, updatedUser);
    }

    @Override
    public User renameUser(Application application, String oldUserName, String newUsername) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidUserException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, oldUserName);
        return this.buildUserForApplication(application, this.getApplicationService().renameUser(application, unaliasedUsername, newUsername));
    }

    @Override
    public void updateUserCredential(Application application, String username, PasswordCredential credential) throws OperationFailedException, UserNotFoundException, InvalidCredentialException, ApplicationPermissionException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        this.getApplicationService().updateUserCredential(application, unaliasedUsername, credential);
    }

    @Override
    public void storeUserAttributes(Application application, String username, Map<String, Set<String>> attributes) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        this.getApplicationService().storeUserAttributes(application, unaliasedUsername, attributes);
    }

    @Override
    public void removeUserAttributes(Application application, String username, String attributeName) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        this.getApplicationService().removeUserAttributes(application, unaliasedUsername, attributeName);
    }

    @Override
    public void removeUser(Application application, String user) throws OperationFailedException, UserNotFoundException, ApplicationPermissionException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, user);
        this.getApplicationService().removeUser(application, unaliasedUsername);
    }

    @Override
    public <T> List<T> searchUsers(Application application, EntityQuery<T> query) {
        return this.searchUsersInternal(application, query, (arg_0, arg_1) -> ((ApplicationService)this.getApplicationService()).searchUsers(arg_0, arg_1));
    }

    private <T> List<T> searchUsersInternal(Application application, EntityQuery<T> query, UserSearcher searcher) {
        if (!application.isAliasingEnabled()) {
            List<T> users = searcher.searchUsers(application, query);
            return this.buildListForApplication(application, users, query.getReturnType(), query.getEntityDescriptor());
        }
        SearchRestriction aliasedRestriction = this.replaceAliasesWithUsernames(application, query.getSearchRestriction());
        EntityQuery aliasedQuery = this.convertToUnboundUserQuery((Query<?>)query).withSearchRestriction(aliasedRestriction);
        Collection<User> users = this.doSearchUsers(application, (EntityQuery<User>)aliasedQuery, searcher);
        List<User> applicationUserList = this.buildListForApplication(application, users, User.class, EntityDescriptor.user());
        List<User> uniqueUserList = this.pruneDuplicates(applicationUserList);
        return this.convertToType(SearchResultsUtil.constrainResults(uniqueUserList, (int)query.getStartIndex(), (int)query.getMaxResults()), query.getReturnType());
    }

    @Override
    public Group findGroupByName(Application application, String name) throws GroupNotFoundException {
        return this.buildGroupForApplication(application, this.getApplicationService().findGroupByName(application, name));
    }

    @Override
    public GroupWithAttributes findGroupWithAttributesByName(Application application, String name) throws GroupNotFoundException {
        GroupWithAttributes group = this.getApplicationService().findGroupWithAttributesByName(application, name);
        if (application.isLowerCaseOutput()) {
            return ((ImmutableGroupWithAttributes.Builder)ImmutableGroupWithAttributes.builder((GroupWithAttributes)group).setName(IdentifierUtils.toLowerCase((String)group.getName()))).build();
        }
        return group;
    }

    @Override
    public Group addGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException {
        return this.buildGroupForApplication(application, this.getApplicationService().addGroup(application, group));
    }

    @Override
    public Group updateGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        return this.buildGroupForApplication(application, this.getApplicationService().updateGroup(application, group));
    }

    @Override
    public <T> List<T> searchGroups(Application application, EntityQuery<T> query) {
        return this.buildListForApplication(application, this.getApplicationService().searchGroups(application, query), query.getReturnType(), query.getEntityDescriptor());
    }

    @Override
    public void addUserToGroup(Application application, String username, String groupName) throws OperationFailedException, UserNotFoundException, GroupNotFoundException, ApplicationPermissionException, MembershipAlreadyExistsException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        this.getApplicationService().addUserToGroup(application, unaliasedUsername, groupName);
    }

    @Override
    public void removeUserFromGroup(Application application, String username, String groupName) throws OperationFailedException, GroupNotFoundException, UserNotFoundException, ApplicationPermissionException, MembershipNotFoundException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        this.getApplicationService().removeUserFromGroup(application, unaliasedUsername, groupName);
    }

    @Override
    public boolean isUserDirectGroupMember(Application application, String username, String groupName) {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        return this.getApplicationService().isUserDirectGroupMember(application, unaliasedUsername, groupName);
    }

    @Override
    public boolean isUserNestedGroupMember(Application application, String username, String groupName) {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        return this.getApplicationService().isUserNestedGroupMember(application, unaliasedUsername, groupName);
    }

    @Override
    public <T> List<T> searchDirectGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.searchGroupRelationshipsInternal(application, query, (arg_0, arg_1) -> ((ApplicationService)this.getApplicationService()).searchDirectGroupRelationships(arg_0, arg_1));
    }

    @Override
    public <T> List<T> searchNestedGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.searchGroupRelationshipsInternal(application, query, (arg_0, arg_1) -> ((ApplicationService)this.getApplicationService()).searchNestedGroupRelationships(arg_0, arg_1));
    }

    private <T> List<T> searchGroupRelationshipsInternal(Application application, MembershipQuery<T> query, RelationshipsSearcher searcher) {
        MembershipQuery<T> unaliasedQuery = this.buildUnaliasedMembershipQuery(application, query);
        List<T> result = searcher.searchGroupRelationships(application, unaliasedQuery);
        return this.buildListForApplication(application, result, query.getReturnType(), query.getEntityToReturn());
    }

    @Override
    public Events getNewEvents(Application application, String eventToken) throws EventTokenExpiredException, OperationFailedException {
        Events result = this.getApplicationService().getNewEvents(application, eventToken);
        if (!application.isAliasingEnabled() && !application.isLowerCaseOutput()) {
            return result;
        }
        ArrayList<UserEvent> applicationEvents = new ArrayList<UserEvent>();
        for (OperationEvent event : result.getEvents()) {
            UserEvent applicationEvent;
            if (event instanceof UserEvent) {
                UserEvent userEvent = (UserEvent)event;
                if (application.isAliasingEnabled() && userEvent.getOperation() == Operation.DELETED) {
                    throw new EventTokenExpiredException("User deleted events invalidate incremental synchronisation, try a full synchronisation");
                }
                User applicationUser = this.buildUserForApplication(application, userEvent.getUser());
                applicationEvent = new UserEvent(event.getOperation(), event.getDirectoryId(), applicationUser, userEvent.getStoredAttributes(), userEvent.getDeletedAttributes());
            } else if (event instanceof GroupEvent) {
                GroupEvent groupEvent = (GroupEvent)event;
                Group applicationGroup = this.buildGroupForApplication(application, groupEvent.getGroup());
                applicationEvent = new GroupEvent(event.getOperation(), event.getDirectoryId(), applicationGroup, groupEvent.getStoredAttributes(), groupEvent.getDeletedAttributes());
            } else if (event instanceof UserMembershipEvent) {
                UserMembershipEvent userMembershipEvent = (UserMembershipEvent)event;
                String applicationChildUsername = this.resolveUsernameForApplication(application, userMembershipEvent.getChildUsername());
                ImmutableSet applicationGroupNames = ImmutableSet.copyOf(this.buildGroupNamesForApplication(application, userMembershipEvent.getParentGroupNames()));
                applicationEvent = new UserMembershipEvent(event.getOperation(), event.getDirectoryId(), applicationChildUsername, (Set)applicationGroupNames);
            } else if (event instanceof GroupMembershipEvent) {
                GroupMembershipEvent groupMembershipEvent = (GroupMembershipEvent)event;
                String applicationGroupName = this.lowercaseNameIfNecessary(application, groupMembershipEvent.getGroupName());
                ImmutableSet applicationParentGroupNames = ImmutableSet.copyOf(this.buildGroupNamesForApplication(application, groupMembershipEvent.getParentGroupNames()));
                ImmutableSet applicationChildGroupNames = ImmutableSet.copyOf(this.buildGroupNamesForApplication(application, groupMembershipEvent.getChildGroupNames()));
                applicationEvent = new GroupMembershipEvent(event.getOperation(), event.getDirectoryId(), applicationGroupName, (Set)applicationParentGroupNames, (Set)applicationChildGroupNames);
            } else {
                if (event instanceof AliasEvent) {
                    AliasEvent aliasEvent = (AliasEvent)event;
                    if (!application.getId().equals(aliasEvent.getApplicationId())) continue;
                    throw new EventTokenExpiredException("Alias events invalidate incremental synchronisation, try a full synchronisation");
                }
                throw new IllegalArgumentException("Event type " + event.getClass() + " not supported.");
            }
            applicationEvents.add(applicationEvent);
        }
        return new Events(applicationEvents, result.getNewEventToken());
    }

    @Override
    public UserCapabilities getCapabilitiesForNewUsers(Application application) {
        return this.getApplicationService().getCapabilitiesForNewUsers(application);
    }

    @Override
    public User userAuthenticated(Application application, String username) throws UserNotFoundException, OperationFailedException, InactiveAccountException {
        String unaliasedUsername = this.aliasManager.findUsernameByAlias(application, username);
        User updatedUser = this.getApplicationService().userAuthenticated(application, unaliasedUsername);
        return this.buildUserForApplication(application, updatedUser);
    }

    private <T> List<T> convertToType(List<User> applicationList, Class<T> returnType) {
        QueryUtils.checkAssignableFrom(returnType, (Class[])new Class[]{String.class, User.class});
        if (String.class.isAssignableFrom(returnType)) {
            return SearchResultsUtil.convertEntitiesToNames(applicationList);
        }
        return applicationList;
    }

    private List<User> pruneDuplicates(Collection<User> users) {
        ResultsAggregator aggregator = ResultsAggregators.with((Function)NameComparator.normaliserOf(User.class), (int)0, (int)-1);
        aggregator.addAll(users);
        return aggregator.constrainResults();
    }

    private SearchRestriction replaceAliasesWithUsernames(Application application, SearchRestriction restrictions) {
        PropertyRestriction restriction;
        if (restrictions instanceof BooleanRestriction) {
            BooleanRestriction restriction2 = (BooleanRestriction)restrictions;
            ArrayList<SearchRestriction> childRestrictions = new ArrayList<SearchRestriction>(restriction2.getRestrictions().size());
            for (SearchRestriction childRestriction : restriction2.getRestrictions()) {
                childRestrictions.add(this.replaceAliasesWithUsernames(application, childRestriction));
            }
            return new BooleanRestrictionImpl(restriction2.getBooleanLogic(), childRestrictions);
        }
        if (restrictions instanceof PropertyRestriction && UserTermKeys.USERNAME.equals((restriction = (PropertyRestriction)restrictions).getProperty()) && restriction.getMatchMode().isExact() && restriction.getValue() != null) {
            String username = this.aliasManager.findUsernameByAlias(application, (String)restriction.getValue());
            return new TermRestriction(UserTermKeys.USERNAME, restriction.getMatchMode(), (Object)username);
        }
        return restrictions;
    }

    private Collection<User> doSearchUsers(Application application, EntityQuery<User> query, UserSearcher searcher) {
        SearchRestriction restrictions = query.getSearchRestriction();
        if (this.containsNonExactUsernameRestrictions(restrictions)) {
            if (restrictions instanceof BooleanRestriction) {
                BooleanRestriction restriction = (BooleanRestriction)restrictions;
                ResultCombiner<User> combiner = new ResultCombiner<User>(restriction.getBooleanLogic());
                for (SearchRestriction childRestriction : restriction.getRestrictions()) {
                    EntityQuery childQuery = QueryBuilder.queryFor((Class)query.getReturnType(), (EntityDescriptor)query.getEntityDescriptor(), (SearchRestriction)childRestriction, (int)0, (int)-1);
                    Collection<User> childResults = this.doSearchUsers(application, (EntityQuery<User>)childQuery, searcher);
                    combiner.combine(childResults);
                }
                return combiner.getValues();
            }
            if (restrictions instanceof PropertyRestriction) {
                PropertyRestriction usernameRestriction = (PropertyRestriction)restrictions;
                return this.searchWithNonExactUsernameRestriction(application, (PropertyRestriction<String>)usernameRestriction, searcher);
            }
            throw new IllegalArgumentException("Unexpected restriction");
        }
        return searcher.searchUsers(application, query);
    }

    private boolean containsNonExactUsernameRestrictions(SearchRestriction restrictions) {
        if (restrictions instanceof BooleanRestriction) {
            BooleanRestriction restriction = (BooleanRestriction)restrictions;
            return restriction.getRestrictions().stream().anyMatch(this::containsNonExactUsernameRestrictions);
        }
        if (restrictions instanceof PropertyRestriction) {
            PropertyRestriction restriction = (PropertyRestriction)restrictions;
            return UserTermKeys.USERNAME.equals(restriction.getProperty()) && !restriction.getMatchMode().isExact();
        }
        return false;
    }

    private Collection<User> searchWithNonExactUsernameRestriction(Application application, PropertyRestriction<String> restriction, UserSearcher searcher) {
        ArrayList<User> users = new ArrayList<User>();
        BooleanRestrictionImpl aliasRestriction = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, new SearchRestriction[]{new TermRestriction(AliasTermKeys.ALIAS, restriction.getMatchMode(), restriction.getValue()), new TermRestriction(AliasTermKeys.APPLICATION_ID, MatchMode.EXACTLY_MATCHES, (Object)application.getId())});
        AliasQuery aliasQuery = new AliasQuery((SearchRestriction)aliasRestriction, 0, -1);
        for (String aliasMatchUsername : this.aliasManager.search((EntityQuery)aliasQuery)) {
            EntityQuery aliasedUserQuery = QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatching((Object)aliasMatchUsername), (int)0, (int)-1);
            users.addAll(searcher.searchUsers(application, aliasedUserQuery));
        }
        UserQuery userQuery = new UserQuery(User.class, restriction, 0, -1);
        List matchingUsers = searcher.searchUsers(application, userQuery);
        for (User user : matchingUsers) {
            if (this.aliasExists(application, user)) continue;
            users.add(user);
        }
        return users;
    }

    private boolean aliasExists(Application application, User user) {
        String alias = this.aliasManager.findAliasByUsername(application, user.getName());
        return !user.getName().equalsIgnoreCase(alias);
    }

    private <T> MembershipQuery<T> buildUnaliasedMembershipQuery(Application application, MembershipQuery<T> query) {
        if (query.getEntityToMatch().equals((Object)EntityDescriptor.user())) {
            return query.withEntityNames((Collection)query.getEntityNamesToMatch().stream().map(alias -> this.aliasManager.findUsernameByAlias(application, alias)).collect(Collectors.toList()));
        }
        return query;
    }

    private <T> List<T> buildListForApplication(Application application, Collection<T> collection, Class<T> returnType, EntityDescriptor entityToReturn) {
        return this.buildListForApplicationFunction(application, returnType, entityToReturn).apply(collection);
    }

    private <T> Function<Collection<T>, List<T>> buildListForApplicationFunction(Application application, Class<T> returnType, EntityDescriptor entityToReturn) {
        if (EntityDescriptor.user().equals((Object)entityToReturn) && String.class.isAssignableFrom(returnType)) {
            return collection -> this.buildUsernamesForApplication(application, (Collection<String>)collection);
        }
        if (User.class.isAssignableFrom(returnType)) {
            return collection -> this.buildUsersForApplication(application, (Collection<User>)collection);
        }
        if (EntityDescriptor.group().equals((Object)entityToReturn) && String.class.isAssignableFrom(returnType)) {
            return collection -> this.buildGroupNamesForApplication(application, (Collection<String>)collection);
        }
        if (Group.class.isAssignableFrom(returnType)) {
            return collection -> this.buildGroupsForApplication(application, (Collection<Group>)collection);
        }
        return this::asList;
    }

    private <T> List<T> asList(Collection<T> collection) {
        if (collection instanceof List) {
            return (List)collection;
        }
        return new ArrayList<T>(collection);
    }

    private List<String> buildUsernamesForApplication(Application application, Collection<String> usernames) {
        return this.buildUsernamesForApplication(application, usernames, (IdentifierMap<String>)new IdentifierMap(this.aliasManager.findAliasesByUsernames(application, usernames)));
    }

    private List<String> buildUsernamesForApplication(Application application, Collection<String> usernames, IdentifierMap<String> aliasesForUsers) {
        return usernames.stream().map(name -> this.resolveUsernameForApplication(application, (String)name, (Optional<String>)Optional.ofNullable(aliasesForUsers.get(name)))).sorted(IdentifierUtils::compareToInLowerCase).collect(Collectors.toList());
    }

    private String resolveUsernameForApplication(Application application, String username) {
        String alias = this.aliasManager.findAliasByUsername(application, username);
        return this.resolveUsernameForApplication(application, username, Optional.of(alias));
    }

    private String resolveUsernameForApplication(Application application, String username, Optional<String> aliasOptional) {
        String applicationUsername = aliasOptional.orElse(username);
        return this.lowercaseNameIfNecessary(application, applicationUsername);
    }

    private String lowercaseNameIfNecessary(Application application, String alias) {
        return application.isLowerCaseOutput() ? IdentifierUtils.toLowerCase((String)alias) : alias;
    }

    private List<User> buildUsersForApplication(Application application, Collection<User> users) {
        ResultsAggregator aggregator = ResultsAggregators.with((Function)UserComparator.KEY_MAKER, (int)0, (int)-1);
        IdentifierMap aliasesForUsers = new IdentifierMap(this.aliasManager.findAliasesByUsernames(application, Iterables.transform(users, Principal::getName)));
        for (User user : users) {
            aggregator.add((Object)this.buildUserForApplication(application, user, Optional.ofNullable(aliasesForUsers.get((Object)user.getName()))));
        }
        return aggregator.constrainResults();
    }

    private User buildUserForApplication(Application application, User user) {
        String alias = this.aliasManager.findAliasByUsername(application, user.getName());
        return this.buildUserForApplication(application, user, Optional.of(alias));
    }

    private User buildUserForApplication(Application application, User user, Optional<String> aliasOptional) {
        String applicationUsername = this.resolveUsernameForApplication(application, user.getName(), aliasOptional);
        if (user.getName().equals(applicationUsername)) {
            return user;
        }
        if (user instanceof BaseImmutableUser) {
            return ((BaseImmutableUser)user).withName(applicationUsername);
        }
        UserTemplate applicationUser = new UserTemplate(user);
        applicationUser.setName(applicationUsername);
        return applicationUser;
    }

    private UserWithAttributes buildUserWithAttributesForApplication(Application application, UserWithAttributes user) {
        String applicationUsername = this.resolveUsernameForApplication(application, user.getName());
        return user.getName().equals(applicationUsername) ? user : ((ImmutableUserWithAttributes.Builder)ImmutableUserWithAttributes.builder((UserWithAttributes)user).name(applicationUsername)).build();
    }

    private List<String> buildGroupNamesForApplication(Application application, Collection<String> groupNames) {
        if (application.isLowerCaseOutput()) {
            return groupNames.stream().map(IdentifierUtils::toLowerCase).collect(Collectors.toList());
        }
        return this.asList(groupNames);
    }

    private List<Group> buildGroupsForApplication(Application application, Collection<Group> groups) {
        if (application.isLowerCaseOutput()) {
            return groups.stream().map(group -> this.buildGroupForApplication(application, (Group)group)).collect(Collectors.toList());
        }
        return this.asList(groups);
    }

    private Group buildGroupForApplication(Application application, Group group) {
        String lowerName = this.lowercaseNameIfNecessary(application, group.getName());
        if (lowerName.equals(group.getName())) {
            return group;
        }
        if (group instanceof BaseImmutableGroup) {
            return ((BaseImmutableGroup)group).withName(lowerName);
        }
        if (group instanceof GroupWithAttributes) {
            return ((ImmutableGroupWithAttributes.Builder)ImmutableGroupWithAttributes.builder((GroupWithAttributes)((GroupWithAttributes)group)).setName(lowerName)).build();
        }
        return ImmutableGroup.builder((Group)group).setName(lowerName).build();
    }

    private EntityQuery<User> convertToUnboundUserQuery(Query<?> query) {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, User.class});
        return QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user(), (SearchRestriction)query.getSearchRestriction(), (int)0, (int)-1);
    }

    @Override
    public ApplicationService.MembershipsIterable getMemberships(final Application application) {
        final ApplicationService.MembershipsIterable memberships = super.getMemberships(application);
        final IdentifierMap aliases = new IdentifierMap(this.aliasManager.findAllAliasesByUsernames(application));
        ApplicationService.MembershipsIterable iterable = new ApplicationService.MembershipsIterable(){

            public int groupCount() {
                return memberships.groupCount();
            }

            public Iterator<Membership> iterator() {
                return Iterators.transform((Iterator)memberships.iterator(), memberships -> TranslatingApplicationService.this.translateMembership(memberships, (IdentifierMap<String>)aliases, application));
            }
        };
        return MembershipsIterableImpl.runWithClassLoader(Thread.currentThread().getContextClassLoader(), iterable);
    }

    private Membership translateMembership(Membership membership, IdentifierMap<String> aliases, Application application) {
        return new ImmutableMembership(this.lowercaseNameIfNecessary(application, membership.getGroupName()), this.buildUsernamesForApplication(application, membership.getUserNames(), aliases), this.buildGroupNamesForApplication(application, membership.getChildGroupNames()));
    }

    @Override
    public <T> PagedSearcher<T> createPagedGroupSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        return this.translating(super.createPagedGroupSearcher(application, query), application, query);
    }

    @Override
    public <T> PagedSearcher<T> createPagedUserSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        if (application.isAliasingEnabled()) {
            throw new PagingNotSupportedException("Paged queries are not supported when aliasing is enabled");
        }
        return this.translating(super.createPagedUserSearcher(application, query), application, query);
    }

    private <T> PagedSearcher<T> translating(PagedSearcher<T> searcher, Application application, EntityQuery<T> query) {
        Function<Collection<T>, List<T>> unproxiedTransformer = this.buildListForApplicationFunction(application, query.getReturnType(), query.getEntityDescriptor());
        Function transformer = (Function)ProxyUtil.runWithContextClassLoader((ClassLoader)Thread.currentThread().getContextClassLoader(), unproxiedTransformer);
        return batchSize -> (List)transformer.apply(searcher.fetchNextBatch(batchSize));
    }

    private static interface RelationshipsSearcher {
        public <T> List<T> searchGroupRelationships(Application var1, MembershipQuery<T> var2);
    }

    private static interface UserSearcher {
        public <T> List<T> searchUsers(Application var1, EntityQuery<T> var2);
    }

    private static class ResultCombiner<T> {
        private final BooleanRestriction.BooleanLogic logic;
        private Set<T> values = null;

        ResultCombiner(BooleanRestriction.BooleanLogic logic) {
            this.logic = logic;
        }

        void combine(Collection<T> newValues) {
            if (this.values == null) {
                this.values = Sets.newHashSet(newValues);
            } else if (this.logic == BooleanRestriction.BooleanLogic.AND) {
                this.values.retainAll(newValues);
            } else if (this.logic == BooleanRestriction.BooleanLogic.OR) {
                this.values.addAll(newValues);
            }
        }

        Set<T> getValues() {
            return this.values == null ? Collections.emptySet() : this.values;
        }
    }
}

