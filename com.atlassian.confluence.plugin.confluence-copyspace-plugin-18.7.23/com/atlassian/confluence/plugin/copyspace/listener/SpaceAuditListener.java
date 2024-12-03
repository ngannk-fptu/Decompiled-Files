/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.StandardAuditResourceTypes
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.plugin.copyspace.event.SpaceCopyEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import java.util.ArrayList;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="spaceAuditListener")
public class SpaceAuditListener {
    public static final String SPACE_COPIED_SUMMARY = "audit.logging.summary.space.copied";
    public static final String SPACE_COPY_COMMENTS = "audit.logging.summary.space.isCopyComments";
    public static final String SPACE_COPY_LABELS = "audit.logging.summary.space.isCopyLabels";
    public static final String SPACE_COPY_ATTACHMENTS = "audit.logging.summary.space.isCopyAttachments";
    public static final String SPACE_COPY_METADATA = "audit.logging.summary.space.isCopyKeepMetaData";
    public static final String SPACE_PRESERVE_WATCHERS = "audit.logging.summary.space.isPreserveWatchers";
    public static final String SPACE_COPY_BLOGPOSTS = "audit.logging.summary.space.isCopyBlogPost";
    public static final String SPACE_COPY_PAGES = "audit.logging.summary.space.isCopyPages";
    public static final String SPACE_CATEGORY = "audit.logging.category.spaces";
    private static final Logger log = LoggerFactory.getLogger(SpaceAuditListener.class);
    private final AuditService auditService;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final StandardAuditResourceTypes resourceTypes;

    @Autowired
    public SpaceAuditListener(@ComponentImport AuditService auditService, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, @ConfluenceImport StandardAuditResourceTypes resourceTypes) {
        this.auditService = auditService;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.resourceTypes = resourceTypes;
    }

    @PostConstruct
    public void registerEventListener() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterEventListener() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onSpaceCopyEvent(SpaceCopyEvent event) {
        log.debug("SpaceCopyEvent received: {}", (Object)event);
        ArrayList<AuditAttribute> attributes = new ArrayList<AuditAttribute>();
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_COMMENTS, (String)String.valueOf(event.isCopyComments())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_LABELS, (String)String.valueOf(event.isCopyLabels())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_ATTACHMENTS, (String)String.valueOf(event.isCopyAttachments())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_METADATA, (String)String.valueOf(event.isKeepMetaData())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_PRESERVE_WATCHERS, (String)String.valueOf(event.isPreserveWatchers())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_BLOGPOSTS, (String)String.valueOf(event.isCopyBlogposts())).build());
        attributes.add(AuditAttribute.fromI18nKeys((String)SPACE_COPY_PAGES, (String)String.valueOf(event.isCopyPages())).build());
        AuditEvent spaceCopyAuditEvent = AuditEvent.fromI18nKeys((String)SPACE_CATEGORY, (String)SPACE_COPIED_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildAuditResource(event.getSpace().getName(), Long.toString(event.getSpace().getId()))).extraAttributes(attributes).changedValue(ChangedValue.fromI18nKeys((String)SPACE_COPIED_SUMMARY).to(event.getSpace().getName()).build()).build();
        this.auditService.audit(spaceCopyAuditEvent);
    }

    private AuditResource buildAuditResource(String spaceName, @Nullable String spaceId) {
        return AuditResource.builder((String)spaceName, (String)this.resourceTypes.space()).id(spaceId).build();
    }
}

