/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableTable
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.crowd.directory.rest.mapper;

import com.atlassian.crowd.directory.rest.delta.GraphDeltaQueryResult;
import com.atlassian.crowd.directory.rest.entity.GraphDirectoryObjectList;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryGroup;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryMembership;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryUser;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroup;
import com.atlassian.crowd.directory.rest.entity.group.GraphGroupList;
import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipGroup;
import com.atlassian.crowd.directory.rest.entity.membership.GraphMembershipUser;
import com.atlassian.crowd.directory.rest.entity.user.GraphUser;
import com.atlassian.crowd.directory.rest.entity.user.GraphUsersList;
import com.atlassian.crowd.directory.rest.mapper.DeltaQueryResult;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupWithMembershipChanges;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class AzureAdRestEntityMapper {
    private static final Logger log = LoggerFactory.getLogger(AzureAdRestEntityMapper.class);
    private static final ImmutableTable<String, Boolean, BiConsumer<GroupWithMembershipChanges.Builder, String>> BUILDER_FUNCTION_MAP = ImmutableTable.builder().put((Object)"#microsoft.graph.user", (Object)true, GroupWithMembershipChanges.Builder::addUserChildrenIdsToAddItem).put((Object)"#microsoft.graph.user", (Object)false, GroupWithMembershipChanges.Builder::addUserChildrenIdsToDeleteItem).put((Object)"#microsoft.graph.group", (Object)true, GroupWithMembershipChanges.Builder::addGroupChildrenIdsToAddItem).put((Object)"#microsoft.graph.group", (Object)false, GroupWithMembershipChanges.Builder::addGroupChildrenIdsToDeleteItem).build();
    private static final Map<String, Function<GraphMembershipUser, String>> GRAPH_MEMBERSHIP_USER_GETTERS = ImmutableMap.of((Object)"mail", GraphMembershipUser::getMail, (Object)"displayName", GraphMembershipUser::getDisplayName, (Object)"givenName", GraphMembershipUser::getGivenName, (Object)"surname", GraphMembershipUser::getSurname);
    private static final Map<String, Function<GraphUser, String>> GRAPH_USER_GETTERS = ImmutableMap.of((Object)"mail", GraphUser::getMail, (Object)"displayName", GraphUser::getDisplayName, (Object)"givenName", GraphUser::getGivenName, (Object)"surname", GraphUser::getSurname);
    private static final String EXTERNAL_UPN_FRAGMENT = "#EXT#";
    private static final String DOMAIN_START = "@";

    public <T> List<T> mapUsers(GraphUsersList graphUsersList, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        return this.mapUsers(graphUsersList.getEntries(), returnType, directoryId, alternativeUsernameAttribute);
    }

    public <T> List<T> mapUsers(Collection<GraphUser> graphUsersList, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        return graphUsersList.stream().map(graphUser -> this.mapUser((GraphUser)graphUser, returnType, directoryId, alternativeUsernameAttribute)).collect(Collectors.toList());
    }

    public DeltaQueryResult<UserWithAttributes> mapDeltaQueryUsers(GraphDeltaQueryResult<GraphDeltaQueryUser> graphUsersList, long directoryId, String alternativeUsernameAttribute) {
        DeltaQueryResult.Builder builder = DeltaQueryResult.builder(AzureAdRestEntityMapper.getDeltaToken(graphUsersList.getDeltaLink()));
        graphUsersList.getResults().forEach(graphUser -> {
            if (graphUser.getRemoved() == null) {
                String username = this.getUsername((GraphUser)graphUser, alternativeUsernameAttribute);
                if (StringUtils.isNotBlank((CharSequence)username)) {
                    builder.addChangedEntity(this.mapUser((GraphUser)graphUser, (Class)UserWithAttributes.class, directoryId, alternativeUsernameAttribute));
                } else {
                    log.debug("Encountered a nameless user from Azure AD, name: {}, id: {}", (Object)username, (Object)graphUser.getId());
                    builder.addNamelessEntity(graphUser.getId());
                }
            } else {
                builder.addDeletedEntity(graphUser.getId());
            }
        });
        return this.buildAndlogDeltaQueryResults("users", builder);
    }

    private <T> DeltaQueryResult<T> buildAndlogDeltaQueryResults(String entityType, DeltaQueryResult.Builder<T> builder) {
        DeltaQueryResult<T> result = builder.build();
        log.debug("Mapped delta query {} - Changed: {}, removed: {}, nameless: {}, delta token: {}", new Object[]{entityType, result.getChangedEntities(), result.getDeletedEntities(), result.getNamelessEntities(), result.getSyncToken()});
        return result;
    }

    public <T> T mapUser(GraphUser graphUser, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        String username = this.getUsername(graphUser, alternativeUsernameAttribute);
        if (returnType == String.class) {
            return (T)username;
        }
        UserTemplate userTemplate = new UserTemplate(username, graphUser.getGivenName(), graphUser.getSurname(), graphUser.getDisplayName());
        userTemplate.setDirectoryId(directoryId);
        userTemplate.setEmailAddress(graphUser.getMail());
        boolean mappedActive = graphUser.getAccountEnabled() != null ? graphUser.getAccountEnabled() : true;
        log.trace("Mapped active flag for user '{}' from {} to {}", new Object[]{graphUser.getUserPrincipalName(), graphUser.getAccountEnabled(), mappedActive});
        userTemplate.setActive(mappedActive);
        userTemplate.setExternalId(graphUser.getId());
        log.debug("Mapped Graph user to Crowd user '{}'", (Object)userTemplate);
        if (returnType == User.class) {
            return (T)userTemplate;
        }
        return (T)UserTemplateWithAttributes.toUserWithNoAttributes((User)userTemplate);
    }

    public <T> List<T> mapGroups(GraphGroupList graphGroupList, Class<T> returnType, long directoryId) {
        return this.mapGroups(graphGroupList.getEntries(), returnType, directoryId);
    }

    public <T> List<T> mapGroups(Collection<GraphGroup> graphGroups, Class<T> returnType, long directoryId) {
        return graphGroups.stream().map(graphGroup -> this.mapGroup((GraphGroup)graphGroup, returnType, directoryId)).collect(Collectors.toList());
    }

    public DeltaQueryResult<GroupWithMembershipChanges> mapDeltaQueryGroups(GraphDeltaQueryResult<GraphDeltaQueryGroup> graphGroups, long directoryId) {
        HashMap changedGroups = new HashMap();
        DeltaQueryResult.Builder builder = DeltaQueryResult.builder(AzureAdRestEntityMapper.getDeltaToken(graphGroups.getDeltaLink()));
        graphGroups.getResults().forEach(graphGroup -> {
            if (graphGroup.getRemoved() == null) {
                if (StringUtils.isNotBlank((CharSequence)graphGroup.getDisplayName())) {
                    changedGroups.merge(graphGroup.getId(), this.mapDeltaQueryGroup((GraphDeltaQueryGroup)graphGroup, directoryId), GroupWithMembershipChanges::merge);
                } else {
                    log.debug("Encountered a nameless group from Azure AD, name: {}, id: {}", (Object)graphGroup.getDisplayName(), (Object)graphGroup.getId());
                    builder.addNamelessEntity(graphGroup.getId());
                }
            } else {
                builder.addDeletedEntity(graphGroup.getId());
            }
        });
        return this.buildAndlogDeltaQueryResults("groups", builder.addChangedEntities(changedGroups.values()));
    }

    private static String getDeltaToken(String deltaLink) {
        List deltatoken = (List)UriComponentsBuilder.fromUriString((String)deltaLink).build().getQueryParams().get((Object)"$deltatoken");
        return (String)Iterables.getOnlyElement((Iterable)deltatoken);
    }

    public <T> List<T> mapDirectoryObjects(GraphDirectoryObjectList directoryObjectList, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        return this.mapDirectoryObjects(directoryObjectList.getEntries(), returnType, directoryId, alternativeUsernameAttribute);
    }

    public <T> List<T> mapDirectoryObjects(Collection<DirectoryObject> directoryObjects, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        return directoryObjects.stream().map(graphGroup -> this.mapDirectoryObject((DirectoryObject)graphGroup, returnType, directoryId, alternativeUsernameAttribute)).collect(Collectors.toList());
    }

    public <T> T mapGroup(GraphGroup graphGroup, Class<T> returnType, long directoryId) {
        if (returnType == String.class) {
            return (T)graphGroup.getDisplayName();
        }
        GroupTemplate groupTemplate = new GroupTemplate(graphGroup.getDisplayName(), directoryId);
        groupTemplate.setDescription(graphGroup.getDescription());
        groupTemplate.setExternalId(graphGroup.getId());
        log.debug("Mapped Graph group to Crowd group {}", (Object)groupTemplate);
        if (returnType == Group.class) {
            return (T)groupTemplate;
        }
        return (T)GroupTemplateWithAttributes.ofGroupWithNoAttributes((Group)groupTemplate);
    }

    public GroupWithMembershipChanges mapDeltaQueryGroup(GraphDeltaQueryGroup graphGroup, long directoryId) {
        GroupTemplate groupTemplate = new GroupTemplate(graphGroup.getDisplayName(), directoryId);
        groupTemplate.setDescription(graphGroup.getDescription());
        groupTemplate.setExternalId(graphGroup.getId());
        GroupWithMembershipChanges.Builder builder = GroupWithMembershipChanges.builder((Group)groupTemplate);
        for (GraphDeltaQueryMembership membership : graphGroup.getMembers()) {
            Optional.ofNullable(BUILDER_FUNCTION_MAP.get((Object)membership.getType(), (Object)(membership.getRemoved() == null ? 1 : 0))).ifPresent(f -> f.accept(builder, membership.getId()));
        }
        return builder.build();
    }

    public <T> T mapDirectoryObject(DirectoryObject directoryObject, Class<T> returnType, long directoryId, String alternativeUsernameAttribute) {
        if (directoryObject instanceof GraphMembershipGroup) {
            if (returnType == String.class) {
                return (T)directoryObject.getDisplayName();
            }
            GroupTemplate groupTemplate = new GroupTemplate(directoryObject.getDisplayName(), directoryId);
            groupTemplate.setDescription(((GraphMembershipGroup)directoryObject).getDescription());
            groupTemplate.setExternalId(directoryObject.getId());
            log.debug("Mapped Graph group to Crowd group {}", (Object)groupTemplate);
            if (returnType == Group.class) {
                return (T)groupTemplate;
            }
            return (T)GroupTemplateWithAttributes.ofGroupWithNoAttributes((Group)groupTemplate);
        }
        if (directoryObject instanceof GraphMembershipUser) {
            GraphMembershipUser graphUser = (GraphMembershipUser)directoryObject;
            String username = this.getUsername(graphUser, alternativeUsernameAttribute);
            if (returnType == String.class) {
                return (T)username;
            }
            UserTemplate userTemplate = new UserTemplate(username, graphUser.getGivenName(), graphUser.getSurname(), graphUser.getDisplayName());
            userTemplate.setDirectoryId(directoryId);
            userTemplate.setEmailAddress(graphUser.getMail());
            userTemplate.setActive(((Boolean)MoreObjects.firstNonNull((Object)graphUser.getAccountEnabled(), (Object)true)).booleanValue());
            userTemplate.setExternalId(directoryObject.getId());
            log.debug("Mapped Graph user to Crowd user '{}'", (Object)userTemplate);
            if (returnType == User.class) {
                return (T)userTemplate;
            }
            return (T)UserTemplateWithAttributes.toUserWithNoAttributes((User)userTemplate);
        }
        throw new IllegalArgumentException("Cannot map directory object of type " + directoryObject.getClass());
    }

    public String getUsername(GraphUser user, String alternativeUsernameAttribute) {
        return this.getUsername(user.getUserPrincipalName(), this.getAlternateUsername(user, GRAPH_USER_GETTERS, alternativeUsernameAttribute));
    }

    private String getUsername(GraphMembershipUser user, String alternativeUsernameAttribute) {
        return this.getUsername(user.getUserPrincipalName(), this.getAlternateUsername(user, GRAPH_MEMBERSHIP_USER_GETTERS, alternativeUsernameAttribute));
    }

    private <T> String getAlternateUsername(T user, Map<String, Function<T, String>> getters, String attributeName) {
        if (StringUtils.isEmpty((CharSequence)attributeName) || "userPrincipalName".equals(attributeName)) {
            return null;
        }
        Function<T, String> getter = getters.get(attributeName);
        Preconditions.checkArgument((getter != null ? 1 : 0) != 0, (String)"'%s' is not a valid username attribute", (Object)attributeName);
        return getter.apply(user);
    }

    private <T> String getUsername(String upn, String alternateUsername) {
        if (this.isExternalUpn(upn) && StringUtils.isNotEmpty((CharSequence)alternateUsername) && !this.sameDomain(upn, alternateUsername)) {
            return alternateUsername;
        }
        return upn;
    }

    private boolean sameDomain(@Nonnull String upn, @Nonnull String alternateUsername) {
        int upnDomainStartPos = upn.lastIndexOf(DOMAIN_START);
        return upnDomainStartPos >= 0 && alternateUsername.endsWith(upn.substring(upnDomainStartPos));
    }

    private boolean isExternalUpn(String upn) {
        return StringUtils.contains((CharSequence)upn, (CharSequence)EXTERNAL_UPN_FRAGMENT);
    }
}

