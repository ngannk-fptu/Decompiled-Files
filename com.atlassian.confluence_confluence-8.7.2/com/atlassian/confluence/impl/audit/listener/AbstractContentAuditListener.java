/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.internal.content.DraftUtils;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractContentAuditListener
extends AbstractAuditListener {
    final PageManager pageManager;
    final SpaceManager spaceManager;

    public AbstractContentAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PageManager pageManager, SpaceManager spaceManager, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
    }

    AbstractPage getContainerPageOrBlog(Contained contained) {
        return this.getPageOrBlog((ContentEntityObject)contained.getContainer());
    }

    AbstractPage getPageOrBlog(ContentEntityObject content) {
        if (DraftUtils.isPageOrBlogPost(content)) {
            return (AbstractPage)content;
        }
        if (DraftUtils.isDraft(content)) {
            return DraftUtils.isPersonalDraft(content) ? this.getPageOrBlogForPersonalDraft((Draft)content) : this.getPageOrBlogForSharedDraft((AbstractPage)content);
        }
        return null;
    }

    @Nullable Space getSpace(ContentEntityObject entityObject) {
        Space space = null;
        if (entityObject instanceof Spaced) {
            space = ((Spaced)((Object)entityObject.getLatestVersion())).getSpace();
        } else if (entityObject instanceof Draft) {
            Draft draft = (Draft)entityObject;
            space = this.spaceManager.getSpace(draft.getDraftSpaceKey());
        }
        return space;
    }

    String getPageOrBlogResourceType(ContentEntityObject content, String defaultValue) {
        if (DraftUtils.isPageOrBlogPost(content)) {
            return DraftUtils.isBlogPost(content) ? this.resourceTypes.blog() : this.resourceTypes.page();
        }
        return defaultValue;
    }

    String getTitle(ContentEntityObject content) {
        return (String)StringUtils.defaultIfBlank((CharSequence)content.getTitle(), (CharSequence)this.auditHelper.translate("untitled.content.render.title"));
    }

    protected Optional<String> getSpaceName(ContentEntityObject ceo) {
        return Optional.ofNullable(this.getSpace(ceo)).map(Space::getName).filter(StringUtils::isNotBlank);
    }

    private AbstractPage getPageOrBlogForSharedDraft(AbstractPage draft) {
        if (!draft.isUnpublished()) {
            return draft.getOriginalVersionPage();
        }
        return null;
    }

    private AbstractPage getPageOrBlogForPersonalDraft(Draft draft) {
        if (!draft.isNewPage()) {
            return this.pageManager.getAbstractPage(draft.getPageIdAsLong());
        }
        return null;
    }
}

