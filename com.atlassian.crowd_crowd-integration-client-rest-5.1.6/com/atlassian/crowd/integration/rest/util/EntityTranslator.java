/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.integration.rest.util;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.integration.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.integration.rest.entity.EventEntityList;
import com.atlassian.crowd.integration.rest.entity.GroupEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEntityList;
import com.atlassian.crowd.integration.rest.entity.GroupEventEntity;
import com.atlassian.crowd.integration.rest.entity.GroupMembershipEventEntity;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntity;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.integration.rest.entity.PasswordEntity;
import com.atlassian.crowd.integration.rest.entity.UserEntity;
import com.atlassian.crowd.integration.rest.entity.UserEntityList;
import com.atlassian.crowd.integration.rest.entity.UserEventEntity;
import com.atlassian.crowd.integration.rest.entity.UserMembershipEventEntity;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.collect.ImmutableList;
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
import org.apache.commons.lang3.Validate;

public class EntityTranslator {
    private EntityTranslator() {
    }

    public static UserEntity toUserEntity(User user) {
        return EntityTranslator.toUserEntity(user, (PasswordCredential)null);
    }

    public static UserEntity toUserEntity(User user, @Nullable PasswordCredential passwordCredential) {
        Date updatedDate;
        Date createdDate;
        boolean isPasswordEncrypted;
        PasswordEntity password;
        if (user == null) {
            return null;
        }
        if (passwordCredential != null) {
            password = new PasswordEntity(passwordCredential.getCredential());
            isPasswordEncrypted = passwordCredential.isEncryptedCredential();
        } else {
            password = null;
            isPasswordEncrypted = false;
        }
        if (user instanceof TimestampedUser) {
            TimestampedUser timestampedUser = (TimestampedUser)user;
            createdDate = timestampedUser.getCreatedDate();
            updatedDate = timestampedUser.getUpdatedDate();
        } else {
            createdDate = null;
            updatedDate = null;
        }
        return new UserEntity(user.getName(), user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getEmailAddress(), password, user.isActive(), user.getExternalId(), createdDate, updatedDate, isPasswordEncrypted);
    }

    public static UserEntity toUserEntity(User user, Attributes attributes) {
        if (user == null) {
            return null;
        }
        Validate.notNull((Object)attributes);
        UserEntity userEntity = EntityTranslator.toUserEntity(user);
        userEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList(attributes));
        return userEntity;
    }

    public static GroupEntity toGroupEntity(Group group) {
        return new GroupEntity(group.getName(), group.getDescription(), group.getType(), group.isActive());
    }

    public static GroupEntity toGroupEntity(Group group, Attributes attributes) {
        GroupEntity groupEntity = EntityTranslator.toGroupEntity(group);
        groupEntity.setAttributes(EntityTranslator.toMultiValuedAttributeEntityList(attributes));
        return groupEntity;
    }

    public static MultiValuedAttributeEntityList toMultiValuedAttributeEntityList(Attributes attributes) {
        if (attributes == null) {
            return null;
        }
        ArrayList keys = new ArrayList(attributes.getKeys());
        Collections.sort(keys);
        ArrayList<MultiValuedAttributeEntity> attributeList = new ArrayList<MultiValuedAttributeEntity>(keys.size());
        for (String key : keys) {
            attributeList.add(new MultiValuedAttributeEntity(key, attributes.getValues(key)));
        }
        return new MultiValuedAttributeEntityList(attributeList);
    }

    public static MultiValuedAttributeEntityList toMultiValuedAttributeEntityList(Map<String, Set<String>> attributes) {
        if (attributes == null) {
            return null;
        }
        ArrayList<MultiValuedAttributeEntity> attributeEntities = new ArrayList<MultiValuedAttributeEntity>(attributes.size());
        for (Map.Entry<String, Set<String>> attribute : attributes.entrySet()) {
            attributeEntities.add(new MultiValuedAttributeEntity(attribute.getKey(), (Collection<String>)attribute.getValue()));
        }
        return new MultiValuedAttributeEntityList(attributeEntities);
    }

    public static List<Group> toGroupList(GroupEntityList groupEntityList) {
        return ImmutableList.copyOf((Iterable)groupEntityList);
    }

    public static List<GroupWithAttributes> toGroupWithAttributesList(GroupEntityList groupEntityList) {
        return ImmutableList.copyOf((Iterable)groupEntityList);
    }

    public static List<String> toNameList(GroupEntityList groupEntityList) {
        ArrayList<String> names = new ArrayList<String>(groupEntityList.size());
        for (GroupEntity groupEntity : groupEntityList) {
            names.add(groupEntity.getName());
        }
        return names;
    }

    public static List<User> toUserList(UserEntityList userEntityList) {
        return ImmutableList.copyOf((Iterable)userEntityList);
    }

    public static List<UserWithAttributes> toUserWithAttributesList(UserEntityList userEntityList) {
        return ImmutableList.copyOf((Iterable)userEntityList);
    }

    public static List<String> toNameList(UserEntityList userEntityList) {
        ArrayList<String> names = new ArrayList<String>(userEntityList.size());
        for (UserEntity userEntity : userEntityList) {
            names.add(userEntity.getName());
        }
        return names;
    }

    public static Events toEvents(EventEntityList eventEntityList) {
        List<Object> eventEntities = eventEntityList.getEvents() != null ? eventEntityList.getEvents() : Collections.emptyList();
        ArrayList<OperationEvent> events = new ArrayList<OperationEvent>(eventEntities.size());
        for (AbstractEventEntity abstractEventEntity : eventEntities) {
            events.add(EntityTranslator.toEvent(abstractEventEntity));
        }
        return new Events(events, eventEntityList.getNewEventToken());
    }

    private static OperationEvent toEvent(AbstractEventEntity eventEntity) {
        if (eventEntity instanceof UserEventEntity) {
            UserEventEntity userEventEntity = (UserEventEntity)eventEntity;
            return new UserEvent(eventEntity.getOperation(), null, (User)userEventEntity.getUser(), EntityTranslator.toAttributes(userEventEntity.getStoredAttributes()), EntityTranslator.toAttributes(userEventEntity.getDeletedAttributes()).keySet());
        }
        if (eventEntity instanceof GroupEventEntity) {
            GroupEventEntity groupEventEntity = (GroupEventEntity)eventEntity;
            return new GroupEvent(eventEntity.getOperation(), null, (Group)groupEventEntity.getGroup(), EntityTranslator.toAttributes(groupEventEntity.getStoredAttributes()), EntityTranslator.toAttributes(groupEventEntity.getDeletedAttributes()).keySet());
        }
        if (eventEntity instanceof UserMembershipEventEntity) {
            UserMembershipEventEntity membershipEventEntity = (UserMembershipEventEntity)eventEntity;
            HashSet<String> parentGroupNames = new HashSet<String>(EntityTranslator.toNameList(membershipEventEntity.getParentGroups()));
            return new UserMembershipEvent(eventEntity.getOperation(), null, membershipEventEntity.getChildUser().getName(), parentGroupNames);
        }
        if (eventEntity instanceof GroupMembershipEventEntity) {
            GroupMembershipEventEntity membershipEventEntity = (GroupMembershipEventEntity)eventEntity;
            HashSet<String> parentGroupNames = new HashSet<String>(EntityTranslator.toNameList(membershipEventEntity.getParentGroups()));
            HashSet<String> childGroupNames = new HashSet<String>(EntityTranslator.toNameList(membershipEventEntity.getChildGroups()));
            return new GroupMembershipEvent(eventEntity.getOperation(), null, membershipEventEntity.getGroup().getName(), parentGroupNames, childGroupNames);
        }
        throw new IllegalArgumentException(eventEntity.getClass() + " is not supported");
    }

    private static Map<String, Set<String>> toAttributes(MultiValuedAttributeEntityList attributeEntityList) {
        if (attributeEntityList == null) {
            return Collections.emptyMap();
        }
        HashMap<String, Set<String>> attributes = new HashMap<String, Set<String>>(attributeEntityList.size());
        for (MultiValuedAttributeEntity attributeEntity : attributeEntityList) {
            HashSet<String> values = attributeEntity.getValues() != null ? new HashSet<String>(attributeEntity.getValues()) : null;
            attributes.put(attributeEntity.getName(), values);
        }
        return attributes;
    }
}

