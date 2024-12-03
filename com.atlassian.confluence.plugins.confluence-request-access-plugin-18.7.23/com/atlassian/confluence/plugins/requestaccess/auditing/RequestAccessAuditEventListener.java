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
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.auditing;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.requestaccess.events.AbstractAccessEvent;
import com.atlassian.confluence.plugins.requestaccess.events.AccessGrantedEvent;
import com.atlassian.confluence.plugins.requestaccess.events.AccessRequestedEvent;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestAccessAuditEventListener {
    public static final String SHARE_CATEGORY = "audit.logging.category.pages";
    public static final String AUDIT_ACCESS_REQUESTED_PAGE = "audit.logging.summary.access.requested.page";
    public static final String AUDIT_ACCESS_REQUESTED_BLOG = "audit.logging.summary.access.requested.blog";
    public static final String AUDIT_ACCESS_GRANTED_PAGE = "audit.logging.summary.access.granted.page";
    public static final String AUDIT_ACCESS_GRANTED_BLOG = "audit.logging.summary.access.granted.blog";
    public static final String AUDIT_ACCESS_GRANTED_ATTRIBUTE_KEY_REQUESTER = "audit.logging.extra.attribute.key.access.granted.requester";
    public static final String AUDIT_ACCESS_GRANTED_ATTRIBUTE_KEY_ACCESS_TYPE = "audit.logging.extra.attribute.key.access.granted.accesstype";
    private static final Logger log = LoggerFactory.getLogger(RequestAccessAuditEventListener.class);
    private final EventPublisher eventPublisher;
    private final AuditService auditService;
    private final StandardAuditResourceTypes resourceTypes;
    private final SpaceService spaceService;

    @Autowired
    public RequestAccessAuditEventListener(@ComponentImport EventPublisher eventPublisher, @ComponentImport AuditService auditService, @ComponentImport StandardAuditResourceTypes resourceTypes, @ComponentImport SpaceService spaceService) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.auditService = Objects.requireNonNull(auditService);
        this.resourceTypes = Objects.requireNonNull(resourceTypes);
        this.spaceService = Objects.requireNonNull(spaceService);
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onAccessRequestedEvent(AccessRequestedEvent event) {
        this.createAuditEventBuilder(event).ifPresent(auditEventBuilder -> this.auditService.audit(auditEventBuilder.build()));
    }

    @EventListener
    public void onAccessGrantedEvent(AccessGrantedEvent event) {
        this.createAuditEventBuilder(event).ifPresent(auditEventBuilder -> {
            Optional.ofNullable(event.getTargetUser()).ifPresent(user -> {
                auditEventBuilder.affectedObject(AuditResource.builder((String)user.getFullName(), (String)this.resourceTypes.user()).id(user.getKey().getStringValue()).build());
                auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)AUDIT_ACCESS_GRANTED_ATTRIBUTE_KEY_REQUESTER, (String)user.getName()).build());
            });
            Optional.ofNullable(event.getAccessType()).ifPresent(accessType -> auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)AUDIT_ACCESS_GRANTED_ATTRIBUTE_KEY_ACCESS_TYPE, (String)accessType.getPermissionName()).build()));
            this.auditService.audit(auditEventBuilder.build());
        });
    }

    @VisibleForTesting
    private Optional<AuditEvent.Builder> createAuditEventBuilder(AbstractAccessEvent event) {
        if (!this.validateEvent(event)) {
            return Optional.empty();
        }
        AuditEvent.Builder auditEventBuilder = AuditEvent.fromI18nKeys((String)SHARE_CATEGORY, (String)this.getI18nActionKey(event), (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.SECURITY);
        this.findSpaceByKey(event.getSpaceKey()).ifPresent(space -> auditEventBuilder.affectedObject(AuditResource.builder((String)space.getName(), (String)this.resourceTypes.space()).id(String.valueOf(space.getId())).build()));
        Optional.ofNullable((AbstractPage)event.getContent()).ifPresent(content -> auditEventBuilder.affectedObject(AuditResource.builder((String)content.getTitle(), (String)(ContentTypeEnum.PAGE.equals((Object)content.getTypeEnum()) ? this.resourceTypes.page() : this.resourceTypes.blog())).id(content.getIdAsString()).build()));
        return Optional.of(auditEventBuilder);
    }

    @VisibleForTesting
    String getI18nActionKey(AbstractAccessEvent event) {
        boolean isPage = ContentTypeEnum.PAGE.equals((Object)event.getContent().getTypeEnum());
        if (AccessRequestedEvent.class.isInstance(event)) {
            return isPage ? AUDIT_ACCESS_REQUESTED_PAGE : AUDIT_ACCESS_REQUESTED_BLOG;
        }
        return isPage ? AUDIT_ACCESS_GRANTED_PAGE : AUDIT_ACCESS_GRANTED_BLOG;
    }

    @VisibleForTesting
    boolean validateEvent(AbstractAccessEvent event) {
        if (event.getContent() == null) {
            log.error("{} event called without content", (Object)event.getClass().getSimpleName());
            return false;
        }
        ContentTypeEnum contentType = event.getContent().getTypeEnum();
        if (ContentTypeEnum.PAGE.equals((Object)contentType) || ContentTypeEnum.BLOG.equals((Object)contentType)) {
            return true;
        }
        log.error("{} event called with unsupported content '{}'", (Object)event.getClass().getSimpleName(), Optional.ofNullable(contentType).map(ContentTypeEnum::getType).orElse(null));
        return false;
    }

    @VisibleForTesting
    Optional<Space> findSpaceByKey(@Nullable String spaceKey) {
        return spaceKey == null ? Optional.empty() : this.spaceService.find(new Expansion[0]).withKeys(new String[]{spaceKey}).fetch();
    }
}

