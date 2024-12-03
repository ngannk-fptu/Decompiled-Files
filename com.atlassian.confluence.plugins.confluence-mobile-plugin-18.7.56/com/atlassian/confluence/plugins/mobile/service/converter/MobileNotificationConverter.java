/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.node.ObjectNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.mobile.dto.ActionContentDto;
import com.atlassian.confluence.plugins.mobile.dto.ActionDto;
import com.atlassian.confluence.plugins.mobile.dto.NotificationDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.AbstractActionMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ShareActionMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.TaskActionMetadataDto;
import com.atlassian.confluence.plugins.mobile.helper.NotificationHelper;
import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileConverter;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.mywork.model.Notification;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileNotificationConverter
implements MobileConverter<NotificationDto, Notification> {
    private static final Logger log = LoggerFactory.getLogger(MobileNotificationConverter.class);
    private final PersonFactory personFactory;
    private final ContentEntityManager contentEntityManager;
    private final NotificationHelper notificationHelper;
    private final PermissionManager permissionManager;

    @Autowired
    public MobileNotificationConverter(PersonFactory personFactory, PermissionManager permissionManager, NotificationHelper notificationHelper, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.personFactory = Objects.requireNonNull(personFactory);
        this.contentEntityManager = Objects.requireNonNull(contentEntityManager);
        this.notificationHelper = Objects.requireNonNull(notificationHelper);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    @Override
    public NotificationDto to(@Nonnull Notification source) throws ServiceException {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(source.getId());
        notificationDto.setRead(source.isRead());
        ActionDto.Builder actionDtoBuilder = ActionDto.builder().by(this.getActionUser(source)).when(source.getCreated());
        NotificationCategory category = NotificationHelper.getCategory(source);
        actionDtoBuilder.category(category);
        actionDtoBuilder.content(this.buildContent(source, Expansions.of((String[])new String[]{"watched"})));
        actionDtoBuilder.metadata(this.buildMetadata(source, category));
        notificationDto.setAction(actionDtoBuilder.build());
        return notificationDto;
    }

    @Override
    public List<NotificationDto> to(@Nullable List<Notification> sources) {
        ArrayList<NotificationDto> notifications = new ArrayList<NotificationDto>();
        if (sources != null && !sources.isEmpty()) {
            sources.forEach(notification -> {
                block2: {
                    try {
                        notifications.add(this.to((Notification)notification));
                    }
                    catch (ServiceException e) {
                        if (!log.isDebugEnabled()) break block2;
                        log.debug("Cannot convert notification", (Throwable)e);
                    }
                }
            });
        }
        return notifications;
    }

    private AbstractActionMetadataDto buildMetadata(Notification notification, NotificationCategory category) {
        if (category == NotificationCategory.TASK_ASSIGN) {
            JsonNode taskId = notification.getMetadata().get("taskId");
            if (taskId == null) {
                throw new ServiceException("TaskId isn't available");
            }
            return new TaskActionMetadataDto(taskId.getLongValue());
        }
        if (category == NotificationCategory.SHARE) {
            return new ShareActionMetadataDto(notification.getDescription());
        }
        if (category == NotificationCategory.SHARE_GROUP) {
            JsonNode groupName = notification.getMetadata().get("groupName");
            return new ShareActionMetadataDto(notification.getDescription(), groupName != null ? groupName.getTextValue() : "");
        }
        return null;
    }

    private ActionContentDto buildContent(Notification notification, Expansions expansions) {
        Long contentId = NotificationHelper.getContentId(notification);
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId.longValue());
        if (ceo == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)ceo)) {
            throw new NotFoundException("Cannot found content with id: " + contentId);
        }
        return this.notificationHelper.buildActionContent(ceo, expansions);
    }

    private Person getActionUser(Notification notification) {
        String actionUserName = null;
        ObjectNode metadata = notification.getMetadata();
        if (metadata != null && metadata.get("username") != null) {
            actionUserName = metadata.get("username").asText();
        }
        return this.personFactory.forUser(actionUserName);
    }
}

