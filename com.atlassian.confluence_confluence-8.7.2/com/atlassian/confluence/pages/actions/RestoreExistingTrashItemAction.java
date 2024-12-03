/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import org.apache.commons.lang3.StringUtils;

public class RestoreExistingTrashItemAction
extends AbstractSpaceAction
implements SpaceAdministrative {
    private PageManager pageManager;
    private ContentEntityManager contentEntityManager;
    protected long contentId;
    protected long existingContentId;
    private String newTitle;
    private String restoreAction;
    private AttachmentManager attachmentManager;

    @Override
    public String doDefault() throws Exception {
        this.setNewTitle(this.getContentEntityObject().getTitle());
        this.setRestoreAction("overwrite");
        return "input";
    }

    @Override
    public void validate() {
        if (!"rename".equals(this.getRestoreAction())) {
            return;
        }
        String newTitle = this.getNewTitle();
        if (StringUtils.isBlank((CharSequence)newTitle)) {
            this.addActionError(this.getText("page.title.empty"));
            return;
        }
        if (this.hasInvalidTitleCharacters(newTitle)) {
            this.addActionError(this.getText("page.title.invalid"));
            return;
        }
        ContentEntityObject ceo = this.getContentEntityObject();
        if (this.hasClashingContent(ceo) && ceo.getTitle().equals(newTitle)) {
            this.addActionError("page.title.exists.specific", "'" + newTitle + "'");
        }
    }

    protected boolean hasInvalidTitleCharacters(String str) {
        return !AbstractPage.isValidPageTitle(str);
    }

    private boolean hasClashingContent(ContentEntityObject ceo) {
        return this.findClashingContent(ceo) != null;
    }

    private ContentEntityObject findClashingContent(ContentEntityObject ceo) {
        if (ceo instanceof Page) {
            Page page = (Page)ceo;
            return this.pageManager.getPage(page.getSpaceKey(), page.getTitle());
        }
        if (ceo instanceof BlogPost) {
            BlogPost blog = (BlogPost)ceo;
            return this.pageManager.getBlogPost(blog.getSpaceKey(), blog.getTitle(), BlogPost.toCalendar(blog.getCreationDate()));
        }
        if (ceo instanceof Attachment) {
            Attachment attachment = (Attachment)ceo;
            return this.attachmentManager.getAttachment(attachment.getContainer(), attachment.getFileName());
        }
        return null;
    }

    public String execute() throws Exception {
        return this.getRestoreAction();
    }

    @Override
    public boolean isPermitted() {
        ContentEntityObject existingContentEntityObject = this.getExistingContentEntityObject();
        if (!(existingContentEntityObject instanceof SpaceContentEntityObject)) {
            return false;
        }
        return super.isPermitted() && ((SpaceContentEntityObject)existingContentEntityObject).isInSpace(this.getSpace());
    }

    public String doOverwrite() {
        ContentEntityObject existingCeo = this.getExistingContentEntityObject();
        if (existingCeo != null) {
            if (existingCeo instanceof AbstractPage) {
                ((AbstractPage)existingCeo).remove(this.pageManager);
            } else if (existingCeo instanceof Attachment) {
                this.attachmentManager.removeAttachmentWithoutNotifications((Attachment)existingCeo);
            } else {
                this.contentEntityManager.removeContentEntity(existingCeo);
            }
        }
        this.restoreItem(this.getContentEntityObject());
        return "success";
    }

    public String doRenameAndRestore() {
        ContentEntityObject ceo = this.getContentEntityObject();
        if (ceo instanceof AbstractPage) {
            AbstractPage page = (AbstractPage)ceo;
            page.restore();
            this.pageManager.renamePage(page, this.getNewTitle());
        } else if (ceo instanceof Attachment) {
            Attachment attachment = (Attachment)ceo;
            attachment.restore();
            this.attachmentManager.moveAttachment(attachment, this.getNewTitle(), attachment.getContainer());
        }
        return "success";
    }

    public boolean isRenamable() {
        return this.getContentEntityObject() instanceof AbstractPage || this.getContentEntityObject() instanceof Attachment;
    }

    private void restoreItem(ContentEntityObject ceo) {
        ceo.restore();
        if (ceo instanceof AbstractPage) {
            this.pageManager.saveContentEntity(ceo, null);
        } else if (ceo instanceof Attachment) {
            Attachment attachment = (Attachment)ceo;
            this.attachmentManager.moveAttachment(attachment, attachment.getFileName(), attachment.getContainer());
        } else {
            this.contentEntityManager.saveContentEntity(ceo, null);
        }
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public String getNewTitle() {
        return this.newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getRestoreAction() {
        return this.restoreAction;
    }

    public void setRestoreAction(String restoreAction) {
        this.restoreAction = restoreAction;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public ContentEntityObject getContentEntityObject() {
        return this.contentEntityManager.getById(this.contentId);
    }

    public ContentEntityObject getExistingContentEntityObject() {
        return this.contentEntityManager.getById(this.existingContentId);
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getExistingContentId() {
        return this.existingContentId;
    }

    public void setExistingContentId(long existingContentId) {
        this.existingContentId = existingContentId;
    }

    public String getType() {
        return this.getNiceContentType(this.getContentEntityObject());
    }
}

