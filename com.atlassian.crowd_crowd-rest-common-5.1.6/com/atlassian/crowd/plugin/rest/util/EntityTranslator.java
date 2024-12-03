/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.MultiValuedAttributeValuesHolder
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.plugins.rest.common.Link
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.crowd.directory.MultiValuedAttributeValuesHolder;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.plugin.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.plugin.rest.entity.EventEntityList;
import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.crowd.plugin.rest.entity.GroupEntityList;
import com.atlassian.crowd.plugin.rest.entity.GroupEventEntity;
import com.atlassian.crowd.plugin.rest.entity.GroupMembershipEventEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.plugin.rest.entity.PasswordEntity;
import com.atlassian.crowd.plugin.rest.entity.UserEntity;
import com.atlassian.crowd.plugin.rest.entity.UserEntityList;
import com.atlassian.crowd.plugin.rest.entity.UserEventEntity;
import com.atlassian.crowd.plugin.rest.entity.UserMembershipEventEntity;
import com.atlassian.crowd.plugin.rest.util.LinkUriHelper;
import com.atlassian.plugins.rest.common.Link;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class EntityTranslator {
    private EntityTranslator() {
    }

    public static UserEntity toUserEntity(User user, Link userLink) {
        Date updatedDate;
        Date createdDate;
        if (user == null) {
            return null;
        }
        Validate.notNull((Object)userLink);
        if (user instanceof TimestampedUser) {
            TimestampedUser timestampedUser = (TimestampedUser)user;
            createdDate = timestampedUser.getCreatedDate();
            updatedDate = timestampedUser.getUpdatedDate();
        } else {
            createdDate = null;
            updatedDate = null;
        }
        UserEntity userEntity = new UserEntity(user.getName(), user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getEmailAddress(), null, user.isActive(), userLink, EntityTranslator.toUserKey(user), null, null, createdDate, updatedDate);
        userEntity.setPassword(EntityTranslator.getEmptyPassword(userLink));
        if (user instanceof UserWithAttributes) {
            Link userAttributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(userLink.getHref()));
            userEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList((Attributes)((UserWithAttributes)user), userAttributesLink));
        } else {
            userEntity.setAttributes(EntityTranslator.getEmptyAttributes(userLink));
        }
        return userEntity;
    }

    @Nullable
    private static String toUserKey(User user) {
        return StringUtils.isBlank((CharSequence)user.getExternalId()) ? null : String.format("%d:%s", user.getDirectoryId(), user.getExternalId());
    }

    public static UserEntityList toUserEntities(List<? extends User> users, URI baseUri) {
        ArrayList<UserEntity> userEntities = new ArrayList<UserEntity>(users.size());
        for (User user : users) {
            userEntities.add(EntityTranslator.toUserEntity(user, LinkUriHelper.buildUserLink(baseUri, user.getName())));
        }
        return new UserEntityList(userEntities);
    }

    public static UserEntityList toMinimalUserEntities(List<String> usernames, URI baseUri) {
        ArrayList<UserEntity> userEntities = new ArrayList<UserEntity>(usernames.size());
        for (String username : usernames) {
            userEntities.add(UserEntity.newMinimalUserEntity(username, null, LinkUriHelper.buildUserLink(baseUri, username)));
        }
        return new UserEntityList(userEntities);
    }

    public static UserWithAttributes fromUserEntity(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserTemplateWithAttributes user = new UserTemplateWithAttributes(userEntity.getName(), -1L);
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setDisplayName(userEntity.getDisplayName());
        user.setEmailAddress(userEntity.getEmail());
        user.setActive(userEntity.isActive() != null ? userEntity.isActive() : true);
        if (userEntity.getAttributes() != null) {
            for (MultiValuedAttributeEntity attributeEntity : userEntity.getAttributes()) {
                user.setAttribute(attributeEntity.getName(), (Set)Sets.newHashSet(attributeEntity.getValues()));
            }
        }
        return user;
    }

    public static UserEntity toUserEntity(User user, Attributes attributes, Link userLink) {
        if (user == null) {
            return null;
        }
        Validate.notNull((Object)attributes);
        Validate.notNull((Object)userLink);
        UserEntity userEntity = EntityTranslator.toUserEntity(user, userLink);
        Link userAttributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(userLink.getHref()));
        userEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList(attributes, userAttributesLink));
        return userEntity;
    }

    public static GroupEntity toGroupEntity(Group group, URI baseURI) {
        return EntityTranslator.toGroupEntity(group, LinkUriHelper.buildGroupLink(baseURI, group.getName()));
    }

    public static GroupEntityList toGroupEntities(List<? extends Group> groups, URI baseURI) {
        ArrayList<GroupEntity> groupEntities = new ArrayList<GroupEntity>(groups.size());
        for (Group group : groups) {
            groupEntities.add(EntityTranslator.toGroupEntity(group, baseURI));
        }
        return new GroupEntityList(groupEntities);
    }

    public static GroupEntityList toMinimalGroupEntities(Collection<String> groupNames, URI baseUri) {
        ArrayList<GroupEntity> groupEntities = new ArrayList<GroupEntity>(groupNames.size());
        for (String groupName : groupNames) {
            groupEntities.add(GroupEntity.newMinimalGroupEntity(groupName, null, baseUri));
        }
        return new GroupEntityList(groupEntities);
    }

    public static GroupEntity toGroupEntity(Group group, Link groupLink) {
        GroupEntity groupEntity = new GroupEntity(group.getName(), group.getDescription(), group.getType(), group.isActive(), groupLink);
        if (group instanceof GroupWithAttributes) {
            Link groupAttributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(groupLink.getHref()));
            groupEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList((Attributes)((GroupWithAttributes)group), groupAttributesLink));
        } else {
            groupEntity.setAttributes(EntityTranslator.getEmptyAttributes(groupLink));
        }
        return groupEntity;
    }

    public static GroupEntity toGroupEntity(Group group, Attributes attributes, Link groupLink) {
        GroupEntity groupEntity = EntityTranslator.toGroupEntity(group, groupLink);
        Link groupAttributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(groupLink.getHref()));
        groupEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList(attributes, groupAttributesLink));
        return groupEntity;
    }

    public static GroupTemplate toGroup(GroupEntity groupEntity) {
        GroupTemplate group = new GroupTemplate(groupEntity.getName());
        group.setDescription(groupEntity.getDescription());
        group.setType(groupEntity.getType());
        group.setActive(groupEntity.isActive() != null ? groupEntity.isActive() : true);
        return group;
    }

    public static MultiValuedAttributeEntityList toMultiValuedAttributeEntityList(Map<String, Set<String>> attributes, Link link) {
        if (attributes == null) {
            return null;
        }
        return EntityTranslator.toMultiValuedAttributeEntityList((Attributes)new MultiValuedAttributeValuesHolder(attributes), link);
    }

    public static MultiValuedAttributeEntityList toMultiValuedAttributeEntityList(Attributes attributes, Link link) {
        if (attributes == null) {
            return null;
        }
        Validate.notNull((Object)link);
        ArrayList keys = new ArrayList(attributes.getKeys());
        Collections.sort(keys);
        ArrayList<MultiValuedAttributeEntity> attributeList = new ArrayList<MultiValuedAttributeEntity>(keys.size());
        for (String key : keys) {
            Link attributeLink = Link.self((URI)LinkUriHelper.buildEntityAttributeUri(link.getHref(), key));
            attributeList.add(new MultiValuedAttributeEntity(key, attributes.getValues(key), attributeLink));
        }
        return new MultiValuedAttributeEntityList(attributeList, link);
    }

    public static MultiValuedAttributeEntityList toDeletedAttributeEntityList(Set<String> attributes) {
        if (attributes == null) {
            return null;
        }
        ArrayList<MultiValuedAttributeEntity> attributeList = new ArrayList<MultiValuedAttributeEntity>(attributes.size());
        for (String attribute : attributes) {
            attributeList.add(new MultiValuedAttributeEntity(attribute, null, null));
        }
        return new MultiValuedAttributeEntityList(attributeList, null);
    }

    public static Map<String, Set<String>> toAttributes(MultiValuedAttributeEntityList attributeEntityList) {
        HashMap<String, Set<String>> attributes = new HashMap<String, Set<String>>(attributeEntityList.size());
        for (MultiValuedAttributeEntity attributeEntity : attributeEntityList) {
            attributes.put(attributeEntity.getName(), new HashSet<String>(attributeEntity.getValues()));
        }
        return attributes;
    }

    public static EventEntityList toEventEntities(Events events, URI baseUri) {
        ArrayList<AbstractEventEntity> eventEntities = new ArrayList<AbstractEventEntity>();
        for (OperationEvent event : events.getEvents()) {
            eventEntities.add(EntityTranslator.toEventEntity(event, baseUri));
        }
        return EventEntityList.create(events.getNewEventToken(), eventEntities);
    }

    public static AbstractEventEntity toEventEntity(OperationEvent event, URI baseUri) {
        if (event instanceof UserEvent) {
            UserEvent userEvent = (UserEvent)event;
            Link userLink = LinkUriHelper.buildUserLink(baseUri, userEvent.getUser().getName());
            UserEntity user = EntityTranslator.toUserEntity(userEvent.getUser(), userLink);
            Link attributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(userLink.getHref()));
            MultiValuedAttributeEntityList storedAttributes = EntityTranslator.toMultiValuedAttributeEntityList(userEvent.getStoredAttributes(), attributesLink);
            MultiValuedAttributeEntityList deletedAttributes = EntityTranslator.toDeletedAttributeEntityList(userEvent.getDeletedAttributes());
            return new UserEventEntity(userEvent.getOperation(), user, storedAttributes, deletedAttributes);
        }
        if (event instanceof GroupEvent) {
            GroupEvent groupEvent = (GroupEvent)event;
            Link groupLink = LinkUriHelper.buildGroupLink(baseUri, groupEvent.getGroup().getName());
            GroupEntity group = EntityTranslator.toGroupEntity(groupEvent.getGroup(), groupLink);
            Link attributesLink = Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(groupLink.getHref()));
            MultiValuedAttributeEntityList storedAttributes = EntityTranslator.toMultiValuedAttributeEntityList(groupEvent.getStoredAttributes(), attributesLink);
            MultiValuedAttributeEntityList deletedAttributes = EntityTranslator.toDeletedAttributeEntityList(groupEvent.getDeletedAttributes());
            return new GroupEventEntity(groupEvent.getOperation(), group, storedAttributes, deletedAttributes);
        }
        if (event instanceof UserMembershipEvent) {
            UserMembershipEvent userMembershipEvent = (UserMembershipEvent)event;
            Link userLink = LinkUriHelper.buildUserLink(baseUri, userMembershipEvent.getChildUsername());
            UserEntity childUser = UserEntity.newMinimalUserEntity(userMembershipEvent.getChildUsername(), null, userLink);
            GroupEntityList parentGroups = EntityTranslator.toMinimalGroupEntities(userMembershipEvent.getParentGroupNames(), baseUri);
            return new UserMembershipEventEntity(event.getOperation(), childUser, parentGroups);
        }
        if (event instanceof GroupMembershipEvent) {
            GroupMembershipEvent groupMembershipEvent = (GroupMembershipEvent)event;
            GroupEntity group = GroupEntity.newMinimalGroupEntity(groupMembershipEvent.getGroupName(), null, baseUri);
            GroupEntityList parentGroups = EntityTranslator.toMinimalGroupEntities(groupMembershipEvent.getParentGroupNames(), baseUri);
            GroupEntityList childGroups = EntityTranslator.toMinimalGroupEntities(groupMembershipEvent.getChildGroupNames(), baseUri);
            return new GroupMembershipEventEntity(event.getOperation(), group, parentGroups, childGroups);
        }
        throw new IllegalArgumentException(event.getClass() + " is not supported");
    }

    private static MultiValuedAttributeEntityList getEmptyAttributes(Link entityLink) {
        Validate.notNull((Object)entityLink);
        return new MultiValuedAttributeEntityList(Collections.emptyList(), Link.self((URI)LinkUriHelper.buildEntityAttributeListUri(entityLink.getHref())));
    }

    private static PasswordEntity getEmptyPassword(Link userLink) {
        Validate.notNull((Object)userLink);
        return new PasswordEntity(null, Link.edit((URI)LinkUriHelper.buildUserPasswordUri(userLink.getHref())));
    }
}

