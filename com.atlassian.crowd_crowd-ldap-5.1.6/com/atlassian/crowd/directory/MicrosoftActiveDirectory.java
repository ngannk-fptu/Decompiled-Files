/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.avatar.AvatarReference$BlobAvatar
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyImpl
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.crowd.util.PasswordHelper
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.core.DirContextAdapter
 *  org.springframework.ldap.core.DirContextProcessor
 *  org.springframework.ldap.filter.AndFilter
 *  org.springframework.ldap.filter.EqualsFilter
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.GreaterThanOrEqualsFilter
 *  org.springframework.ldap.filter.HardcodedFilter
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.NamedLdapEntity;
import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.SpringLdapTemplateWrapper;
import com.atlassian.crowd.directory.ldap.control.DeletedResultsControl;
import com.atlassian.crowd.directory.ldap.credential.ActiveDirectoryCredentialEncoder;
import com.atlassian.crowd.directory.ldap.credential.EnforceUnencryptedCredentialEncoder;
import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.ExternalIdContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.JpegPhotoContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.NameWithExternalIdContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.TombstoneContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapperConfig;
import com.atlassian.crowd.directory.ldap.mapper.attribute.ActiveDirectoryUserContextMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.ObjectGUIDMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.ObjectSIDMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.PrimaryGroupIdMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.SIDUtils;
import com.atlassian.crowd.directory.ldap.mapper.attribute.USNChangedMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.UserAccountControlMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.UserAccountControlUtil;
import com.atlassian.crowd.directory.ldap.mapper.attribute.group.RFC4519MemberDnRangeOffsetMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.group.RFC4519MemberDnRangedMapper;
import com.atlassian.crowd.directory.ldap.name.GenericConverter;
import com.atlassian.crowd.directory.ldap.util.ActiveDirectoryExpirationUtils;
import com.atlassian.crowd.directory.ldap.util.GuidHelper;
import com.atlassian.crowd.directory.ldap.util.IncrementalAttributeMapper;
import com.atlassian.crowd.directory.ldap.util.ListAttributeValueProcessor;
import com.atlassian.crowd.directory.ldap.util.RangeOption;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.model.Tombstone;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.ldap.ActiveDirectoryQueryTranslaterImpl;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.crowd.util.PasswordHelper;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;

public class MicrosoftActiveDirectory
extends RFC4519Directory {
    private static final Logger logger = LoggerFactory.getLogger(MicrosoftActiveDirectory.class);
    public static final int UF_ACCOUNTDISABLE = 2;
    private static final int UF_PASSWD_NOTREQD = 32;
    private static final int UF_NORMAL_ACCOUNT = 512;
    private static final int UF_PASSWORD_EXPIRED = 0x800000;
    private static final String AD_USER_ACCOUNT_CONTROL = "userAccountControl";
    private static final String AD_SAM_ACCOUNT_NAME = "samAccountName";
    private static final String AD_HIGHEST_COMMITTED_USN = "highestCommittedUSN";
    private static final String AD_IS_DELETED = "isDeleted";
    private static final String AD_OBJECT_CLASS = "objectClass";
    private static final String DELETED_OBJECTS_DN_ADDITION = "CN=Deleted Objects";
    private static final String ROOT_DOMAIN_NAMING_CONTEXT = "rootDomainNamingContext";
    private static final String GROUP_TYPE_NAME = "groupType";
    private static final String GROUP_TYPE_VALUE = "2";
    public static final PropertyImpl<String> OBJECT_SID = new PropertyImpl("objectSid", String.class);
    public static final String AD_DS_SERVICE_NAME = "dsServiceName";
    public static final String AD_INVOCATION_ID = "invocationId";
    private final LDAPCredentialEncoder credentialEncoder;

    public MicrosoftActiveDirectory(ActiveDirectoryQueryTranslaterImpl activeDirectoryQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordHelper passwordHelper, LdapContextSourceProvider ldapContextSourceProvider) {
        super(activeDirectoryQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
        EnforceUnencryptedCredentialEncoder baseEncoder = new EnforceUnencryptedCredentialEncoder(passwordHelper);
        this.credentialEncoder = new ActiveDirectoryCredentialEncoder(baseEncoder);
    }

    public static String getStaticDirectoryType() {
        return "Microsoft Active Directory";
    }

    public String getDescriptiveName() {
        return MicrosoftActiveDirectory.getStaticDirectoryType();
    }

    @Override
    public void removeGroup(String name) throws GroupNotFoundException, OperationFailedException {
        String primaryGroupRid;
        Iterable<LdapName> users;
        Validate.notEmpty((CharSequence)name, (String)"name argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(name);
        if (this.isPrimaryGroupSupportEnabled() && !Iterables.isEmpty(users = this.findUserMembersNamesOfGroupViaPrimaryGroupId(primaryGroupRid = SIDUtils.getLastRidFromSid(group.getValue("objectSid")), 0, 1))) {
            throw new OperationFailedException("Cannot remove group '" + group.getName() + "' because it is the primary group of some user(s), including '" + ((LdapName)Iterables.get(users, (int)0)).toString() + "'");
        }
        try {
            ((SpringLdapTemplateWrapper)this.ldapTemplate.get()).unbind(this.asLdapGroupName(group.getDn(), name));
        }
        catch (NamingException ex) {
            throw new OperationFailedException((Throwable)ex);
        }
    }

    @Override
    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        try {
            LDAPGroupWithAttributes group = this.findGroupByName(groupName);
            LDAPUserWithAttributes user = this.findUserByName(username);
            return this.isDnDirectGroupMember(user.getDn(), group) || this.isUserMemberOfPrimaryGroup(user, group);
        }
        catch (UserNotFoundException e) {
            return false;
        }
        catch (GroupNotFoundException e) {
            return false;
        }
    }

    @Override
    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, OperationFailedException, UserNotFoundException, MembershipAlreadyExistsException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(groupName);
        LDAPUserWithAttributes user = this.findUserByName(username);
        if (this.isDnDirectGroupMember(user.getDn(), group) || this.isUserMemberOfPrimaryGroup(user, group)) {
            throw new MembershipAlreadyExistsException(this.getDirectoryId(), username, groupName);
        }
        this.addDnToGroup(user.getDn(), group);
    }

    @Override
    public void removeUserFromGroup(String username, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException, OperationFailedException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(groupName);
        LDAPUserWithAttributes user = this.findUserByName(username);
        if (!this.isDnDirectGroupMember(user.getDn(), group)) {
            if (this.isUserMemberOfPrimaryGroup(user, group)) {
                throw new OperationFailedException("Cannot remove user '" + user.getName() + "' from group '" + group.getName() + "' because it is the primary group of the user");
            }
            throw new MembershipNotFoundException(username, groupName);
        }
        this.removeDnFromGroup(user.getDn(), group);
    }

    private String findGroupNameBySID(String sid) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)sid, (String)"SID argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on(OBJECT_SID).exactlyMatching((Object)sid)).returningAtMost(1);
        ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getGroupNameAttribute());
        try {
            return ((NamedLdapEntity)Iterables.getOnlyElement(this.searchGroupObjects(query, mapper))).getName();
        }
        catch (NoSuchElementException e) {
            throw new GroupNotFoundException("objectId = " + sid);
        }
    }

    public Collection<LDAPGroupWithAttributes> searchGroupsBySids(Set<String> groupSids) throws OperationFailedException {
        return this.searchGroupsByAttribute(groupSids, values -> this.prepareOrFilterForGroupProperty(OBJECT_SID.getPropertyName(), (List<String>)values));
    }

    private LDAPGroupWithAttributes findGroupWithAttributesBySID(String sid) throws GroupNotFoundException, OperationFailedException {
        Validate.notNull((Object)sid, (String)"SID argument cannot be null", (Object[])new Object[0]);
        EntityQuery query = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on(OBJECT_SID).exactlyMatching((Object)sid)).returningAtMost(1);
        try {
            return (LDAPGroupWithAttributes)Iterables.getOnlyElement(this.searchGroupObjects(query, this.getGroupContextMapper(GroupType.GROUP, true)));
        }
        catch (NoSuchElementException e) {
            throw new GroupNotFoundException("objectId = " + sid);
        }
    }

    @Override
    protected List<? extends LDAPGroupWithAttributes> findGroupMemberships(MembershipQuery<? extends LDAPGroupWithAttributes> query) throws OperationFailedException {
        List<? extends LDAPGroupWithAttributes> memberships = super.findGroupMemberships(query);
        if (this.isPrimaryGroupSupportEnabled() && query.getEntityToMatch().getEntityType() == Entity.USER && !MicrosoftActiveDirectory.isResultPageFull(memberships, query.getMaxResults())) {
            try {
                LDAPUserWithAttributes user = this.findUserWithAttributesByName(query.getEntityNameToMatch());
                Optional<String> primaryGroupSidOrEmpty = this.getPrimaryGroupSIDOfUser(user);
                if (primaryGroupSidOrEmpty.isPresent()) {
                    LDAPGroupWithAttributes primaryGroup = this.findGroupWithAttributesBySID(primaryGroupSidOrEmpty.get());
                    ImmutableList augmentedMemberships = ImmutableList.builder().addAll(memberships).add((Object)primaryGroup).build();
                    return SearchResultsUtil.constrainResults((List)augmentedMemberships, (int)0, (int)query.getMaxResults());
                }
                return memberships;
            }
            catch (UserNotFoundException e) {
                return memberships;
            }
            catch (GroupNotFoundException e) {
                logger.debug("Primary group of user '{}' is not under the base DN", (Object)query.getEntityNameToMatch());
                return memberships;
            }
        }
        return memberships;
    }

    @Override
    protected Iterable<String> findGroupMembershipNames(MembershipQuery<String> query) throws OperationFailedException {
        ImmutableList membershipNames = ImmutableList.copyOf(super.findGroupMembershipNames(query));
        if (this.isPrimaryGroupSupportEnabled() && query.getEntityToMatch().getEntityType() == Entity.USER && !MicrosoftActiveDirectory.isResultPageFull(membershipNames, query.getMaxResults())) {
            try {
                LDAPUserWithAttributes user = this.findUserWithAttributesByName(query.getEntityNameToMatch());
                Optional<String> primaryGroupSidOrEmpty = this.getPrimaryGroupSIDOfUser(user);
                if (primaryGroupSidOrEmpty.isPresent()) {
                    String primaryGroupName = this.findGroupNameBySID(primaryGroupSidOrEmpty.get());
                    ImmutableList augmentedMembershipNames = ImmutableList.builder().addAll((Iterable)membershipNames).add((Object)primaryGroupName).build();
                    return SearchResultsUtil.constrainResults((List)augmentedMembershipNames, (int)0, (int)query.getMaxResults());
                }
                return membershipNames;
            }
            catch (UserNotFoundException e) {
                return membershipNames;
            }
            catch (GroupNotFoundException e) {
                logger.debug("Primary group of user '{}' is not under the base DN", (Object)query.getEntityNameToMatch());
                return membershipNames;
            }
        }
        return membershipNames;
    }

    private AndFilter getUserByPrimaryGroupRidFilter(String primaryGroupRid) {
        AndFilter filter = new AndFilter();
        filter.and((Filter)new HardcodedFilter(this.ldapPropertiesMapper.getUserFilter()));
        filter.and((Filter)new EqualsFilter("primaryGroupId", primaryGroupRid));
        return filter;
    }

    private Iterable<LdapName> findUserMembersNamesOfGroupViaPrimaryGroupId(String primaryGroupRid, int startIndex, int maxResults) throws OperationFailedException {
        AndFilter filter = this.getUserByPrimaryGroupRidFilter(primaryGroupRid);
        ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getUserNameAttribute());
        if (logger.isDebugEnabled()) {
            logger.debug("Executing search at DN: <" + this.searchDN.getUser() + "> with filter: <" + filter.encode() + ">");
        }
        return NamedLdapEntity.dnsOf(this.searchEntities(this.searchDN.getUser(), filter.encode(), mapper, startIndex, maxResults));
    }

    private Iterable<LDAPUserWithAttributes> findUserMembersOfGroupViaPrimaryGroupId(String primaryGroupRid, int startIndex, int maxResults) throws OperationFailedException {
        AndFilter filter = this.getUserByPrimaryGroupRidFilter(primaryGroupRid);
        if (logger.isDebugEnabled()) {
            logger.debug("Executing search at DN: <" + this.searchDN.getUser() + "> with filter: <" + filter.encode() + ">");
        }
        return MicrosoftActiveDirectory.toGenericIterable(this.searchEntities(this.searchDN.getUser(), filter.encode(), this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()), startIndex, maxResults));
    }

    @Override
    protected List<LDAPUserWithAttributes> findUserMembersOfGroupViaMemberDN(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        List<LDAPUserWithAttributes> users = super.findUserMembersOfGroupViaMemberDN(groupName, groupType, startIndex, maxResults);
        return this.augmentUserMembersOfGroupWithPrimaryGroupMembers(groupName, users, startIndex, maxResults);
    }

    @Override
    protected Iterable<LDAPUserWithAttributes> findUserMembersOfGroupViaMemberOf(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        ImmutableList users = ImmutableList.copyOf(super.findUserMembersOfGroupViaMemberOf(groupName, groupType, startIndex, maxResults));
        return this.augmentUserMembersOfGroupWithPrimaryGroupMembers(groupName, (List<LDAPUserWithAttributes>)users, startIndex, maxResults);
    }

    @Override
    protected Iterable<LdapName> findAdditionalDirectMembers(LdapName groupDn, Supplier<Optional<LDAPGroupWithAttributes>> groupSupplier) throws OperationFailedException {
        Optional<LDAPGroupWithAttributes> maybeGroup;
        if (this.isPrimaryGroupSupportEnabled() && (maybeGroup = groupSupplier.get()).isPresent()) {
            String primaryGroupRid = SIDUtils.getLastRidFromSid(maybeGroup.get().getValue("objectSid"));
            Iterable<LdapName> additionalUsers = this.findUserMembersNamesOfGroupViaPrimaryGroupId(primaryGroupRid, 0, -1);
            return additionalUsers;
        }
        return ImmutableList.of();
    }

    private List<LDAPUserWithAttributes> augmentUserMembersOfGroupWithPrimaryGroupMembers(String groupName, List<LDAPUserWithAttributes> users, int startIndex, int maxResults) throws OperationFailedException {
        if (this.isPrimaryGroupSupportEnabled() && !MicrosoftActiveDirectory.isResultPageFull(users, maxResults)) {
            try {
                LDAPGroupWithAttributes group = this.findGroupWithAttributesByName(groupName);
                String primaryGroupRid = SIDUtils.getLastRidFromSid(group.getValue("objectSid"));
                Iterable<LDAPUserWithAttributes> additionalUsers = this.findUserMembersOfGroupViaPrimaryGroupId(primaryGroupRid, startIndex, maxResults);
                ImmutableList augmentedUsers = ImmutableList.builder().addAll(users).addAll(additionalUsers).build();
                return SearchResultsUtil.constrainResults((List)augmentedUsers, (int)0, (int)maxResults);
            }
            catch (GroupNotFoundException e) {
                return ImmutableList.copyOf(users);
            }
        }
        return ImmutableList.copyOf(users);
    }

    @VisibleForTesting
    static boolean isResultPageFull(List<?> results, int maxResults) {
        if (maxResults == -1) {
            return false;
        }
        return results.size() == maxResults;
    }

    @VisibleForTesting
    boolean isUserMemberOfPrimaryGroup(LDAPUserWithAttributes user, LDAPGroupWithAttributes group) {
        if (!this.isPrimaryGroupSupportEnabled()) {
            return false;
        }
        Optional<String> primaryGroupSidOrEmpty = this.getPrimaryGroupSIDOfUser(user);
        if (primaryGroupSidOrEmpty.isPresent()) {
            String primaryGroupSid = primaryGroupSidOrEmpty.get();
            String groupSID = group.getValue("objectSid");
            return primaryGroupSid.equals(groupSID);
        }
        return false;
    }

    public Optional<String> getPrimaryGroupSIDOfUser(LDAPUserWithAttributes user) {
        String primaryGroupRID = user.getValue("primaryGroupId");
        if (primaryGroupRID == null) {
            return Optional.empty();
        }
        String userSID = user.getValue("objectSid");
        return Optional.of(SIDUtils.substituteLastRidInSid(userSID, primaryGroupRID));
    }

    @VisibleForTesting
    boolean isPrimaryGroupSupportEnabled() {
        return this.getAttributeAsBoolean("ldap.activedirectory.use_primary_groups", false);
    }

    @Override
    protected String getInitialGroupMemberDN() {
        return null;
    }

    @Override
    protected LDAPCredentialEncoder getCredentialEncoder() {
        return this.credentialEncoder;
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        attributes.put(AD_SAM_ACCOUNT_NAME, user.getName());
        String accountStatus = null;
        accountStatus = user.isActive() ? Integer.toString(0x800220) : Integer.toString(0x800222);
        attributes.put(new BasicAttribute(AD_USER_ACCOUNT_CONTROL, accountStatus));
    }

    @Override
    protected void getNewGroupDirectorySpecificAttributes(Group group, Attributes attributes) {
        attributes.put(GROUP_TYPE_NAME, GROUP_TYPE_VALUE);
    }

    @Override
    protected List<AttributeMapper> getCustomUserAttributeMappers(UserContextMapperConfig config) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)new ObjectGUIDMapper());
        builder.addAll(super.getCustomUserAttributeMappers(config));
        builder.add((Object)new USNChangedMapper());
        builder.add((Object)new ObjectSIDMapper());
        builder.add((Object)new PrimaryGroupIdMapper());
        builder.add((Object)new UserAccountControlMapper());
        return builder.build();
    }

    @Override
    protected List<AttributeMapper> getCustomGroupAttributeMappers() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(super.getCustomGroupAttributeMappers());
        builder.add((Object)new ObjectGUIDMapper());
        builder.add((Object)new USNChangedMapper());
        builder.add((Object)new ObjectSIDMapper());
        return builder.build();
    }

    @Override
    protected List<AttributeMapper> getRequiredCustomGroupAttributeMappers() {
        return Collections.singletonList(new ObjectGUIDMapper());
    }

    @Override
    protected List<AttributeMapper> getMemberDnMappers() {
        return Arrays.asList(new RFC4519MemberDnRangedMapper(this.ldapPropertiesMapper.getGroupMemberAttribute(), this.ldapPropertiesMapper.isRelaxedDnStandardisation()), new RFC4519MemberDnRangeOffsetMapper(this.ldapPropertiesMapper.getGroupMemberAttribute()));
    }

    @Override
    protected List<LDAPGroupWithAttributes> postprocessGroups(List<LDAPGroupWithAttributes> groups) throws OperationFailedException {
        ArrayList result = Lists.newArrayList();
        for (LDAPGroupWithAttributes group : groups) {
            if (group.getValue("memberRangeStart") != null) {
                ListAttributeValueProcessor valueAggregator = new ListAttributeValueProcessor();
                String rangeStart = group.getValue("memberRangeStart");
                RangeOption range = new RangeOption(Integer.valueOf(rangeStart));
                IncrementalAttributeMapper incrementalAttributeMapper = new IncrementalAttributeMapper(this.ldapPropertiesMapper.getGroupMemberAttribute(), valueAggregator, range);
                LdapName groupDnName = group.getLdapName();
                while (incrementalAttributeMapper.hasMore()) {
                    ((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(groupDnName, incrementalAttributeMapper.getAttributesArray(), incrementalAttributeMapper);
                }
                Set<String> initialMembers = group.getValues("memberDNs");
                HashSet<String> standardDNs = new HashSet<String>(initialMembers.size() + valueAggregator.getValues().size());
                standardDNs.addAll(initialMembers);
                for (String memberDN : valueAggregator.getValues()) {
                    String dn = this.standardiseDN(memberDN);
                    standardDNs.add(dn);
                }
                GroupTemplateWithAttributes groupTemplate = new GroupTemplateWithAttributes((GroupWithAttributes)group);
                groupTemplate.setAttribute("memberDNs", standardDNs);
                groupTemplate.removeAttribute("memberRangeStart");
                result.add(new LDAPGroupWithAttributes(group.getDn(), groupTemplate));
                continue;
            }
            result.add(group);
        }
        return result;
    }

    @Override
    protected Map<String, Object> getBaseEnvironmentProperties() {
        Map<String, Object> env = super.getBaseEnvironmentProperties();
        env.put("java.naming.ldap.attributes.binary", Joiner.on((char)' ').join((Iterable)ImmutableList.of((Object)"objectGUID", (Object)"objectSid", (Object)AD_INVOCATION_ID)));
        return env;
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input, the values are sourced from AD metadata")
    public String fetchInvocationId() throws OperationFailedException {
        String dsServiceName = ((DirContextAdapter)((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(GenericConverter.emptyLdapName())).getStringAttribute(AD_DS_SERVICE_NAME);
        try {
            Object invocationIdAttribute = ((DirContextAdapter)((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(new LdapName(dsServiceName))).getObjectAttribute(AD_INVOCATION_ID);
            if (invocationIdAttribute instanceof byte[]) {
                return GuidHelper.convertToADGUID(GuidHelper.getGUIDAsString((byte[])invocationIdAttribute));
            }
            logger.warn("Returning null invocation id because value {} wasn't a byte array ", invocationIdAttribute);
            return null;
        }
        catch (InvalidNameException e) {
            logger.error("Active directory returned invalid service node name: {}", (Object)dsServiceName);
            throw new OperationFailedException((Throwable)e);
        }
    }

    public long fetchHighestCommittedUSN() throws OperationFailedException {
        try {
            String highestCommittedUSN = ((DirContextAdapter)((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(GenericConverter.emptyLdapName())).getStringAttribute(AD_HIGHEST_COMMITTED_USN);
            if (highestCommittedUSN != null) {
                try {
                    long usn = Long.parseLong(highestCommittedUSN);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Fetched highest committed USN of " + usn);
                    }
                    return usn;
                }
                catch (NumberFormatException e) {
                    throw new OperationFailedException("Error parsing highestCommittedUSN as a number", (Throwable)e);
                }
            }
            throw new OperationFailedException("No highestCommittedUSN attribute found for AD root");
        }
        catch (NamingException e) {
            throw new OperationFailedException("Error looking up attributes for highestCommittedUSN", (Throwable)e);
        }
    }

    public List<LDAPUserWithAttributes> findAddedOrUpdatedUsersSince(long usnChange) throws OperationFailedException {
        return this.findAddedOrUpdatedObjectsSince(usnChange, this.searchDN.getUser(), this.ldapPropertiesMapper.getUserFilter(), this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().withMemberOfAttribute().build()));
    }

    public List<LDAPGroupWithAttributes> findAddedOrUpdatedGroupsSince(long usnChanged) throws OperationFailedException {
        return this.findAddedOrUpdatedObjectsSince(usnChanged, this.searchDN.getGroup(), this.ldapPropertiesMapper.getGroupFilter(), this.getGroupContextMapper(GroupType.GROUP, true));
    }

    public List<Tombstone> findUserTombstonesSince(long usnChange) throws OperationFailedException {
        return this.findTombstonesSince(usnChange, this.searchDN.getUser(), this.ldapPropertiesMapper.getUserObjectClass());
    }

    public Set<String> findAllUserGuids() throws OperationFailedException {
        ExternalIdContextMapper contextMapper = new ExternalIdContextMapper(this.ldapPropertiesMapper.getExternalIdAttribute());
        String filter = this.ldapPropertiesMapper.getUserFilter();
        logger.debug("Performing all users objectGUID search: DN = {} , filter = {}", (Object)this.searchDN.getUser(), (Object)filter);
        return ImmutableSet.copyOf(this.searchEntities(this.searchDN.getUser(), filter, contextMapper, 0, -1));
    }

    public Set<String> findAllGroupGuids() throws OperationFailedException {
        ExternalIdContextMapper contextMapper = new ExternalIdContextMapper(this.ldapPropertiesMapper.getGroupExternalIdAttribute());
        String filter = this.ldapPropertiesMapper.getGroupFilter();
        logger.debug("Performing all groups objectGUID search: DN = {} , filter = {}", (Object)this.searchDN.getGroup(), (Object)filter);
        return ImmutableSet.copyOf(this.searchEntities(this.searchDN.getGroup(), filter, contextMapper, 0, -1));
    }

    public Set<Pair<String, String>> findAllGroupNamesAndGuids() throws OperationFailedException {
        NameWithExternalIdContextMapper contextMapper = new NameWithExternalIdContextMapper(this.ldapPropertiesMapper.getGroupNameAttribute(), this.ldapPropertiesMapper.getGroupExternalIdAttribute());
        String filter = this.ldapPropertiesMapper.getGroupFilter();
        logger.debug("Performing all groups objectGUID search: DN = {} , filter = {}", (Object)this.searchDN.getGroup(), (Object)filter);
        return ImmutableSet.copyOf(this.searchEntities(this.searchDN.getGroup(), filter, contextMapper, 0, -1));
    }

    protected <T> List<T> findAddedOrUpdatedObjectsSince(long usnChange, Name objectBaseDN, String objectFilter, ContextMapperWithRequiredAttributes<T> contextMapper) throws OperationFailedException {
        AndFilter filter = new AndFilter();
        filter.and((Filter)new HardcodedFilter(objectFilter));
        filter.and((Filter)new GreaterThanOrEqualsFilter("uSNChanged", Long.toString(usnChange + 1L)));
        logger.debug("Performing polling search: baseDN = {} - filter = {}", (Object)objectBaseDN, (Object)filter.encode());
        return this.searchEntities(objectBaseDN, filter.encode(), contextMapper, 0, -1);
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    private Name getDeletedObjectsDN() {
        try {
            DirContextAdapter root = (DirContextAdapter)((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(new LdapName(""));
            String rootDN = root.getStringAttribute(ROOT_DOMAIN_NAMING_CONTEXT);
            String dn = DELETED_OBJECTS_DN_ADDITION + ',' + rootDN;
            return new LdapName(dn);
        }
        catch (javax.naming.NamingException e) {
            return this.searchDN.getNamingContext();
        }
    }

    protected List<Tombstone> findTombstonesSince(long usnChange, Name objectBaseDN, String objectClass) throws OperationFailedException {
        TombstoneContextMapper contextMapper = new TombstoneContextMapper();
        SearchControls searchControls = this.getSearchControls(contextMapper, 2);
        AndFilter filter = new AndFilter();
        filter.and((Filter)new EqualsFilter(AD_IS_DELETED, "TRUE"));
        filter.and((Filter)new EqualsFilter(AD_OBJECT_CLASS, objectClass));
        filter.and((Filter)new GreaterThanOrEqualsFilter("uSNChanged", Long.toString(usnChange + 1L)));
        Name deletedObjectsDN = this.getDeletedObjectsDN();
        logger.debug("Performing tombstones search: baseDN = {} - filter = {}", (Object)deletedObjectsDN, (Object)filter.encode());
        return this.searchEntitiesWithRequestControls(deletedObjectsDN, filter.encode(), contextMapper, searchControls, (DirContextProcessor)new DeletedResultsControl(), 0, -1);
    }

    @Override
    public ContextMapperWithRequiredAttributes<LDAPUserWithAttributes> getUserContextMapper(UserContextMapperConfig config) {
        ImmutableList.Builder mappers = ImmutableList.builder();
        mappers.addAll(this.getCustomUserAttributeMappers(config));
        return new ActiveDirectoryUserContextMapper(this.getDirectoryId(), this.ldapPropertiesMapper, (List<AttributeMapper>)mappers.build());
    }

    public boolean isUsersExternalIdConfigured() {
        return StringUtils.isNotBlank((CharSequence)this.ldapPropertiesMapper.getExternalIdAttribute());
    }

    public boolean isGroupExternalIdConfigured() {
        return StringUtils.isNotBlank((CharSequence)this.ldapPropertiesMapper.getGroupExternalIdAttribute());
    }

    @Override
    protected List<ModificationItem> getUserModificationItems(User userTemplate, LDAPUserWithAttributes currentUser) {
        String newValue;
        ImmutableList.Builder modificationItems = ImmutableList.builder().addAll(super.getUserModificationItems(userTemplate, currentUser));
        String currentValue = currentUser.getValue(AD_USER_ACCOUNT_CONTROL);
        ModificationItem activeModItem = MicrosoftActiveDirectory.createModificationItem(AD_USER_ACCOUNT_CONTROL, currentValue, newValue = userTemplate.isActive() ? UserAccountControlUtil.enabledUser(currentValue) : UserAccountControlUtil.disabledUser(currentValue));
        if (activeModItem != null) {
            modificationItems.add((Object)activeModItem);
        }
        return modificationItems.build();
    }

    @Override
    public boolean supportsInactiveAccounts() {
        return true;
    }

    @Override
    protected ContextMapperWithRequiredAttributes<AvatarReference.BlobAvatar> avatarMapper() {
        return new JpegPhotoContextMapper("thumbnailPhoto");
    }

    @Override
    protected void setLdapPropertiesMapperAttributes(Map<String, String> attributes) {
        HashMap<String, String> attributesToSet = new HashMap<String, String>(attributes);
        if (this.isFilteringExpiredUsers(attributesToSet)) {
            attributesToSet.put("ldap.user.filter", this.decorateUserFilterWithExpirationAttribute((String)attributesToSet.get("ldap.user.filter")));
        }
        if (Strings.isNullOrEmpty((String)attributes.get("ldap.group.external.id"))) {
            attributesToSet.put("ldap.group.external.id", "objectGUID");
        }
        this.ldapPropertiesMapper.setAttributes(Collections.unmodifiableMap(attributesToSet));
    }

    private boolean isFilteringExpiredUsers(Map<String, String> attributes) {
        return Boolean.parseBoolean(attributes.get("ldap.filter.expiredUsers"));
    }

    private String decorateUserFilterWithExpirationAttribute(String currentFilter) {
        return new AndFilter().and((Filter)new HardcodedFilter(currentFilter)).and(ActiveDirectoryExpirationUtils.notExpiredAt(new Date())).encode();
    }
}

