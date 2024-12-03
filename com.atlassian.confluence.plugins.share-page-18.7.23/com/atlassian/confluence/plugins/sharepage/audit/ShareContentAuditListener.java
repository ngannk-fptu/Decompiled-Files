/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShareContentAuditListener {
    public static final String SHARE_CATEGORY = "audit.logging.category.pages";
    public static final String PAGE_SHARED_SUMMARY = "audit.logging.summary.page.shared";
    public static final String BLOG_SHARED_SUMMARY = "audit.logging.summary.blog.shared";
    public static final String USERS_EXTRA_ATTRIBUTE_KEY = "audit.logging.extra.attribute.key.users";
    public static final String GROUPS_EXTRA_ATTRIBUTE_KEY = "audit.logging.extra.attribute.key.groups";
    public static final String EMAILS_EXTRA_ATTRIBUTE_KEY = "audit.logging.extra.attribute.key.emails";
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final ContentEntityManager contentEntityManager;
    private final StandardAuditResourceTypes standardAuditResourceTypes;
    private final AuditService auditService;

    @Autowired
    public ShareContentAuditListener(EventPublisher eventPublisher, UserAccessor userAccessor, ContentEntityManager contentEntityManager, StandardAuditResourceTypes standardAuditResourceTypes, AuditService auditService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.contentEntityManager = Objects.requireNonNull(contentEntityManager);
        this.standardAuditResourceTypes = Objects.requireNonNull(standardAuditResourceTypes);
        this.auditService = Objects.requireNonNull(auditService);
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onShareContentEvent(ShareContentEvent event) {
        ContentType contentType = ContentType.valueOf((String)event.getContentType());
        if (!ContentType.PAGE.equals((Object)contentType) && !ContentType.BLOG_POST.equals((Object)contentType)) {
            return;
        }
        ContentEntityObject contentEntity = this.contentEntityManager.getById(event.getContentId().longValue());
        if (contentEntity == null) {
            return;
        }
        AuditEvent.Builder auditEventBuilder = AuditEvent.fromI18nKeys((String)SHARE_CATEGORY, (String)(ContentType.PAGE.equals((Object)contentType) ? PAGE_SHARED_SUMMARY : BLOG_SHARED_SUMMARY), (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.END_USER_ACTIVITY);
        auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)USERS_EXTRA_ATTRIBUTE_KEY, (String)event.getUsers().stream().map(UserKey::new).map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByKey(arg_0)).filter(Objects::nonNull).map(User::getFullName).collect(Collectors.joining(", "))).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)GROUPS_EXTRA_ATTRIBUTE_KEY, (String)String.join((CharSequence)", ", event.getGroupNames())).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)EMAILS_EXTRA_ATTRIBUTE_KEY, (String)String.join((CharSequence)", ", event.getEmails())).build());
        if (contentEntity instanceof Spaced) {
            Space space = ((Spaced)contentEntity).getSpace();
            auditEventBuilder.affectedObject(AuditResource.builder((String)space.getName(), (String)this.standardAuditResourceTypes.space()).id(String.valueOf(space.getId())).build());
        }
        auditEventBuilder.affectedObject(AuditResource.builder((String)contentEntity.getTitle(), (String)(ContentType.PAGE.equals((Object)contentType) ? this.standardAuditResourceTypes.page() : this.standardAuditResourceTypes.blog())).id(contentEntity.getIdAsString()).build());
        this.auditService.audit(auditEventBuilder.build());
    }
}

