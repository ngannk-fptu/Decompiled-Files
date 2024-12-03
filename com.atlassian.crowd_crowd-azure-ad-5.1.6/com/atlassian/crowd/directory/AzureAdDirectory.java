/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.AttributeValuesHolder
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.Defaults
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.QueryUtils
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.QuerySplitter
 *  com.atlassian.crowd.search.util.ResultsAggregator
 *  com.atlassian.crowd.search.util.ResultsAggregators
 *  com.atlassian.crowd.util.AttributeUtil
 *  com.atlassian.crowd.util.BoundedCount
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.AttributeValuesHolder;
import com.atlassian.crowd.directory.AzureMembershipHelper;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.authentication.UserCredentialVerifier;
import com.atlassian.crowd.directory.authentication.UserCredentialVerifierFactory;
import com.atlassian.crowd.directory.cache.AzureGroupFilterProcessor;
import com.atlassian.crowd.directory.query.FetchMode;
import com.atlassian.crowd.directory.query.GraphQuery;
import com.atlassian.crowd.directory.query.MicrosoftGraphDeltaToken;
import com.atlassian.crowd.directory.query.MicrosoftGraphQueryTranslator;
import com.atlassian.crowd.directory.query.ODataExpand;
import com.atlassian.crowd.directory.query.ODataFilter;
import com.atlassian.crowd.directory.query.ODataSelect;
import com.atlassian.crowd.directory.query.ODataTop;
import com.atlassian.crowd.directory.rest.AzureAdPagingWrapper;
import com.atlassian.crowd.directory.rest.AzureAdRestClient;
import com.atlassian.crowd.directory.rest.AzureAdRestClientFactory;
import com.atlassian.crowd.directory.rest.delta.GraphDeltaQueryResult;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolverFactory;
import com.atlassian.crowd.directory.rest.entity.GraphDirectoryObjectList;
import com.atlassian.crowd.directory.rest.entity.PageableGraphList;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryGroup;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryUser;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroup;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroupList;
import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import com.atlassian.crowd.directory.rest.entity.user.GraphUser;
import com.atlassian.crowd.directory.rest.mapper.AzureAdRestEntityMapper;
import com.atlassian.crowd.directory.rest.mapper.DeltaQueryResult;
import com.atlassian.crowd.directory.rest.util.MembershipFilterUtil;
import com.atlassian.crowd.directory.synchronisation.Defaults;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.GroupWithMembershipChanges;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.QueryUtils;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.QuerySplitter;
import com.atlassian.crowd.search.util.ResultsAggregator;
import com.atlassian.crowd.search.util.ResultsAggregators;
import com.atlassian.crowd.util.AttributeUtil;
import com.atlassian.crowd.util.BoundedCount;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.security.Principal;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureAdDirectory
implements RemoteDirectory {
    private static Logger logger = LoggerFactory.getLogger(AzureAdDirectory.class);
    public static final String WEBAPP_CLIENT_ID_ATTRIBUTE = "AZURE_AD_WEBAPP_CLIENT_ID";
    public static final String WEBAPP_CLIENT_SECRET_ATTRIBUTE = "AZURE_AD_WEBAPP_CLIENT_SECRET";
    public static final String TENANT_ID_ATTRIBUTE = "AZURE_AD_TENANT_ID";
    public static final String NATIVE_APP_ID_ATTRIBUTE = "AZURE_AD_NATIVE_AP_IDD";
    public static final String GRAPH_API_ENDPOINT_ATTRIBUTE = "AZURE_AD_GRAPH_API_ENDPOINT";
    public static final String AUTHORITY_API_ENDPOINT_ATTRIBUTE = "AZURE_AD_AUTHORITY_API_ENDPOINT";
    public static final String REGION_ATTRIBUTE = "AZURE_AD_REGION";
    public static final String CUSTOM_REGION_ATTRIBUTE_VALUE = "CUSTOM";
    public static final String FILTERED_GROUPS_ATTRIBUTE = "AZURE_AD_FILTERED_GROUPS";
    public static final String GROUP_FILTERING_ENABLED_ATTRIBUTE = "GROUP_FILTERING_ENABLED";
    public static final String NOT_IMPLEMENTED = "Azure Active Directory support is Read-only";
    public static final String ALTERNATIVE_USERNAME_ATTRIBUTE = "ALTERNATIVE_USERNAME_ATTRIBUTE";
    public static final int MAX_RESTRICTIONS_PER_QUERY = 10;
    private final AzureAdRestClientFactory restClientFactory;
    private final MicrosoftGraphQueryTranslator graphQueryTranslator;
    private final AzureAdRestEntityMapper restEntityMapper;
    private final UserCredentialVerifierFactory credentialVerifierFactory;
    private final AzureApiUriResolverFactory endpointDataProviderFactory;
    private final DcLicenseChecker dcLicenseChecker;
    private AzureApiUriResolver endpointDataProvider;
    private Supplier<UserCredentialVerifier> userCredentialVerifier;
    private Supplier<AzureAdRestClient> azureAdRestClient;
    private Supplier<AzureAdPagingWrapper> azureAdPagingWrapper;
    private AttributeValuesHolder attributes;
    private long directoryId;
    private boolean supportsNestedGroups;
    private boolean localGroupsEnabled;

    public AzureAdDirectory(AzureAdRestClientFactory restClientFactory, MicrosoftGraphQueryTranslator graphQueryTranslator, AzureAdRestEntityMapper restEntityMapper, UserCredentialVerifierFactory credentialVerifierFactory, AzureApiUriResolverFactory endpointDataProviderFactory, DcLicenseChecker dcLicenseChecker) {
        this.restClientFactory = restClientFactory;
        this.graphQueryTranslator = graphQueryTranslator;
        this.restEntityMapper = restEntityMapper;
        this.credentialVerifierFactory = credentialVerifierFactory;
        this.endpointDataProviderFactory = endpointDataProviderFactory;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    @Nullable
    public Set<String> getValues(String key) {
        return this.attributes.getValues(key);
    }

    @Nullable
    public String getValue(String key) {
        return this.attributes.getValue(key);
    }

    public Set<String> getKeys() {
        return this.attributes.getKeys();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    @Nonnull
    public String getDescriptiveName() {
        return "Microsoft Azure Active Directory";
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new AttributeValuesHolder(attributes);
        String webappClientId = attributes.get(WEBAPP_CLIENT_ID_ATTRIBUTE);
        String webappClientSecret = attributes.get(WEBAPP_CLIENT_SECRET_ATTRIBUTE);
        String tenantId = attributes.get(TENANT_ID_ATTRIBUTE);
        String nativeClientId = attributes.get(NATIVE_APP_ID_ATTRIBUTE);
        Duration connectionTimeout = AttributeUtil.safeParseDurationMillis((String)attributes.get("ldap.connection.timeout"), (Duration)Defaults.CONNECTION_TIMEOUT);
        Duration readTimeout = AttributeUtil.safeParseDurationMillis((String)attributes.get("ldap.read.timeout"), (Duration)Defaults.READ_TIMEOUT);
        this.endpointDataProvider = this.endpointDataProviderFactory.getEndpointDataProviderForDirectory(this);
        this.supportsNestedGroups = Boolean.parseBoolean(attributes.get("useNestedGroups"));
        this.localGroupsEnabled = Boolean.parseBoolean(attributes.get("ldap.local.groups"));
        this.azureAdRestClient = Suppliers.memoize(() -> this.restClientFactory.create(webappClientId, webappClientSecret, tenantId, this.endpointDataProvider, connectionTimeout.toMillis(), readTimeout.toMillis()));
        this.azureAdPagingWrapper = Suppliers.memoize(() -> this.restClientFactory.create(this.getRestClient()));
        this.userCredentialVerifier = Suppliers.memoize(() -> this.credentialVerifierFactory.create(this.endpointDataProvider, nativeClientId, tenantId));
    }

    @Nonnull
    public User findUserByName(String name) throws UserNotFoundException, OperationFailedException {
        EntityQuery query = QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatching((Object)name)).returningAtMost(2);
        return (User)this.getSingleResult(this.searchUsersWithFallback(query, true), () -> new UserNotFoundException(name));
    }

    @Nonnull
    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        return UserTemplateWithAttributes.toUserWithNoAttributes((User)this.findUserByName(name));
    }

    @Nonnull
    public User findUserByExternalId(String externalId) throws UserNotFoundException, OperationFailedException {
        EntityQuery query = QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.EXTERNAL_ID).exactlyMatching((Object)externalId)).returningAtMost(2);
        return (User)this.getSingleResult(this.searchUsersInternal(query), () -> UserNotFoundException.forExternalId((String)externalId));
    }

    @Nonnull
    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        User user = this.findUserByName(name);
        this.getUserCredentialVerifier().checkUserCredential(name, credential);
        return user;
    }

    @Nonnull
    public User addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, UserAlreadyExistsException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public <T> List<T> searchUsers(EntityQuery<T> query) throws OperationFailedException {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, User.class});
        return QuerySplitter.batchConditionsIfNeeded(query, q -> this.searchUsersWithFallback((EntityQuery)q, false), (int)10);
    }

    private <T> List<T> searchUsersWithFallback(EntityQuery<T> query, boolean fallbackOnlyIfEmpty) throws OperationFailedException {
        String alternativeUsernameAttribute = this.getAlternativeUsernameAttribute();
        if (StringUtils.isEmpty((CharSequence)alternativeUsernameAttribute) || !this.hasUsernameRestriction(query.getSearchRestriction())) {
            return this.searchUsersInternal(query);
        }
        if (fallbackOnlyIfEmpty) {
            Preconditions.checkArgument((query.getStartIndex() == 0 ? 1 : 0) != 0);
            List<T> originalResults = this.searchWithUpnFiltering(query, alternativeUsernameAttribute, true);
            return originalResults.isEmpty() ? this.searchWithUpnFiltering(query, alternativeUsernameAttribute, false) : originalResults;
        }
        ResultsAggregator aggregator = ResultsAggregators.with(query);
        aggregator.addAll(this.searchWithUpnFiltering(query.withAllResults(), alternativeUsernameAttribute, true));
        aggregator.addAll(this.searchWithUpnFiltering(query.withAllResults(), alternativeUsernameAttribute, false));
        return aggregator.constrainResults();
    }

    private <T> List<T> searchWithUpnFiltering(EntityQuery<T> fallbackQuery, String alternativeUsernameAttribute, boolean matchUpn) throws OperationFailedException {
        String matchAttribute = matchUpn ? "userPrincipalName" : alternativeUsernameAttribute;
        return this.searchGraphUsers(fallbackQuery.withReturnType(User.class), matchAttribute).stream().filter(u -> this.matches((GraphUser)u, matchUpn, alternativeUsernameAttribute)).map(u -> this.restEntityMapper.mapUser((GraphUser)u, fallbackQuery.getReturnType(), this.directoryId, alternativeUsernameAttribute)).collect(Collectors.toList());
    }

    private boolean matches(GraphUser user, boolean matchUpn, String alternativeUsernameAttribute) {
        String mappedUsername = this.restEntityMapper.getUsername(user, alternativeUsernameAttribute);
        boolean upnAsUsername = Objects.equals(user.getUserPrincipalName(), mappedUsername);
        return matchUpn == upnAsUsername;
    }

    private boolean hasUsernameRestriction(SearchRestriction restriction) {
        if (restriction instanceof PropertyRestriction) {
            return ((PropertyRestriction)restriction).getProperty().equals(UserTermKeys.USERNAME);
        }
        if (restriction instanceof BooleanRestriction) {
            return ((BooleanRestriction)restriction).getRestrictions().stream().anyMatch(this::hasUsernameRestriction);
        }
        return false;
    }

    private <T> List<T> searchUsersInternal(EntityQuery<T> query) throws OperationFailedException {
        List<GraphUser> graphUsers = this.searchGraphUsers(this.convert(query), query);
        return this.restEntityMapper.mapUsers(graphUsers, query.getReturnType(), this.getDirectoryId(), this.getAlternativeUsernameAttribute());
    }

    private List<GraphUser> searchGraphUsers(EntityQuery<?> query, String usernameAttribute) throws OperationFailedException {
        return this.searchGraphUsers(this.graphQueryTranslator.convert(query, usernameAttribute), query);
    }

    private List<GraphUser> searchGraphUsers(GraphQuery graphQuery, EntityQuery<?> query) throws OperationFailedException {
        return this.getAzureAdPagingWrapper().fetchAppropriateAmountOfResults(this.getRestClient().searchUsers(graphQuery), query.getStartIndex(), query.getMaxResults());
    }

    @Nonnull
    public Group findGroupByName(String name) throws GroupNotFoundException, OperationFailedException {
        EntityQuery query = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).exactlyMatching((Object)name)).returningAtMost(2);
        GraphGroupList graphGroupList = this.getRestClient().searchGroups(this.convert(query));
        this.validateSingleResult(graphGroupList, () -> new GroupNotFoundException(name));
        return (Group)Iterables.getOnlyElement(this.restEntityMapper.mapGroups(graphGroupList, query.getReturnType(), this.directoryId));
    }

    @Nonnull
    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        return GroupTemplateWithAttributes.ofGroupWithNoAttributes((Group)this.findGroupByName(name));
    }

    @Nonnull
    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeGroup(String name) throws GroupNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public <T> List<T> searchGroups(EntityQuery<T> query) throws OperationFailedException {
        QueryUtils.checkAssignableFrom((Class)query.getReturnType(), (Class[])new Class[]{String.class, Group.class});
        return QuerySplitter.batchConditionsIfNeeded(query, this::searchGroupsSplit, (int)10);
    }

    private <T> List<T> searchGroupsSplit(EntityQuery<T> query) throws OperationFailedException {
        GraphGroupList graphGroupsList = this.getRestClient().searchGroups(this.convert(query));
        List<GraphGroup> results = this.getAzureAdPagingWrapper().fetchAppropriateAmountOfResults(graphGroupsList, query.getStartIndex(), query.getMaxResults());
        return this.restEntityMapper.mapGroups(results, query.getReturnType(), this.getDirectoryId());
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.NAME);
        String externalIdOrName = StringUtils.isEmpty((CharSequence)this.getAlternativeUsernameAttribute()) ? username : this.fetchExternalIdOfUser(username).orElseThrow(() -> new OperationFailedException((Throwable)new UserNotFoundException(username)));
        Optional<DirectoryObject> maybeGroup = this.getAzureAdPagingWrapper().pageForElement(this.getRestClient().getDirectParentsOfUser(externalIdOrName, select), this.withGroup(groupName));
        return maybeGroup.isPresent();
    }

    private Predicate<DirectoryObject> withGroup(String groupName) {
        return g -> MembershipFilterUtil.isGroup(g) && g.getDisplayName().equals(groupName);
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        String groupExternalId = this.fetchExternalIdOfGroup(childGroup).orElseThrow(() -> new OperationFailedException((Throwable)new GroupNotFoundException(childGroup)));
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.NAME);
        Optional<DirectoryObject> maybeGroup = this.getAzureAdPagingWrapper().pageForElement(this.getRestClient().getDirectParentsOfGroup(groupExternalId, select), this.withGroup(parentGroup));
        return maybeGroup.isPresent();
    }

    @Nonnull
    public BoundedCount countDirectMembersOfGroup(String groupName, int querySizeHint) throws OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException, MembershipAlreadyExistsException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, ReadOnlyGroupException, OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nonnull
    public <T> List<T> searchGroupRelationships(MembershipQuery<T> query) throws OperationFailedException {
        String alternativeUsernameAttribute;
        Preconditions.checkArgument((query.getSearchRestriction() == NullRestrictionImpl.INSTANCE ? 1 : 0) != 0, (Object)"Azure AD membership queries do not support search restrictions.");
        if (query.getEntityToReturn() == EntityDescriptor.group() && query.getEntityToMatch() == EntityDescriptor.group() && !this.supportsNestedGroups) {
            return Collections.emptyList();
        }
        if (query.getReturnType() == String.class && query.getEntityToReturn().equals((Object)EntityDescriptor.user()) && StringUtils.isNotBlank((CharSequence)(alternativeUsernameAttribute = this.getAlternativeUsernameAttribute()))) {
            return this.searchGroupRelationships(query.withReturnType(User.class)).stream().map(Principal::getName).collect(Collectors.toList());
        }
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(query.getEntityToReturn(), query.getReturnType());
        if (query.isFindChildren()) {
            Preconditions.checkArgument((query.getEntityToMatch() == EntityDescriptor.group() ? 1 : 0) != 0, (Object)"Cannot search for children of entities other than groups");
            return this.fetchAllResults(query, this::fetchExternalIdOfGroup, id -> this.getRestClient().getDirectChildrenOfGroup(id, select));
        }
        Preconditions.checkArgument((query.getEntityToReturn() == EntityDescriptor.group() ? 1 : 0) != 0, (Object)"Cannot search for parents of other types than groups");
        if (query.getEntityToMatch() == EntityDescriptor.user()) {
            ExternalIdResolver resolver = StringUtils.isEmpty((CharSequence)this.getAlternativeUsernameAttribute()) ? Optional::of : this::fetchExternalIdOfUser;
            return this.fetchAllResults(query, resolver, id -> this.getRestClient().getDirectParentsOfUser(id, select));
        }
        if (query.getEntityToMatch() == EntityDescriptor.group()) {
            return this.fetchAllResults(query, this::fetchExternalIdOfGroup, id -> this.getRestClient().getDirectParentsOfGroup(id, select));
        }
        throw new IllegalArgumentException("Unsupported entity type " + query.getEntityToMatch());
    }

    private <T> List<T> fetchAllResults(MembershipQuery<T> query, ExternalIdResolver idResolver, PageProvider provider) throws OperationFailedException {
        String alternativeUsernameAttribute = this.getAlternativeUsernameAttribute();
        Predicate<DirectoryObject> filter = this.getDirectoryObjectFilter(query);
        ResultsAggregator aggregator = ResultsAggregators.with(query);
        for (String name : query.getEntityNamesToMatch()) {
            Optional<String> externalId = idResolver.getExternalId(name);
            if (!externalId.isPresent()) continue;
            List<DirectoryObject> results = this.getAzureAdPagingWrapper().fetchAllMatchingResults(provider.getPage(externalId.get()), filter);
            aggregator.addAll(this.restEntityMapper.mapDirectoryObjects(results, query.getReturnType(), this.directoryId, alternativeUsernameAttribute));
        }
        return aggregator.constrainResults();
    }

    public void testConnection() throws OperationFailedException {
        GraphQuery graphQuery = this.convert(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(1));
        this.azureAdRestClient.get().searchUsers(graphQuery);
    }

    public boolean supportsInactiveAccounts() {
        return true;
    }

    public boolean supportsNestedGroups() {
        return this.supportsNestedGroups;
    }

    public boolean supportsPasswordExpiration() {
        return false;
    }

    public boolean supportsSettingEncryptedCredential() {
        return false;
    }

    public boolean isRolesDisabled() {
        return true;
    }

    @Nonnull
    public Iterable<Membership> getMemberships() throws OperationFailedException {
        Iterator<Membership> iterator = this.createMembershipHelper().membershipIterator();
        return () -> iterator;
    }

    @Nonnull
    public RemoteDirectory getAuthoritativeDirectory() {
        return this;
    }

    public void expireAllPasswords() throws OperationFailedException {
        throw new OperationNotSupportedException(NOT_IMPLEMENTED);
    }

    @Nullable
    public AvatarReference getUserAvatarByName(String username, int sizeHint) {
        return null;
    }

    public Optional<Set<String>> getLocallyFilteredGroupNames() {
        return Optional.of(this.getGroupsNamesToFilter()).filter(filter -> !filter.isEmpty() && this.isGroupFilteringEnabled());
    }

    public boolean isGroupFilteringEnabled() {
        return this.dcLicenseChecker.isDcLicense() && Boolean.parseBoolean(this.getValue(GROUP_FILTERING_ENABLED_ATTRIBUTE));
    }

    public Set<String> getGroupsNamesToFilter() {
        String groupsToFilterAttribute = this.getValue(FILTERED_GROUPS_ATTRIBUTE);
        return AzureGroupFilterProcessor.getGroupNames(groupsToFilterAttribute);
    }

    public List<GroupWithAttributes> getFilteredGroups() throws OperationFailedException {
        ImmutableList groupNamesToFilter = ImmutableList.copyOf(this.getGroupsNamesToFilter());
        NullRestriction searchRestriction = groupNamesToFilter.isEmpty() ? NullRestriction.INSTANCE : Restriction.on((Property)GroupTermKeys.NAME).exactlyMatchingAny((Collection)groupNamesToFilter);
        ImmutableList groupsFromAzureAd = ImmutableList.copyOf(this.searchGroups(QueryBuilder.queryFor(GroupWithAttributes.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)searchRestriction).returningAtMost(-1)));
        this.logNotExistingGroupNames((ImmutableList<String>)groupNamesToFilter, (ImmutableList<GroupWithAttributes>)groupsFromAzureAd);
        return groupsFromAzureAd;
    }

    private void logNotExistingGroupNames(ImmutableList<String> groupNamesToFilter, ImmutableList<GroupWithAttributes> groupsFromAzureAd) {
        List groupNamesFromAzureAd = groupsFromAzureAd.stream().map(DirectoryEntity::getName).collect(Collectors.toList());
        Set groupsNotFoundInAzureAd = groupNamesToFilter.stream().filter(IdentifierUtils.containsIdentifierPredicate(groupNamesFromAzureAd).negate()).collect(Collectors.toSet());
        if (!groupsNotFoundInAzureAd.isEmpty()) {
            logger.warn("Non existent Group(s) to filter out in Azure AD: {}", groupsNotFoundInAzureAd);
        }
    }

    public DeltaQueryResult<UserWithAttributes> performUsersDeltaQuery() throws OperationFailedException {
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.user(), FetchMode.DELTA_QUERY);
        GraphDeltaQueryResult<GraphDeltaQueryUser> results = this.getAzureAdPagingWrapper().fetchAllDeltaQueryResults(this.getRestClient().performUsersDeltaQuery(select));
        return this.restEntityMapper.mapDeltaQueryUsers(results, this.getDirectoryId(), this.getAlternativeUsernameAttribute());
    }

    public DeltaQueryResult<GroupWithMembershipChanges> performGroupsDeltaQuery() throws OperationFailedException {
        ODataExpand expand = this.graphQueryTranslator.resolveAzureAdNavigationPropertiesForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.DELTA_QUERY);
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.DELTA_QUERY);
        GraphDeltaQueryResult<GraphDeltaQueryGroup> results = this.getAzureAdPagingWrapper().fetchAllDeltaQueryResults(this.getRestClient().performGroupsDeltaQuery(select, expand));
        return this.restEntityMapper.mapDeltaQueryGroups(results, this.getDirectoryId());
    }

    public DeltaQueryResult<GroupWithMembershipChanges> fetchGroupChanges(String syncToken) throws OperationFailedException {
        GraphDeltaQueryResult<GraphDeltaQueryGroup> groups = this.getAzureAdPagingWrapper().fetchAllDeltaQueryResults(this.getRestClient().performGroupsDeltaQuery(new MicrosoftGraphDeltaToken(syncToken)));
        return this.restEntityMapper.mapDeltaQueryGroups(groups, this.getDirectoryId());
    }

    public DeltaQueryResult<UserWithAttributes> fetchUserChanges(String syncToken) throws OperationFailedException {
        GraphDeltaQueryResult<GraphDeltaQueryUser> users = this.getAzureAdPagingWrapper().fetchAllDeltaQueryResults(this.getRestClient().performUsersDeltaQuery(new MicrosoftGraphDeltaToken(syncToken)));
        return this.restEntityMapper.mapDeltaQueryUsers(users, this.getDirectoryId(), this.getAlternativeUsernameAttribute());
    }

    private Optional<String> fetchExternalIdOfGroup(String groupName) throws OperationFailedException {
        ODataFilter filter = this.graphQueryTranslator.translateSearchRestriction(EntityDescriptor.group(), (SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).exactlyMatching((Object)groupName), null);
        ODataSelect select = this.graphQueryTranslator.resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor.group(), FetchMode.ID);
        GraphGroupList groupList = this.getRestClient().searchGroups(new GraphQuery(filter, select, 0, new ODataTop(2)));
        return this.getOnlyElementIfPresent("group", groupName, groupList).map(GraphGroup::getId);
    }

    private Optional<String> fetchExternalIdOfUser(String userName) throws OperationFailedException {
        try {
            return Optional.of(this.findUserByName(userName).getExternalId());
        }
        catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }

    private <T> Optional<T> getOnlyElementIfPresent(String entityType, String name, PageableGraphList<T> pageableGraphList) {
        List<T> results = pageableGraphList.getEntries();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        if (results.size() > 1) {
            throw new IllegalStateException(String.format("More than one %s with name %s exists", entityType, name));
        }
        return Optional.of(Iterables.getOnlyElement(results));
    }

    public boolean supportsDeltaQueryApi() {
        return this.getRestClient().supportsDeltaQuery();
    }

    private <T> Predicate<DirectoryObject> getDirectoryObjectFilter(MembershipQuery<T> query) {
        if (query.getEntityToReturn() == EntityDescriptor.user()) {
            return MembershipFilterUtil::isUser;
        }
        if (query.getEntityToReturn() == EntityDescriptor.group()) {
            return MembershipFilterUtil::isGroup;
        }
        throw new IllegalStateException("Unsupported entity type " + query.getEntityToReturn());
    }

    private <T extends Exception> void validateSingleResult(PageableGraphList<?> results, Supplier<T> noResultsFoundExceptionSupplier) throws T {
        this.getSingleResult(results.getEntries(), noResultsFoundExceptionSupplier);
    }

    private <T, E extends Exception> T getSingleResult(List<T> results, Supplier<E> noResultsFoundExceptionSupplier) throws E {
        int amountOfResults = results.size();
        if (amountOfResults == 0) {
            throw (Exception)noResultsFoundExceptionSupplier.get();
        }
        if (amountOfResults > 1) {
            throw new IllegalStateException(String.format("Expected one result, found %d. Please verify that there are noentities with duplicate names in the directory", amountOfResults));
        }
        return (T)Iterables.getOnlyElement(results);
    }

    public AzureMembershipHelper createMembershipHelper() {
        return new AzureMembershipHelper(this.getRestClient(), this.getAzureAdPagingWrapper(), this.graphQueryTranslator, this.restEntityMapper, this);
    }

    @VisibleForTesting
    public AzureAdRestClient getRestClient() {
        return this.azureAdRestClient.get();
    }

    private UserCredentialVerifier getUserCredentialVerifier() {
        return this.userCredentialVerifier.get();
    }

    private AzureAdPagingWrapper getAzureAdPagingWrapper() {
        return this.azureAdPagingWrapper.get();
    }

    public MicrosoftGraphQueryTranslator getTranslator() {
        return this.graphQueryTranslator;
    }

    public boolean isLocalGroupsEnabled() {
        return this.localGroupsEnabled;
    }

    public String getAlternativeUsernameAttribute() {
        return this.dcLicenseChecker.isDcLicense() ? this.getValue(ALTERNATIVE_USERNAME_ATTRIBUTE) : null;
    }

    private GraphQuery convert(EntityQuery<?> query) {
        String alternativeAttribute = this.getAlternativeUsernameAttribute();
        return this.graphQueryTranslator.convert(query, StringUtils.isNotEmpty((CharSequence)alternativeAttribute) ? alternativeAttribute : "userPrincipalName");
    }

    static interface PageProvider {
        public GraphDirectoryObjectList getPage(String var1) throws OperationFailedException;
    }

    static interface ExternalIdResolver {
        public Optional<String> getExternalId(String var1) throws OperationFailedException;
    }
}

