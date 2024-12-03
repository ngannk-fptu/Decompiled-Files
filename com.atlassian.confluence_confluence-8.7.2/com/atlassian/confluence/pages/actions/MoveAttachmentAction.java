/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.Action
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.ActionHelper;
import com.atlassian.confluence.pages.actions.beans.AttachmentBean;
import com.atlassian.confluence.pages.actions.beans.BootstrapAware;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.user.User;
import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveAttachmentAction
extends AbstractPageAwareAction
implements BootstrapAware,
ContentDetailAction,
BreadcrumbAware {
    private static final Logger log = LoggerFactory.getLogger(MoveAttachmentAction.class);
    private List<Space> availableSpaces;
    private String newFileName;
    private String newParentPage;
    private String newComment;
    private String newContentType;
    private static final Pattern DATE_PATH_PATTERN = Pattern.compile("/\\d{4}/\\d{2}/\\d{2}/.*");
    private static final char[] INVALID_CHARS = new char[]{'\\', '/', '\"', ':', '?', '*', '<', '|', '>'};
    private AttachmentManager attachmentManager;
    private LinkManager linkManager;
    private PageManager pageManager;
    private BreadcrumbGenerator breadcrumbGenerator;
    protected Collection<ContentEntityObject> suggestedContentToBeRefactored;
    private Attachment attachment;
    private final AttachmentBean attachmentBean = new AttachmentBean();
    private final AttachmentBean newAttachmentBean = new AttachmentBean();
    private String newPageTitle;
    private String newSpaceKey;
    private Space newSpace;
    private boolean isFromPageView;

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }

    public void setNewPageTitle(String newPageTitle) {
        this.newPageTitle = newPageTitle == null ? null : newPageTitle.trim();
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

    public void setNewContentType(String contentType) {
        this.newContentType = contentType;
    }

    public void setNewParentPage(String newParentPage) {
        this.newSpaceKey = ActionHelper.extractSpaceKey(newParentPage);
        this.newPageTitle = ActionHelper.extractPageTitle(newParentPage);
        this.newParentPage = newParentPage;
    }

    public void setNewFileName(String fileName) {
        this.newFileName = fileName;
    }

    @Override
    public void bootstrap() {
        if (StringUtils.isNotEmpty((CharSequence)this.newSpaceKey)) {
            this.newSpace = this.spaceManager.getSpace(this.newSpaceKey);
        }
        AbstractPage pageHoldingAttachment = this.getPage();
        this.attachment = this.attachmentBean.retrieveMatchingAttachment(pageHoldingAttachment, this.attachmentManager);
        Objects.requireNonNull(pageHoldingAttachment);
        if (this.newContentType == null) {
            this.newContentType = this.attachment.getMediaType();
        }
        if (this.newComment == null) {
            this.newComment = this.attachment.getVersionComment();
        }
        if (this.newFileName == null) {
            this.newFileName = this.attachmentBean.getFileName();
        }
        this.suggestedContentToBeRefactored = new ArrayList<ContentEntityObject>(this.linkManager.getReferringContent(this.getPage()));
        this.suggestedContentToBeRefactored.add(this.getPage());
    }

    @Override
    public void validate() {
        boolean cantEditAttachment;
        boolean checkForClashes;
        super.validate();
        if (StringUtils.isEmpty((CharSequence)this.newFileName)) {
            this.addFieldError("rename", this.getText("fileName.required"));
        }
        if (StringUtils.containsAny((CharSequence)this.newFileName, (char[])INVALID_CHARS)) {
            this.addFieldError("rename", this.getText("filename.contain.invalid.character"));
        }
        AbstractPage destinationPage = this.getPageForAttachment();
        if (this.destinationPageChange()) {
            if (StringUtils.isEmpty((CharSequence)this.newPageTitle)) {
                this.addFieldError("newPageTitle", this.getText("page.title.empty"));
            } else if (this.isToPage()) {
                if (destinationPage == null) {
                    this.addFieldError("move", this.getText("page.doesnot.exist"));
                }
            } else if (this.isToBlogPost() && destinationPage == null) {
                this.addFieldError("move", this.getText("blog.info.insufficient"));
            }
        }
        if (StringUtils.isNotEmpty((CharSequence)this.newComment) && this.newComment.length() > 255) {
            this.addFieldError("newComment", this.getText("comment.length.limit"));
        }
        boolean bl = checkForClashes = !this.destinationPageChange() || this.destinationPageChange() && destinationPage != null;
        if (checkForClashes && this.attachmentManager.getAttachment(destinationPage, this.newFileName) != null) {
            this.handleFilenameClash(destinationPage);
        }
        boolean cantRemoveAttachment = !this.spacePermissionManager.hasPermission("REMOVEATTACHMENT", this.getSpace(), this.getAuthenticatedUser());
        boolean cantCreateAttachment = !this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)destinationPage, Attachment.class);
        boolean cantViewAttachmentPage = !this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, destinationPage);
        boolean bl2 = cantEditAttachment = !this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, destinationPage);
        if (checkForClashes) {
            if (this.getPage() != destinationPage) {
                if (cantRemoveAttachment) {
                    this.addFieldError("move", this.getText("move.attachment.remove.permission.denied"));
                } else if (cantViewAttachmentPage) {
                    this.addFieldError("move", this.getText("move.attachment.destination.permission.cannotview"));
                } else if (cantCreateAttachment) {
                    String destinationSpaceName = destinationPage != null && destinationPage.getSpace() != null ? destinationPage.getSpace().getName() : this.getText("space.key.unknown");
                    this.addFieldError("move", this.getText("move.attachment.destination.permission.denied", new Object[]{HtmlUtil.htmlEncode(destinationSpaceName)}));
                }
            } else if (cantEditAttachment) {
                this.addActionError(this.getText("edit.attachment.permission.denied"));
            }
        }
    }

    private void handleFilenameClash(AbstractPage destinationPage) {
        if (destinationPage == this.getPage() && !this.attachmentBean.getFileName().equals(this.newFileName)) {
            this.addFieldError("rename", this.getText("fileName.exists"));
        } else if (destinationPage != this.getPage()) {
            this.addFieldError("rename", this.getText("fileName.exists.destination.page"));
        }
    }

    @Override
    public boolean isPermitted() {
        boolean hasPermissionToEditAttachment = this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.attachment);
        return super.isPermitted() && hasPermissionToEditAttachment;
    }

    public String execute() throws Exception {
        if (this.attachment.getVersionComment() == null || !this.attachment.getVersionComment().equals(this.newComment)) {
            this.attachment.setVersionComment(this.newComment);
        }
        if (StringUtils.isNotBlank((CharSequence)this.newContentType)) {
            this.attachment.setMediaType(this.newContentType);
        }
        try {
            AbstractPage oldContainer = (AbstractPage)this.attachment.getContainer();
        }
        catch (ClassCastException e) {
            log.error("The original ContentEntityObject owning the attachment could not be cast to AbstractPage", (Throwable)e);
            this.addActionError(this.getText("error.reading.current.home"));
            return "error";
        }
        this.attachmentManager.moveAttachment(this.attachment, this.newFileName, this.getPageForAttachment());
        return "success";
    }

    public List getAvailableSpaces() {
        if (this.availableSpaces == null) {
            this.availableSpaces = this.spaceManager.getAllSpaces(SpacesQuery.newQuery().forUser(this.getAuthenticatedUser()).withPermission("EDITSPACE").withSpaceType(SpaceType.GLOBAL).build());
        }
        return this.availableSpaces;
    }

    public void setIsFromPageView(boolean isFromPageView) {
        this.isFromPageView = isFromPageView;
    }

    public boolean getIsFromPageView() {
        return this.isFromPageView;
    }

    public String getRedirectPage() {
        if (this.isFromPageView) {
            return "/pages/viewpage.action?pageId=" + this.getPageId();
        }
        return "/pages/viewpageattachments.action?pageId=" + this.getPageId();
    }

    public String getNewFileName() {
        return this.newFileName;
    }

    public String getNewParentPage() {
        return this.newParentPage;
    }

    public String getNewComment() {
        return this.newComment;
    }

    public Collection<ContentEntityObject> getSuggestedContentToBeRefactored() {
        return this.suggestedContentToBeRefactored;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public String getNewContentType() {
        return this.newContentType;
    }

    public AttachmentBean getAttachmentBean() {
        return this.attachmentBean;
    }

    public AttachmentBean getNewAttachmentBean() {
        return this.newAttachmentBean;
    }

    private boolean isToBlogPost() {
        Matcher m = DATE_PATH_PATTERN.matcher(this.newPageTitle);
        return m.matches();
    }

    private boolean isToPage() {
        return !this.isToBlogPost();
    }

    private AbstractPage getPageForAttachment() {
        if (this.destinationPageChange()) {
            String spaceKeyToUse = this.newSpaceKey;
            if (StringUtils.isBlank((CharSequence)spaceKeyToUse)) {
                spaceKeyToUse = this.getPage().getSpaceKey();
            }
            if (this.isToBlogPost()) {
                return this.pageManager.getBlogPost(spaceKeyToUse, BlogPost.getTitleFromDatePath(this.newPageTitle), BlogPost.getCalendarFromDatePath(this.newPageTitle));
            }
            return this.pageManager.getPage(spaceKeyToUse, this.newPageTitle);
        }
        return this.getPage();
    }

    private boolean destinationPageChange() {
        return StringUtils.isNotEmpty((CharSequence)this.newParentPage);
    }

    public void setFileName(String fileName) {
        this.attachmentBean.setFileName(fileName);
    }

    public void setVersion(int version) {
        this.attachmentBean.setVersion(version);
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext context = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        if (this.attachment != null) {
            context.setParameter("labels", this.attachment.getLabels());
        }
        return context;
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        AbstractPage page = this.getPage();
        Breadcrumb parent = this.breadcrumbGenerator.getContentDetailActionBreadcrumb((Action)this, this.getSpace(), page);
        SimpleBreadcrumb breadcrumb = new SimpleBreadcrumb("type.attachments", "/pages/viewpageattachments.action?pageId=" + page.getId(), parent);
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return breadcrumb;
    }
}

