/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.space.SpaceTrashRestoreContentEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;

public class RestoreTrashItemAction
extends AbstractSpaceAction
implements SpaceAdministrative {
    @VisibleForTesting
    static final String RESTORE_EXISTING = "restoreexisting";
    protected ContentEntityManager contentEntityManager;
    protected SpaceContentEntityObject spaceContentEntityObject;
    protected PageManager pageManager;
    protected long contentId;
    protected long existingContentId;
    private AttachmentManager attachmentManager;
    private ConfluenceIndexer indexer;

    private ContentEntityObject findExistingObject(SpaceContentEntityObject object) {
        if (object instanceof Page) {
            return this.pageManager.getPage(object.getSpaceKey(), object.getTitle());
        }
        if (object instanceof BlogPost) {
            return this.pageManager.getBlogPost(object.getSpaceKey(), object.getTitle(), BlogPost.toCalendar(object.getCreationDate()));
        }
        if (object instanceof Attachment) {
            return this.attachmentManager.getAttachment(((Attachment)object).getContainer(), object.getTitle());
        }
        return null;
    }

    @Override
    public boolean isPermitted() {
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        if (!(contentEntityObject instanceof SpaceContentEntityObject)) {
            return false;
        }
        return super.isPermitted() && ((SpaceContentEntityObject)contentEntityObject).isInSpace(this.getSpace());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ContentEntityObject ceo = this.findExistingObject(this.getSpaceContentEntityObject());
        if (ceo != null) {
            this.existingContentId = ceo.getId();
            return RESTORE_EXISTING;
        }
        return "input";
    }

    public String doRestore() {
        SpaceContentEntityObject spaceContentEntityObject = this.getSpaceContentEntityObject();
        if (spaceContentEntityObject instanceof AbstractPage) {
            AbstractPage page = (AbstractPage)spaceContentEntityObject;
            this.pageManager.restorePage(page);
        } else if (spaceContentEntityObject instanceof Attachment) {
            Attachment attachment = (Attachment)spaceContentEntityObject;
            this.attachmentManager.restore(attachment);
        } else {
            spaceContentEntityObject.restore();
            this.contentEntityManager.saveContentEntity(this.getSpaceContentEntityObject(), null);
            this.restoreIndex(this.getSpaceContentEntityObject());
        }
        this.publishEvent(spaceContentEntityObject);
        return "success";
    }

    private void publishEvent(SpaceContentEntityObject spaceContentEntityObject) {
        this.eventPublisher.publish((Object)new SpaceTrashRestoreContentEvent(this, spaceContentEntityObject.getSpace(), spaceContentEntityObject.getTypeEnum()));
    }

    private void restoreIndex(Searchable ceo) {
        Collection dependents = ceo.getSearchableDependants();
        dependents.forEach(this::restoreIndex);
        this.indexer.index(ceo);
    }

    public ContentEntityObject getContentEntityObject() {
        return this.contentEntityManager.getById(this.contentId);
    }

    public SpaceContentEntityObject getSpaceContentEntityObject() {
        return (SpaceContentEntityObject)this.contentEntityManager.getById(this.contentId);
    }

    public void setSpaceContentEntityObject(SpaceContentEntityObject spaceContentEntityObject) {
        this.spaceContentEntityObject = spaceContentEntityObject;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return this.getNiceContentType(this.getSpaceContentEntityObject());
    }

    public long getExistingContentId() {
        return this.existingContentId;
    }

    public void setExistingContentId(long existingContentId) {
        this.existingContentId = existingContentId;
    }

    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

