/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.AttributeInUseException
 *  org.springframework.ldap.NameAlreadyBoundException
 *  org.springframework.ldap.NameNotFoundException
 *  org.springframework.ldap.NamingException
 *  org.springframework.ldap.OperationNotSupportedException
 *  org.springframework.ldap.core.DirContextAdapter
 *  org.springframework.ldap.filter.AndFilter
 *  org.springframework.ldap.filter.EqualsFilter
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.HardcodedFilter
 *  org.springframework.ldap.filter.OrFilter
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.NamedLdapEntity;
import com.atlassian.crowd.directory.SpringLDAPConnector;
import com.atlassian.crowd.directory.ldap.SpringLdapTemplateWrapper;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapperConfig;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.group.RFC4519MemberDnMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.user.MemberOfOverlayMapper;
import com.atlassian.crowd.directory.rfc4519.RFC4519DirectoryMembershipsIterableBuilder;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.ldap.LDAPQuery;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.search.ldap.NullResultException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AttributeInUseException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.OperationNotSupportedException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.OrFilter;

public abstract class RFC4519Directory
extends SpringLDAPConnector {
    private static final Logger logger = LoggerFactory.getLogger(RFC4519Directory.class);
    private final LookupByDn<LDAPGroupWithAttributes> lookupGroupByDn = new LookupByDn<LDAPGroupWithAttributes>(){

        @Override
        public LDAPGroupWithAttributes lookup(LdapName groupDN) throws OperationFailedException, UserNotFoundException, GroupNotFoundException {
            return RFC4519Directory.this.findEntityByDN(groupDN.toString(), LDAPGroupWithAttributes.class);
        }
    };
    @VisibleForTesting
    final LookupByDn<String> lookupGroupNameByDn = new LookupByDn<String>(){

        @Override
        public String lookup(LdapName groupDN) throws GroupNotFoundException {
            NamedLdapEntity namedLdapEntity = ((SpringLdapTemplateWrapper)RFC4519Directory.this.ldapTemplate.get()).lookup(groupDN, NamedLdapEntity.mapperFromAttribute(RFC4519Directory.this.ldapPropertiesMapper.getGroupNameAttribute()));
            if (namedLdapEntity.getName() == null) {
                logger.debug("LDAP user does not have sufficient access to read the group {}", (Object)groupDN);
                throw new GroupNotFoundException(groupDN.toString());
            }
            return namedLdapEntity.getName();
        }
    };
    private static final Function<String, ? extends LdapName> TO_LDAP_NAME = new Function<String, LdapName>(){

        @Override
        @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="Function, not used directly on user provided data currently")
        public LdapName apply(String input) {
            try {
                return new LdapName(input);
            }
            catch (javax.naming.NamingException e) {
                throw new RuntimeException(e);
            }
        }
    };
    public static final ContextMapperWithRequiredAttributes<LdapName> DN_MAPPER = new DnMapper();

    public RFC4519Directory(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
    }

    @Override
    protected List<AttributeMapper> getCustomGroupAttributeMappers() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(super.getCustomGroupAttributeMappers());
        builder.addAll(this.getMemberDnMappers());
        return builder.build();
    }

    protected List<AttributeMapper> getMemberDnMappers() {
        return Collections.singletonList(new RFC4519MemberDnMapper(this.ldapPropertiesMapper.getGroupMemberAttribute(), this.ldapPropertiesMapper.isRelaxedDnStandardisation()));
    }

    @Override
    protected List<AttributeMapper> getCustomUserAttributeMappers(UserContextMapperConfig config) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(super.getCustomUserAttributeMappers(config));
        if (config.includeAll() && this.ldapPropertiesMapper.isUsingUserMembershipAttributeForGroupMembership() || config.includeMemberOf()) {
            builder.add((Object)new MemberOfOverlayMapper(this.ldapPropertiesMapper.getUserGroupMembershipsAttribute(), this.ldapPropertiesMapper.isRelaxedDnStandardisation()));
        }
        return builder.build();
    }

    public Collection<LDAPGroupWithAttributes> searchGroupsByDns(Set<String> groupsDn) throws OperationFailedException {
        return this.searchGroupsByAttribute(groupsDn, values -> this.prepareOrFilterForGroupProperty("distinguishedName", (List<String>)values));
    }

    protected Collection<LDAPGroupWithAttributes> searchGroupsByAttribute(Set<String> propertyValues, Function<List<String>, Filter> filterFunction) throws OperationFailedException {
        HashSet<LDAPGroupWithAttributes> results = new HashSet<LDAPGroupWithAttributes>(propertyValues.size());
        Iterable batchedPropertyValues = Iterables.partition(propertyValues, (int)1000);
        for (List propertyValuesBatch : batchedPropertyValues) {
            Filter filter = filterFunction.apply(propertyValuesBatch);
            results.addAll(this.searchEntities(this.searchDN.getGroup(), filter.encode(), this.getGroupContextMapper(GroupType.GROUP, true), 0, -1));
        }
        return results;
    }

    protected AndFilter prepareOrFilterForGroupProperty(String propertyName, List<String> propertyValues) {
        AndFilter rootFilter = new AndFilter();
        OrFilter orFilter = new OrFilter();
        rootFilter.and((Filter)new HardcodedFilter(this.ldapPropertiesMapper.getGroupFilter()));
        propertyValues.stream().map(value -> new EqualsFilter(propertyName, value)).forEach(arg_0 -> ((OrFilter)orFilter).or(arg_0));
        rootFilter.and((Filter)orFilter);
        return rootFilter;
    }

    private static Set<String> getMemberDNs(LDAPGroupWithAttributes group) {
        return (Set)ObjectUtils.defaultIfNull(group.getValues("memberDNs"), Collections.emptySet());
    }

    private static Set<String> getMemberOfs(LDAPUserWithAttributes user) {
        return (Set)ObjectUtils.defaultIfNull(user.getValues("memberOf"), Collections.emptySet());
    }

    protected boolean isDnDirectGroupMember(String memberDN, LDAPGroupWithAttributes parentGroup) {
        LdapName ldapNameToCheck = TO_LDAP_NAME.apply(memberDN);
        Set<String> parentGroupMemberDNs = RFC4519Directory.getMemberDNs(parentGroup);
        return Iterables.contains((Iterable)Iterables.transform(parentGroupMemberDNs, TO_LDAP_NAME::apply), (Object)ldapNameToCheck);
    }

    protected boolean isDirectGroupMemberOf(LDAPUserWithAttributes user, String groupDN) {
        Set<String> groupDNs = RFC4519Directory.getMemberOfs(user);
        return Iterables.contains((Iterable)Iterables.transform(groupDNs, inputDn -> this.standardiseDN((String)inputDn)), (Object)this.standardiseDN(groupDN));
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        try {
            LDAPGroupWithAttributes group = this.findGroupByName(groupName);
            LDAPUserWithAttributes user = this.findUserByName(username);
            return this.isDnDirectGroupMember(user.getDn(), group) || RFC4519Directory.getMemberDNs(group).isEmpty() && this.isDirectGroupMemberOf(user, group.getDn());
        }
        catch (UserNotFoundException e) {
            return false;
        }
        catch (GroupNotFoundException e) {
            return false;
        }
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) throws OperationFailedException {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup argument cannot be null or empty", (Object[])new Object[0]);
        try {
            LDAPGroupWithAttributes parent = this.findGroupByName(parentGroup);
            LDAPGroupWithAttributes child = this.findGroupByName(childGroup);
            return this.isDnDirectGroupMember(child.getDn(), parent);
        }
        catch (GroupNotFoundException e) {
            return false;
        }
    }

    protected void addDnToGroup(String dn, LDAPGroupWithAttributes group) throws OperationFailedException {
        try {
            ModificationItem[] mods = new ModificationItem[]{new ModificationItem(1, new BasicAttribute(this.ldapPropertiesMapper.getGroupMemberAttribute(), dn))};
            ((SpringLdapTemplateWrapper)this.ldapTemplate.get()).modifyAttributes(this.asLdapGroupName(group.getDn(), group.getName()), mods);
        }
        catch (AttributeInUseException mods) {
        }
        catch (NameAlreadyBoundException mods) {
        }
        catch (GroupNotFoundException e) {
            logger.error("Could not modify members of group with DN: " + dn, (Throwable)e);
        }
        catch (NamingException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, OperationFailedException, UserNotFoundException, MembershipAlreadyExistsException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(groupName);
        LDAPUserWithAttributes user = this.findUserByName(username);
        if (this.isDnDirectGroupMember(user.getDn(), group)) {
            throw new MembershipAlreadyExistsException(this.getDirectoryId(), username, groupName);
        }
        this.addDnToGroup(user.getDn(), group);
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, OperationFailedException, MembershipAlreadyExistsException {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes parent = this.findGroupByName(parentGroup);
        LDAPGroupWithAttributes child = this.findGroupByName(childGroup);
        if (parent.getType() != child.getType()) {
            throw new InvalidMembershipException("Cannot add group of type " + child.getType().name() + " to group of type " + parent.getType().name());
        }
        if (this.isDnDirectGroupMember(child.getDn(), parent)) {
            throw new MembershipAlreadyExistsException(this.getDirectoryId(), childGroup, parentGroup);
        }
        this.addDnToGroup(child.getDn(), parent);
    }

    protected void removeDnFromGroup(String dn, LDAPGroupWithAttributes group) throws OperationFailedException {
        try {
            ModificationItem[] mods = new ModificationItem[]{new ModificationItem(3, new BasicAttribute(this.ldapPropertiesMapper.getGroupMemberAttribute(), dn))};
            ((SpringLdapTemplateWrapper)this.ldapTemplate.get()).modifyAttributes(this.asLdapGroupName(group.getDn(), group.getName()), mods);
        }
        catch (OperationNotSupportedException mods) {
        }
        catch (GroupNotFoundException e) {
            logger.error("Could not modify memers of group with DN: " + dn, (Throwable)e);
        }
        catch (NamingException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void removeUserFromGroup(String username, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException, OperationFailedException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes group = this.findGroupByName(groupName);
        LDAPUserWithAttributes user = this.findUserByName(username);
        if (!this.isDnDirectGroupMember(user.getDn(), group)) {
            throw new MembershipNotFoundException(username, groupName);
        }
        this.removeDnFromGroup(user.getDn(), group);
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, MembershipNotFoundException, InvalidMembershipException, OperationFailedException {
        Validate.notEmpty((CharSequence)childGroup, (String)"childGroup argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)parentGroup, (String)"parentGroup argument cannot be null or empty", (Object[])new Object[0]);
        LDAPGroupWithAttributes parent = this.findGroupByName(parentGroup);
        LDAPGroupWithAttributes child = this.findGroupByName(childGroup);
        if (!this.isDnDirectGroupMember(child.getDn(), parent)) {
            throw new MembershipNotFoundException(childGroup, parentGroup);
        }
        if (parent.getType() != child.getType()) {
            throw new InvalidMembershipException("Cannot remove group of type " + child.getType().name() + " from group of type " + parent.getType().name());
        }
        this.removeDnFromGroup(child.getDn(), parent);
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException {
        Map<LdapName, String> users = this.getEntityNamesAsMap(this.searchDN.getUser(), EntityDescriptor.user(), this.ldapPropertiesMapper.getUserNameAttribute());
        Map<LdapName, String> groups = this.getEntityNamesAsMap(this.searchDN.getGroup(), EntityDescriptor.group((GroupType)GroupType.GROUP), this.ldapPropertiesMapper.getGroupNameAttribute());
        return new RFC4519DirectoryMembershipsIterableBuilder().forConnector(this).withFullCache(users, groups).build();
    }

    private Map<LdapName, String> getEntityNamesAsMap(LdapName baseDn, EntityDescriptor desc, String nameAttribute) throws OperationFailedException {
        LDAPQuery ldapQueryGroups;
        EntityQuery allGroups = QueryBuilder.queryFor(String.class, (EntityDescriptor)desc).returningAtMost(-1);
        try {
            ldapQueryGroups = this.ldapQueryTranslater.asLDAPFilter(allGroups, this.ldapPropertiesMapper);
        }
        catch (NullResultException e) {
            return Collections.emptyMap();
        }
        String filter = ldapQueryGroups.encode();
        if (logger.isDebugEnabled()) {
            logger.debug("Performing " + desc.getEntityType() + " search: baseDN = " + baseDn + " - filter = " + filter);
        }
        ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(nameAttribute);
        return RFC4519Directory.asMap(this.searchEntities(baseDn, filter, mapper, allGroups.getStartIndex(), allGroups.getMaxResults()));
    }

    static Map<LdapName, String> asMap(Iterable<NamedLdapEntity> entities) {
        HashMap<LdapName, String> named = new HashMap<LdapName, String>();
        for (NamedLdapEntity entity : entities) {
            named.put(entity.getDn(), entity.getName());
        }
        return named;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected <T> Iterable<T> searchGroupRelationshipsWithGroupTypeSpecified(MembershipQuery<T> query) throws OperationFailedException {
        void var2_7;
        if (query.isFindChildren()) {
            if (query.getEntityToMatch().getEntityType() != Entity.GROUP) throw new IllegalArgumentException("You can only find the GROUP or USER members of a GROUP");
            if (query.getEntityToReturn().getEntityType() == Entity.USER) {
                if (this.ldapPropertiesMapper.isUsingUserMembershipAttribute()) {
                    Iterable<LDAPUserWithAttributes> iterable = this.findUserMembersOfGroupViaMemberOf(query.getEntityNameToMatch(), query.getEntityToMatch().getGroupType(), query.getStartIndex(), query.getMaxResults());
                } else {
                    List<LDAPUserWithAttributes> list = this.findUserMembersOfGroupViaMemberDN(query.getEntityNameToMatch(), query.getEntityToMatch().getGroupType(), query.getStartIndex(), query.getMaxResults());
                }
            } else {
                if (query.getEntityToReturn().getEntityType() != Entity.GROUP) throw new IllegalArgumentException("You can only find the GROUP or USER members of a GROUP");
                if (this.ldapPropertiesMapper.isNestedGroupsDisabled()) {
                    List list = Collections.emptyList();
                } else {
                    List<LDAPGroupWithAttributes> list = this.findGroupMembersOfGroupViaMemberDN(query.getEntityNameToMatch(), query.getEntityToMatch().getGroupType(), query.getStartIndex(), query.getMaxResults());
                }
            }
        } else {
            if (query.getEntityToReturn().getEntityType() != Entity.GROUP) throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
            if (query.getEntityToReturn().getGroupType() != GroupType.GROUP) {
                if (query.getEntityToReturn().getGroupType() != GroupType.LEGACY_ROLE) throw new IllegalArgumentException("Cannot find group memberships of entity via member DN for GroupType: " + query.getEntityToReturn().getGroupType());
                return Collections.emptyList();
            }
            if (query.getReturnType() == String.class) {
                return RFC4519Directory.toGenericIterable(this.findGroupMembershipNames(query));
            }
            List<? extends LDAPGroupWithAttributes> list = this.findGroupMemberships(query);
        }
        if (query.getReturnType() != String.class) return RFC4519Directory.toGenericIterable((Iterable)var2_7);
        return RFC4519Directory.toGenericIterable(SearchResultsUtil.convertEntitiesToNames((Iterable)var2_7));
    }

    protected List<? extends LDAPGroupWithAttributes> findGroupMemberships(MembershipQuery<? extends LDAPGroupWithAttributes> query) throws OperationFailedException {
        if (query.getEntityToMatch().getEntityType() == Entity.USER) {
            if (this.ldapPropertiesMapper.isUsingUserMembershipAttributeForGroupMembership()) {
                return this.findGroupMembershipsOfUserViaMemberOf(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
            }
            return this.findGroupMembershipsOfUserViaMemberDN(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
        }
        if (query.getEntityToMatch().getEntityType() == Entity.GROUP) {
            if (this.ldapPropertiesMapper.isNestedGroupsDisabled()) {
                return Collections.emptyList();
            }
            return this.findGroupMembershipsOfGroupViaMemberDN(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
        }
        throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
    }

    protected Iterable<String> findGroupMembershipNames(MembershipQuery<String> query) throws OperationFailedException {
        if (query.getEntityToMatch().getEntityType() == Entity.USER) {
            if (this.ldapPropertiesMapper.isUsingUserMembershipAttributeForGroupMembership()) {
                return this.findGroupMembershipNamesOfUserViaMemberOf(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
            }
            return this.findGroupMembershipNamesOfUserViaMemberDN(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
        }
        if (query.getEntityToMatch().getEntityType() == Entity.GROUP) {
            if (this.ldapPropertiesMapper.isNestedGroupsDisabled()) {
                return Collections.emptyList();
            }
            return this.findGroupMembershipNamesOfGroupViaMemberDN(query.getEntityNameToMatch(), query.getStartIndex(), query.getMaxResults());
        }
        throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
    }

    private List<LDAPGroupWithAttributes> findGroupMembershipsOfUserViaMemberOf(String username, int startIndex, int maxResults) throws OperationFailedException {
        return this.findGroupMembershipsOfUserViaMemberOf(username, startIndex, maxResults, this.lookupGroupByDn);
    }

    private List<String> findGroupMembershipNamesOfUserViaMemberOf(String username, int startIndex, int maxResults) throws OperationFailedException {
        return this.findGroupMembershipsOfUserViaMemberOf(username, startIndex, maxResults, this.lookupGroupNameByDn);
    }

    static int totalResultsSize(int startIndex, int maxResults) {
        if (maxResults == -1) {
            return -1;
        }
        int totalResults = startIndex + maxResults;
        if (totalResults < 0) {
            return -1;
        }
        return totalResults;
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    protected <T> List<T> findGroupMembershipsOfUserViaMemberOf(String username, int startIndex, int maxResults, LookupByDn<T> mapper) throws OperationFailedException {
        try {
            LDAPUserWithAttributes user = this.findUserByName(username);
            Set<String> memberOfs = RFC4519Directory.getMemberOfs(user);
            if (memberOfs != null) {
                ImmutableList.Builder results = ImmutableList.builder();
                int numResultsFound = 0;
                int totalResultSize = RFC4519Directory.totalResultsSize(startIndex, maxResults);
                for (String groupDN : memberOfs) {
                    try {
                        T entity = mapper.lookup(new LdapName(groupDN));
                        results.add(entity);
                        ++numResultsFound;
                    }
                    catch (GroupNotFoundException entity) {
                    }
                    catch (javax.naming.NamingException e) {
                        logger.info("Invalid group DN {}", (Object)groupDN);
                    }
                    catch (IllegalArgumentException e) {
                        logger.info("Invalid group DN {}", (Object)groupDN);
                    }
                    if (totalResultSize == -1 || numResultsFound < totalResultSize) continue;
                    break;
                }
                return SearchResultsUtil.constrainResults((List)results.build(), (int)startIndex, (int)maxResults);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("User with name <" + username + "> does not have any memberOf values and therefore has no memberships");
            }
            return Collections.emptyList();
        }
        catch (UserNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("User with name <" + username + "> does not exist and therefore has no memberships");
            }
            return Collections.emptyList();
        }
    }

    private List<LDAPGroupWithAttributes> findGroupMembershipsOfUserViaMemberDN(String username, int startIndex, int maxResults) throws OperationFailedException {
        try {
            LDAPUserWithAttributes user = this.findUserByName(username);
            return this.findGroupMembershipsOfEntityViaMemberDN(user.getLdapName(), startIndex, maxResults);
        }
        catch (UserNotFoundException e) {
            return Collections.emptyList();
        }
        catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    private List<LDAPGroupWithAttributes> findGroupMembershipsOfGroupViaMemberDN(String groupName, int startIndex, int maxResults) throws OperationFailedException {
        try {
            LDAPGroupWithAttributes group = this.findGroupByNameAndType(groupName, GroupType.GROUP);
            return this.findGroupMembershipsOfEntityViaMemberDN(group.getLdapName(), startIndex, maxResults);
        }
        catch (GroupNotFoundException e) {
            return Collections.emptyList();
        }
    }

    private Iterable<String> findGroupMembershipNamesOfUserViaMemberDN(String username, int startIndex, int maxResults) throws OperationFailedException {
        try {
            LDAPUserWithAttributes user = this.findUserByName(username);
            return this.findGroupMembershipNamesOfEntityViaMemberDN(user.getLdapName(), startIndex, maxResults);
        }
        catch (UserNotFoundException e) {
            return Collections.emptyList();
        }
        catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    private Iterable<String> findGroupMembershipNamesOfGroupViaMemberDN(String groupName, int startIndex, int maxResults) throws OperationFailedException {
        try {
            LdapName groupDn = this.findGroupDnByName(groupName);
            return this.findGroupMembershipNamesOfEntityViaMemberDN(groupDn, startIndex, maxResults);
        }
        catch (GroupNotFoundException e) {
            return Collections.emptyList();
        }
    }

    private List<LDAPGroupWithAttributes> findGroupMembershipsOfEntityViaMemberDN(LdapName dn, int startIndex, int maxResults) throws OperationFailedException {
        return this.findGroupMembershipsOfEntityViaMemberDN(dn, startIndex, maxResults, this.getGroupContextMapper(GroupType.GROUP, true));
    }

    private Iterable<String> findGroupMembershipNamesOfEntityViaMemberDN(LdapName dn, int startIndex, int maxResults) throws OperationFailedException {
        ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getGroupNameAttribute());
        return NamedLdapEntity.namesOf(this.findGroupMembershipsOfEntityViaMemberDN(dn, startIndex, maxResults, mapper));
    }

    private <T> List<T> findGroupMembershipsOfEntityViaMemberDN(LdapName dn, int startIndex, int maxResults, ContextMapperWithRequiredAttributes<T> contextMapper) throws OperationFailedException {
        AndFilter filter = this.getGroupsByGroupMemberAttributeFilter(dn);
        LdapName baseDN = this.searchDN.getGroup();
        if (logger.isDebugEnabled()) {
            logger.debug("Executing search at DN: <" + this.searchDN.getGroup() + "> with filter: <" + filter.encode() + ">");
        }
        return this.searchEntities(baseDN, filter.encode(), contextMapper, startIndex, maxResults);
    }

    private List<LDAPGroupWithAttributes> findGroupMembersOfGroupViaMemberDN(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        return this.findMembersOfGroupViaMemberDN(groupName, groupType, LDAPGroupWithAttributes.class, startIndex, maxResults);
    }

    protected List<LDAPUserWithAttributes> findUserMembersOfGroupViaMemberDN(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        return this.findMembersOfGroupViaMemberDN(groupName, groupType, LDAPUserWithAttributes.class, startIndex, maxResults);
    }

    protected Iterable<LDAPUserWithAttributes> findUserMembersOfGroupViaMemberOf(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        Iterable<LDAPUserWithAttributes> results;
        try {
            LDAPGroupWithAttributes group = this.findGroupWithAttributesByName(groupName);
            if (group.getType() == groupType) {
                AndFilter filter = this.getUsersByUserGroupMembershipAttributeFilter(group.getLdapName());
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing search at DN: <" + this.searchDN.getUser() + "> with filter: <" + filter.encode() + ">");
                }
                results = RFC4519Directory.toGenericIterable(this.searchEntities(this.searchDN.getUser(), filter.encode(), this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()), startIndex, maxResults));
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Group with name <" + groupName + "> does exist but is of GroupType <" + group.getType() + "> and not <" + groupType + ">");
                }
                results = Collections.emptyList();
            }
        }
        catch (GroupNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Group with name <" + groupName + "> does not exist and therefore has no members");
            }
            results = Collections.emptyList();
        }
        return results;
    }

    private <T extends LDAPDirectoryEntity> List<T> findMembersOfGroupViaMemberDN(String groupName, GroupType groupType, Class<T> memberClass, int startIndex, int maxResults) throws OperationFailedException {
        try {
            LDAPGroupWithAttributes group = this.findGroupByNameAndType(groupName, groupType);
            Set<String> memberDNs = RFC4519Directory.getMemberDNs(group);
            if (memberDNs != null) {
                ImmutableList.Builder results = ImmutableList.builder();
                int numResultsFound = 0;
                int totalResultSize = RFC4519Directory.totalResultsSize(startIndex, maxResults);
                for (String memberDN : memberDNs) {
                    try {
                        T entity = this.findEntityByDN(memberDN, memberClass);
                        if (entity instanceof LDAPGroupWithAttributes) {
                            if (((LDAPGroupWithAttributes)entity).getType() == groupType) {
                                results.add(entity);
                                ++numResultsFound;
                            }
                        } else {
                            results.add(entity);
                            ++numResultsFound;
                        }
                    }
                    catch (UserNotFoundException userNotFoundException) {
                    }
                    catch (GroupNotFoundException groupNotFoundException) {
                        // empty catch block
                    }
                    if (totalResultSize == -1 || numResultsFound < totalResultSize) continue;
                    break;
                }
                return SearchResultsUtil.constrainResults((List)results.build(), (int)startIndex, (int)maxResults);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Group with name <" + groupName + "> does not have any memberDNs and therefore has no members");
            }
            return Collections.emptyList();
        }
        catch (GroupNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Group with name <" + groupName + "> does not exist and therefore has no members");
            }
            return Collections.emptyList();
        }
    }

    protected static <T> Iterable<T> toGenericIterable(Iterable list) {
        return list;
    }

    public Iterable<LdapName> findDirectMembersOfGroup(LdapName groupDn) throws OperationFailedException {
        return this.findDirectMembersOfGroup(groupDn, DN_MAPPER);
    }

    public Iterable<LdapName> findDirectMembersOfGroup(LdapName groupDn, ContextMapperWithRequiredAttributes<LdapName> dnMapper) throws OperationFailedException {
        Iterable<Object> names;
        List<Object> childDns;
        if (this.ldapPropertiesMapper.isUsingUserMembershipAttribute()) {
            AndFilter filter = this.getUsersByUserGroupMembershipAttributeFilter(groupDn);
            childDns = this.searchEntities(this.searchDN.getUser(), filter.encode(), dnMapper, 0, -1);
        } else {
            childDns = Collections.emptyList();
        }
        Supplier<Optional<LDAPGroupWithAttributes>> groupSupplier = () -> ((com.google.common.base.Supplier)Suppliers.memoize(() -> this.findGroup(groupDn))).get();
        if (!this.ldapPropertiesMapper.isUsingUserMembershipAttribute() || !this.ldapPropertiesMapper.isNestedGroupsDisabled()) {
            Optional maybeGroup = groupSupplier.get();
            if (!maybeGroup.isPresent()) {
                return Collections.emptyList();
            }
            names = this.toLdapNames(RFC4519Directory.getMemberDNs(this.postprocessGroups((List<LDAPGroupWithAttributes>)ImmutableList.of(maybeGroup.get())).get(0)));
        } else {
            names = Collections.emptyList();
        }
        return Iterables.concat(childDns, names, this.findAdditionalDirectMembers(groupDn, groupSupplier));
    }

    protected Iterable<LdapName> findAdditionalDirectMembers(LdapName groupDn, @Nullable Supplier<Optional<LDAPGroupWithAttributes>> group) throws OperationFailedException {
        return Collections.emptyList();
    }

    Optional<LDAPGroupWithAttributes> findGroup(LdapName groupDn) {
        ContextMapperWithRequiredAttributes<LDAPGroupWithAttributes> mapper = this.getGroupContextMapper(GroupType.GROUP, true);
        LDAPGroupWithAttributes group = null;
        try {
            group = ((SpringLdapTemplateWrapper)this.ldapTemplate.get()).lookup(groupDn, mapper);
        }
        catch (NameNotFoundException e) {
            if (e.getCause() instanceof javax.naming.NameNotFoundException) {
                logger.warn("Treating missing LDAP group as empty: {}", (Object)groupDn);
                return Optional.empty();
            }
            throw e;
        }
        return Optional.of(group);
    }

    private AndFilter getUsersByUserGroupMembershipAttributeFilter(LdapName groupDn) {
        AndFilter filter = new AndFilter();
        filter.and((Filter)new HardcodedFilter(this.ldapPropertiesMapper.getUserFilter()));
        filter.and((Filter)new EqualsFilter(this.ldapPropertiesMapper.getUserGroupMembershipsAttribute(), groupDn.toString()));
        return filter;
    }

    private AndFilter getGroupsByGroupMemberAttributeFilter(LdapName groupDn) {
        AndFilter filter = new AndFilter();
        filter.and((Filter)new HardcodedFilter(this.ldapPropertiesMapper.getGroupFilter()));
        filter.and((Filter)new EqualsFilter(this.ldapPropertiesMapper.getGroupMemberAttribute(), groupDn.toString()));
        return filter;
    }

    private LdapName findGroupDnByName(String groupName) throws OperationFailedException, GroupNotFoundException {
        EntityQuery query = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).exactlyMatching((Object)groupName)).returningAtMost(1);
        try {
            return (LdapName)Iterables.getOnlyElement(this.searchGroupObjects(query, DN_MAPPER));
        }
        catch (NoSuchElementException e) {
            throw new GroupNotFoundException(groupName);
        }
    }

    Iterable<LdapName> toLdapNames(Iterable<String> names) {
        return Iterables.transform(names, TO_LDAP_NAME::apply);
    }

    private static class DnMapper
    implements ContextMapperWithRequiredAttributes<LdapName> {
        private DnMapper() {
        }

        @Override
        @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
        public LdapName mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter)ctx;
            try {
                return new LdapName(context.getDn().toString());
            }
            catch (InvalidNameException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Set<String> getRequiredLdapAttributes() {
            return ImmutableSet.of();
        }
    }

    @VisibleForTesting
    static interface LookupByDn<T> {
        public T lookup(LdapName var1) throws OperationFailedException, UserNotFoundException, GroupNotFoundException;
    }
}

