/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent
 *  com.atlassian.confluence.plugins.sharepage.api.SharePageService
 *  com.atlassian.confluence.plugins.sharepage.api.ShareRequest
 *  com.atlassian.confluence.plugins.sharepage.api.ShareRequest$ShareRequestBuilder
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharelinks.listener;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.sharepage.api.SharePageService;
import com.atlassian.confluence.plugins.sharepage.api.ShareRequest;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SharelinksBlueprintListener
implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(SharelinksBlueprintListener.class);
    private static final ModuleCompleteKey SHARELINKS_BLUEPRINT_KEY = new ModuleCompleteKey("com.atlassian.confluence.plugins.confluence-business-blueprints", "sharelinks-blueprint");
    private final CommentManager commentManager;
    private final EventPublisher evenPublisher;
    private final SharePageService sharePageService;
    private final PermissionManager permissionManager;
    private final LabelManager labelManager;
    private final UserAccessor userAccessor;

    public SharelinksBlueprintListener(EventPublisher eventPublisher, CommentManager commentManager, SharePageService sharePageService, LabelManager labelManager, PermissionManager permissionManager, UserAccessor userAccessor) {
        this.evenPublisher = eventPublisher;
        this.commentManager = commentManager;
        this.sharePageService = sharePageService;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onBlueprintCreateEvent(BlueprintPageCreateEvent event) {
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(event.getBlueprint().getModuleCompleteKey());
        if (!SHARELINKS_BLUEPRINT_KEY.equals((Object)moduleCompleteKey)) {
            return;
        }
        Page blueprintPage = event.getPage();
        Map context = event.getContext();
        String comment = (String)context.get("comment");
        String shareWith = (String)context.get("sharewith");
        String label = (String)context.get("label");
        if (logger.isDebugEnabled()) {
            logger.debug("Event caught with context {}", (Object)event.getContext());
        }
        if (this.permissionManager.hasCreatePermission(this.getUser(), (Object)event.getPage().getSpace(), Comment.class)) {
            this.addCommentIfNotBlank(blueprintPage, comment);
        }
        this.addLabelIfNotBlank((Labelable)blueprintPage, label);
        this.shareWithUsers(blueprintPage, shareWith, comment);
    }

    private User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private void addCommentIfNotBlank(Page blueprintPage, String comment) {
        if (StringUtils.isNotBlank((CharSequence)comment)) {
            comment = GeneralUtil.plain2html((String)comment);
            this.commentManager.addCommentToObject((ContentEntityObject)blueprintPage, null, comment);
        }
    }

    private void addLabelIfNotBlank(Labelable blueprintPage, String label) {
        if (StringUtils.isNotBlank((CharSequence)label)) {
            HashSet<String> labels = new HashSet<String>();
            labels.addAll(Arrays.asList(label.split(",")));
            for (String labelValue : labels) {
                Label newLabel = new Label(labelValue);
                this.labelManager.addLabel(blueprintPage, newLabel);
            }
        }
    }

    @VisibleForTesting
    void shareWithUsers(Page page, String sharewith, String note) {
        if (StringUtils.isNotBlank((CharSequence)sharewith)) {
            Set<String> usersToShare = this.getUserKeys(sharewith);
            ShareRequest shareContent = new ShareRequest.ShareRequestBuilder().setEntityId(Long.valueOf(page.getId())).setEntityTypeValue(ContentType.PAGE.getType()).setUsers(usersToShare).setNote(note).build();
            this.sharePageService.share(shareContent);
        }
    }

    private Set<String> getUserKeys(String userKeyList) {
        return Arrays.stream(userKeyList.split(",")).filter(userKey -> this.userAccessor.getExistingUserByKey(new UserKey(userKey)) != null).collect(Collectors.toSet());
    }

    public void destroy() throws Exception {
        this.evenPublisher.unregister((Object)this);
    }
}

