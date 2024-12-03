/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.internal.security.ContentPermissionAddedEvent;
import com.atlassian.confluence.event.events.internal.security.ContentPermissionRemovedEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractContentAuditListener;
import com.atlassian.confluence.internal.audit.event.RestrictedPageViewNotPermittedEvent;
import com.atlassian.confluence.internal.content.DraftUtils;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContentPermissionsAuditListener
extends AbstractContentAuditListener {
    public static final String RESTRICTED_PAGE_VIEW_NOT_PERMITTED_SUMMARY = AuditHelper.buildSummaryTextKey("security.restricted.page.not.permitted");
    public static final String RESTRICTED_PAGE_VIEW_EXTRA_LINK_TYPE_KEY = "link.type";
    public static final String RESTRICTED_PAGE_VIEW_EXTRA_LINK_TYPE_DRAFT = "Draft";
    static final String PAGE_EDIT_PERMISSION_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("page.edit.permission.added");
    static final String PAGE_VIEW_PERMISSION_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("page.view.permission.added");
    static final String BLOG_POST_EDIT_PERMISSION_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("blogpost.edit.permission.added");
    static final String BLOG_POST_VIEW_PERMISSION_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("blogpost.view.permission.added");
    static final String PAGE_EDIT_PERMISSION_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("page.edit.permission.removed");
    static final String PAGE_VIEW_PERMISSION_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("page.view.permission.removed");
    static final String BLOG_POST_EDIT_PERMISSION_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("blogpost.edit.permission.removed");
    static final String BLOG_POST_VIEW_PERMISSION_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("blogpost.view.permission.removed");
    private static final String RESTRICTED_BLOGPOST_VIEW_NOT_PERMITTED_SUMMARY = AuditHelper.buildSummaryTextKey("security.restricted.blogpost.not.permitted");

    public ContentPermissionsAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PageManager pageManager, SpaceManager spaceManager, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, pageManager, spaceManager, auditingContext);
    }

    @EventListener
    public void onContentPermissionChanged(ContentPermissionAddedEvent event) {
        this.auditContentPermissionChange(event.getContent(), event.getPermission(), true);
    }

    @EventListener
    public void onContentPermissionChanged(ContentPermissionRemovedEvent event) {
        this.auditContentPermissionChange(event.getContent(), event.getPermission(), false);
    }

    @EventListener
    public void onRestrictedPageViewNotPermittedEvent(RestrictedPageViewNotPermittedEvent notPermittedEvent) {
        AbstractPage abstractPage = notPermittedEvent.getPage();
        this.save(() -> {
            AuditEvent.Builder builder = AuditEvent.fromI18nKeys((String)AuditCategories.SECURITY, (String)(abstractPage.getTypeEnum() == ContentTypeEnum.BLOG ? RESTRICTED_BLOGPOST_VIEW_NOT_PERMITTED_SUMMARY : RESTRICTED_PAGE_VIEW_NOT_PERMITTED_SUMMARY), (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.SECURITY).affectedObjects(this.buildAffectedObjects(abstractPage));
            if (abstractPage.isDraft()) {
                builder.extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute(RESTRICTED_PAGE_VIEW_EXTRA_LINK_TYPE_KEY), (String)RESTRICTED_PAGE_VIEW_EXTRA_LINK_TYPE_DRAFT).build());
            }
            return builder.build();
        });
    }

    private void auditContentPermissionChange(ContentEntityObject content, ContentPermission permission, boolean isAdded) {
        this.saveIfPresent(() -> {
            if (this.isAuditable(content, permission)) {
                AuditEvent.Builder eventBuilder = AuditEvent.fromI18nKeys((String)AuditCategories.PAGES, (String)this.getSummaryKey(content, permission, isAdded), (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.END_USER_ACTIVITY).changedValues(this.buildChangedValues(permission, isAdded)).affectedObjects(this.buildAffectedObjects(content));
                return Optional.of(eventBuilder.build());
            }
            return Optional.empty();
        });
    }

    private String getSummaryKey(ContentEntityObject content, ContentPermission permission, boolean isAdded) {
        String summaryKey = "";
        if (isAdded) {
            if (this.isContentForPage(content)) {
                summaryKey = Objects.equals(permission.getType(), "Edit") ? PAGE_EDIT_PERMISSION_ADDED_SUMMARY : PAGE_VIEW_PERMISSION_ADDED_SUMMARY;
            } else if (this.isContentForBlog(content)) {
                summaryKey = Objects.equals(permission.getType(), "Edit") ? BLOG_POST_EDIT_PERMISSION_ADDED_SUMMARY : BLOG_POST_VIEW_PERMISSION_ADDED_SUMMARY;
            }
        } else if (this.isContentForPage(content)) {
            summaryKey = Objects.equals(permission.getType(), "Edit") ? PAGE_EDIT_PERMISSION_REMOVED_SUMMARY : PAGE_VIEW_PERMISSION_REMOVED_SUMMARY;
        } else if (this.isContentForBlog(content)) {
            summaryKey = Objects.equals(permission.getType(), "Edit") ? BLOG_POST_EDIT_PERMISSION_REMOVED_SUMMARY : BLOG_POST_VIEW_PERMISSION_REMOVED_SUMMARY;
        }
        return summaryKey;
    }

    private boolean isContentForPage(ContentEntityObject content) {
        return (DraftUtils.isPageOrBlogPost(content) || DraftUtils.isSharedDraft(content)) && content.getTypeEnum() == ContentTypeEnum.PAGE || DraftUtils.isPersonalDraft(content) && Objects.equals("page", ((Draft)content).getDraftType());
    }

    private boolean isContentForBlog(ContentEntityObject content) {
        return (DraftUtils.isPageOrBlogPost(content) || DraftUtils.isSharedDraft(content)) && content.getTypeEnum() == ContentTypeEnum.BLOG || DraftUtils.isPersonalDraft(content) && Objects.equals("blogpost", ((Draft)content).getDraftType());
    }

    private boolean isAuditable(ContentEntityObject content, ContentPermission permission) {
        return !(!DraftUtils.isPageOrBlogPost(content) && (!DraftUtils.isDraft(content) || DraftUtils.isPersonalDraft(content) && content.isUnpublished()) || !Objects.equals(permission.getType(), "Edit") && !Objects.equals(permission.getType(), "View"));
    }

    private List<AuditResource> buildAffectedObjects(ContentEntityObject content) {
        ArrayList<AuditResource> auditResources = new ArrayList<AuditResource>();
        Optional.ofNullable(this.getSpace(content)).ifPresent(space -> auditResources.add(this.buildResource(space.getName(), this.resourceTypes.space(), space.getId())));
        Optional.ofNullable((ContentEntityObject)Optional.ofNullable(this.getPageOrBlog(content)).orElse((AbstractPage)content)).ifPresent(pageOrBlog -> auditResources.add(this.buildResource(this.getTitle((ContentEntityObject)pageOrBlog), this.getPageOrBlogResourceType(content, this.resourceTypes.page()), pageOrBlog.getId())));
        return auditResources;
    }

    private List<ChangedValue> buildChangedValues(ContentPermission contentPermission, boolean isAdded) {
        return this.getAuditHandlerService().handle(contentPermission, isAdded ? AuditAction.ADD : AuditAction.REMOVE);
    }
}

