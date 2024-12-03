/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
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
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.directory.ldap.mapper.UserContextMapperConfig;
import com.atlassian.crowd.directory.ldap.mapper.attribute.AttributeMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.RFC2307GidNumberMapper;
import com.atlassian.crowd.directory.ldap.mapper.attribute.group.RFC2307MemberUidMapper;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.OrFilter;

public abstract class RFC2307Directory
extends SpringLDAPConnector {
    private static final Logger logger = LoggerFactory.getLogger(RFC2307Directory.class);
    private final Function<? super LDAPGroupWithAttributes, ? extends Membership> fillInPrimaryGroups = new Function<LDAPGroupWithAttributes, Membership>(){

        public Membership apply(LDAPGroupWithAttributes group) {
            final String groupName = group.getName();
            Set secondaryMemberNames = RFC2307Directory.this.getMemberNames(group);
            final HashSet allMembers = new HashSet(secondaryMemberNames);
            String gidNumber = RFC2307Directory.this.getGid(group);
            if (gidNumber != null) {
                try {
                    AndFilter filter = new AndFilter();
                    filter.and((Filter)new HardcodedFilter(RFC2307Directory.this.ldapPropertiesMapper.getUserFilter()));
                    filter.and((Filter)new EqualsFilter("gidNumber", gidNumber));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Executing search at DN: <" + RFC2307Directory.this.searchDN.getUser() + "> with filter: <" + filter.encode() + ">");
                    }
                    ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(RFC2307Directory.this.ldapPropertiesMapper.getUserNameAttribute());
                    List<NamedLdapEntity> entities = RFC2307Directory.this.searchEntities(RFC2307Directory.this.searchDN.getUser(), filter.encode(), mapper, 0, -1);
                    Iterables.addAll(allMembers, NamedLdapEntity.namesOf(entities));
                }
                catch (OperationFailedException e) {
                    logger.debug("Unable to get gid members for group: " + group.getDn(), (Throwable)e);
                }
            }
            return new Membership(){

                public String getGroupName() {
                    return groupName;
                }

                public Set<String> getUserNames() {
                    return allMembers;
                }

                public Set<String> getChildGroupNames() {
                    return Collections.emptySet();
                }
            };
        }
    };

    public RFC2307Directory(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
    }

    @Override
    protected List<AttributeMapper> getCustomGroupAttributeMappers() {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(super.getCustomGroupAttributeMappers());
        builder.add((Object)new RFC2307MemberUidMapper(this.ldapPropertiesMapper.getGroupMemberAttribute()));
        builder.add((Object)new RFC2307GidNumberMapper());
        return builder.build();
    }

    @Override
    protected List<AttributeMapper> getCustomUserAttributeMappers(UserContextMapperConfig config) {
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(super.getCustomUserAttributeMappers(config));
        if (config.includeAll()) {
            builder.add((Object)new RFC2307GidNumberMapper());
        }
        return builder.build();
    }

    private Set<String> getMemberNames(LDAPGroupWithAttributes group) {
        return group.getValues("memberUIDs");
    }

    private String getGid(Attributes entity) {
        return entity.getValue("gidNumber");
    }

    public boolean isUserDirectGroupMember(String username, String groupName) throws OperationFailedException {
        Validate.notEmpty((CharSequence)username, (String)"username argument cannot be null or empty", (Object[])new Object[0]);
        Validate.notEmpty((CharSequence)groupName, (String)"groupName argument cannot be null or empty", (Object[])new Object[0]);
        boolean isMember = false;
        try {
            String groupGid;
            LDAPUserWithAttributes user;
            String userGid;
            LDAPGroupWithAttributes group = this.findGroupByName(groupName);
            Set<String> memberNames = this.getMemberNames(group);
            if (memberNames != null) {
                for (String member : memberNames) {
                    if (!member.equalsIgnoreCase(username)) continue;
                    isMember = true;
                    break;
                }
            }
            if (!isMember && StringUtils.equals((CharSequence)(userGid = this.getGid(user = this.findUserByName(username))), (CharSequence)(groupGid = this.getGid(group)))) {
                isMember = true;
            }
        }
        catch (UserNotFoundException userNotFoundException) {
        }
        catch (GroupNotFoundException groupNotFoundException) {
            // empty catch block
        }
        return isMember;
    }

    public boolean isGroupDirectGroupMember(String childGroup, String parentGroup) {
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected <T> Iterable<T> searchGroupRelationshipsWithGroupTypeSpecified(MembershipQuery<T> query) throws OperationFailedException {
        Iterable<? extends LDAPGroupWithAttributes> relations;
        Validate.notNull(query, (String)"query argument cannot be null", (Object[])new Object[0]);
        if (query.isFindChildren()) {
            if (query.getEntityToMatch().getEntityType() != Entity.GROUP) throw new IllegalArgumentException("You can only find the GROUP or USER members of a GROUP");
            if (query.getEntityToReturn().getEntityType() == Entity.USER) {
                if (query.getReturnType() == String.class) {
                    try {
                        LDAPGroupWithAttributes group = this.findGroupByNameAndType(query.getEntityNameToMatch(), GroupType.GROUP);
                        Membership mem = (Membership)this.fillInPrimaryGroups.apply((Object)group);
                        return mem.getUserNames();
                    }
                    catch (GroupNotFoundException e) {
                        return Collections.emptyList();
                    }
                }
                relations = this.findUserMembersOfGroup(query.getEntityNameToMatch(), query.getEntityToMatch().getGroupType(), query.getStartIndex(), query.getMaxResults());
            } else {
                if (query.getEntityToReturn().getEntityType() != Entity.GROUP) throw new IllegalArgumentException("You can only find the GROUP or USER members of a GROUP");
                relations = Collections.emptyList();
            }
        } else {
            if (query.getReturnType() == String.class) {
                return this.findGroupMembershipNames(query);
            }
            relations = this.findGroupMemberships(query);
        }
        if (query.getReturnType() != String.class) return relations;
        return SearchResultsUtil.convertEntitiesToNames(relations);
    }

    private Iterable<? extends LDAPGroupWithAttributes> findGroupMemberships(MembershipQuery<? extends LDAPGroupWithAttributes> query) throws OperationFailedException {
        if (query.getEntityToReturn().getEntityType() == Entity.GROUP) {
            if (query.getEntityToMatch().getEntityType() == Entity.USER) {
                return this.findGroupMembershipsOfUser(query.getEntityNameToMatch(), query.getEntityToReturn().getGroupType(), query.getStartIndex(), query.getMaxResults());
            }
            if (query.getEntityToMatch().getEntityType() == Entity.GROUP) {
                return Collections.emptyList();
            }
            throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
        }
        throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
    }

    private Iterable<String> findGroupMembershipNames(MembershipQuery<String> query) throws OperationFailedException {
        if (query.getEntityToReturn().getEntityType() == Entity.GROUP) {
            if (query.getEntityToMatch().getEntityType() == Entity.USER) {
                return this.findGroupMembershipNamesOfUser(query.getEntityNameToMatch(), query.getEntityToReturn().getGroupType(), query.getStartIndex(), query.getMaxResults());
            }
            if (query.getEntityToMatch().getEntityType() == Entity.GROUP) {
                return Collections.emptyList();
            }
            throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
        }
        throw new IllegalArgumentException("You can only find the GROUP memberships of USER or GROUP");
    }

    private Iterable<LDAPGroupWithAttributes> findGroupMembershipsOfUser(String username, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        return this.findGroupMembershipsOfUser(username, groupType, this.getGroupContextMapper(groupType, true), startIndex, maxResults);
    }

    private Iterable<String> findGroupMembershipNamesOfUser(String username, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        ContextMapperWithRequiredAttributes<NamedLdapEntity> mapper = NamedLdapEntity.mapperFromAttribute(this.ldapPropertiesMapper.getGroupNameAttribute());
        return NamedLdapEntity.namesOf(this.findGroupMembershipsOfUser(username, groupType, mapper, startIndex, maxResults));
    }

    private <T> List<T> findGroupMembershipsOfUser(String username, GroupType groupType, ContextMapperWithRequiredAttributes<T> contextMapper, int startIndex, int maxResults) throws OperationFailedException {
        try {
            if (groupType != GroupType.GROUP) {
                if (groupType == GroupType.LEGACY_ROLE) {
                    return Collections.emptyList();
                }
                throw new IllegalArgumentException("Cannot find membership of user that are of GroupType: " + groupType);
            }
            LdapName baseDN = this.searchDN.getGroup();
            String memberAttribute = this.ldapPropertiesMapper.getGroupMemberAttribute();
            String containerFilter = this.ldapPropertiesMapper.getGroupFilter();
            LDAPUserWithAttributes user = this.findUserByName(username);
            String gidNumber = this.getGid(user);
            OrFilter membershipFilter = new OrFilter();
            membershipFilter.or((Filter)new EqualsFilter(memberAttribute, user.getName()));
            if (gidNumber != null) {
                membershipFilter.or((Filter)new EqualsFilter("gidNumber", gidNumber));
            }
            AndFilter rootFilter = new AndFilter();
            rootFilter.and((Filter)new HardcodedFilter(containerFilter));
            rootFilter.and((Filter)membershipFilter);
            return this.searchEntities(baseDN, rootFilter.encode(), contextMapper, startIndex, maxResults);
        }
        catch (UserNotFoundException e) {
            return Collections.emptyList();
        }
    }

    private List<LDAPUserWithAttributes> findUserMembersOfGroup(String groupName, GroupType groupType, int startIndex, int maxResults) throws OperationFailedException {
        try {
            Set<String> memberNames;
            LDAPGroupWithAttributes group = this.findGroupByNameAndType(groupName, groupType);
            HashSet<LDAPUserWithAttributes> members = new HashSet<LDAPUserWithAttributes>();
            String gidNumber = this.getGid(group);
            if (gidNumber != null) {
                try {
                    AndFilter filter = new AndFilter();
                    filter.and((Filter)new HardcodedFilter(this.ldapPropertiesMapper.getUserFilter()));
                    filter.and((Filter)new EqualsFilter("gidNumber", gidNumber));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Executing search at DN: <" + this.searchDN.getUser() + "> with filter: <" + filter.encode() + ">");
                    }
                    members.addAll(this.searchEntities(this.searchDN.getUser(), filter.encode(), this.getUserContextMapper(UserContextMapperConfig.Builder.withCustomAttributes().build()), startIndex, maxResults));
                }
                catch (OperationFailedException e) {
                    logger.debug("Unable to get gid members for group: " + group.getDn(), (Throwable)e);
                }
            }
            if ((memberNames = this.getMemberNames(group)) != null) {
                for (String memberName : memberNames) {
                    try {
                        members.add(this.findUserByName(memberName));
                    }
                    catch (UserNotFoundException userNotFoundException) {}
                }
            }
            return SearchResultsUtil.constrainResults(new ArrayList(members), (int)startIndex, (int)maxResults);
        }
        catch (GroupNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Group with name <" + groupName + "> does not exist and therefore has no members");
            }
            return Collections.emptyList();
        }
    }

    @Override
    public boolean supportsNestedGroups() {
        return false;
    }

    @Override
    public boolean supportsPasswordExpiration() {
        return false;
    }

    public Iterable<Membership> getMemberships() throws OperationFailedException {
        EntityQuery allGroupsQuery = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).returningAtMost(-1);
        List<LDAPGroupWithAttributes> groups = this.searchGroupObjectsOfSpecifiedGroupType(allGroupsQuery, this.getGroupContextMapper(GroupType.GROUP, true));
        return Iterables.transform(groups, this.fillInPrimaryGroups);
    }
}

