/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage.impl;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.api.ShareAttachmentEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareCustomEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareDraftEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareEvent;
import com.atlassian.confluence.plugins.sharepage.api.SharePageService;
import com.atlassian.confluence.plugins.sharepage.api.ShareRequest;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ExportAsService(value={SharePageService.class})
@Component
public class SharePageServiceImpl
implements SharePageService {
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;
    private final EventPublisher eventPublisher;
    private final ShareGroupEmailManager shareGroupEmailManager;
    private final PermissionManager permissionManager;

    @Autowired
    public SharePageServiceImpl(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, AttachmentManager attachmentManager, EventPublisher eventPublisher, PermissionManager permissionManager, ShareGroupEmailManager shareGroupEmailManager) {
        this.contentEntityManager = Objects.requireNonNull(contentEntityManager);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.shareGroupEmailManager = Objects.requireNonNull(shareGroupEmailManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    @Override
    public void share(ShareRequest shareRequest) {
        if (shareRequest.getEntityType() == null) {
            throw new RuntimeException("Unsupported entity type: " + shareRequest.getEntityType());
        }
        ConfluenceUser sender = AuthenticatedUserThreadLocal.get();
        Set<String> userIdsOrNames = Collections.unmodifiableSet(shareRequest.getUsers());
        Set<String> groupNames = Collections.unmodifiableSet(shareRequest.getGroups());
        Set<String> requestEmails = Collections.unmodifiableSet(shareRequest.getEmails());
        HashMap<String, Set<String>> emailMap = new HashMap<String, Set<String>>();
        for (String email : requestEmails) {
            emailMap.put(email, new HashSet());
            ((Set)emailMap.get(email)).add("");
        }
        Sets.SetView mappedGroups = Sets.intersection(shareRequest.getGroups(), this.shareGroupEmailManager.getMappedGroupNames());
        for (String group : mappedGroups) {
            String email = this.shareGroupEmailManager.getGroupEmail(group);
            if (!emailMap.containsKey(email)) {
                emailMap.put(email, new HashSet());
            }
            ((Set)emailMap.get(email)).add(group);
        }
        if (shareRequest.getEntityType().in(new BaseApiEnum[]{ContentType.ATTACHMENT})) {
            this.shareAttachment((User)sender, shareRequest.getEntityId(), shareRequest.getContextualPageId(), shareRequest.getNote(), userIdsOrNames, groupNames, requestEmails, emailMap);
        } else {
            this.sharePage((User)sender, shareRequest.getEntityId(), shareRequest.getEntityType(), shareRequest.getNote(), userIdsOrNames, groupNames, requestEmails, emailMap);
        }
    }

    private void shareAttachment(User sender, Long attachmentId, Long contextualPageId, String note, Set<String> users, Set<String> groups, Set<String> requestEmails, Map<String, Set<String>> allEmails) {
        if (this.permissionManager.hasPermission(sender, Permission.VIEW, (Object)this.attachmentManager.getAttachment(attachmentId.longValue()))) {
            this.eventPublisher.publish((Object)new ShareAttachmentEvent(sender.getName(), users, requestEmails, allEmails, groups, attachmentId, contextualPageId, note));
        }
    }

    private void sharePage(User sender, Long contentEntityId, ContentType contentType, String note, Set<String> users, Set<String> groups, Set<String> requestEmails, Map<String, Set<String>> allEmails) {
        ContentEntityObject contentEntity = this.contentEntityManager.getById(contentEntityId.longValue());
        if (contentEntity == null) {
            return;
        }
        if (this.permissionManager.hasPermission(sender, Permission.VIEW, (Object)contentEntity)) {
            ShareEvent event = ContentType.BUILT_IN.contains(contentType) && contentEntity.getContentStatusObject().equals((Object)ContentStatus.DRAFT) ? new ShareDraftEvent(sender.getName(), users, requestEmails, allEmails, groups, contentEntityId, contentType, note) : (ContentType.BUILT_IN.contains(contentType) ? new ShareContentEvent(sender.getName(), users, requestEmails, allEmails, groups, contentEntityId, contentType, note) : new ShareCustomEvent(sender.getName(), users, requestEmails, allEmails, groups, contentEntityId, contentType, note));
            this.eventPublisher.publish((Object)event);
        }
    }
}

