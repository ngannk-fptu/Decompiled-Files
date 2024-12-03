/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.TinyUrlAware;
import com.atlassian.confluence.pages.actions.beans.AvailableSpaces;
import com.atlassian.confluence.pages.actions.beans.PageIncomingLinks;
import com.atlassian.confluence.pages.actions.beans.SuggestedLabels;
import com.atlassian.confluence.pages.wysiwyg.ConfluenceWysiwygConverter;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.editor.EditorManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.themes.PageHelper;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPageAction
extends AbstractPageAwareAction
implements TinyUrlAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractPageAction.class);
    protected PageManager pageManager;
    protected LinkManager linkManager;
    protected List incomingLinks;
    private Date postingDate = null;
    private LocalTime postingTime = LocalTime.of(0, 0, 0);
    private BlogPost nextPost;
    private BlogPost previousPost;
    private List<Page> permittedChildren;
    protected ConfluenceWysiwygConverter wysiwygConverter;
    protected Renderer viewRenderer;
    private String mode = null;
    public static final String RICHTEXT = "richtext";
    public static final String PREVIEW = "preview";
    private EditorManager editorManager;
    private PageHelper pageHelper;

    @Deprecated
    public String getPostingDay() {
        return BlogPost.toDatePath(this.getPostingDate());
    }

    @Deprecated
    public boolean displayDatePath() {
        return this.getPage() != null && this.getPage() instanceof BlogPost;
    }

    public BlogPost getBlogPost() {
        return (BlogPost)this.getPage();
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }

    public List<OutgoingLink> getIncomingLinks() {
        return new PageIncomingLinks(this.linkManager, this.permissionManager).getIncomingLinks(this.getPage(), this.getAuthenticatedUser());
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        permissionTypes.add("VIEWSPACE");
        return permissionTypes;
    }

    public Date getPostingDate() {
        if (this.postingDate == null && this.getPage() != null) {
            return this.getPage().getCreationDate();
        }
        return this.postingDate;
    }

    public void setPostingDate(String date) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(date, dateFormatter);
            ZonedDateTime dateTime = ZonedDateTime.of(parsedDate, this.getPostingTime(), this.getUserTimeZone().getWrappedTimeZone().toZoneId());
            this.postingDate = Date.from(dateTime.toInstant());
        }
        catch (Exception e) {
            this.addActionError("page.posting.date.invalid", date, "yyyy-MM-dd");
            log.warn("Failed parsing posting date '{}'. Expected format: '{}'", new Object[]{date, "yyyy-MM-dd", e});
            this.postingDate = null;
        }
    }

    public LocalTime getPostingTime() {
        return this.postingTime;
    }

    public void setPostingTime(String time) {
        try {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            this.updatePostingTime(localTime);
        }
        catch (Exception e) {
            this.addActionError("page.posting.time.invalid", time, "HH:mm");
            log.warn("Failed parsing posting time '{}'. Expected format: '{}'", new Object[]{time, "HH:mm", e});
        }
    }

    private void updatePostingTime(LocalTime localTime) {
        this.postingTime = localTime;
        Calendar userDateTime = this.getUserPostingDate();
        if (userDateTime == null) {
            return;
        }
        userDateTime.set(10, localTime.getHour());
        userDateTime.set(12, localTime.getMinute());
        userDateTime.set(13, localTime.getSecond());
        userDateTime.set(14, 0);
        this.setUserPostingDate(userDateTime);
    }

    private Calendar getUserPostingDate() {
        if (this.postingDate == null) {
            return null;
        }
        Calendar userDateTime = Calendar.getInstance(this.getUserTimeZone().getWrappedTimeZone());
        userDateTime.setTime(this.postingDate);
        return userDateTime;
    }

    private void setUserPostingDate(Calendar userDateTime) {
        this.postingDate = Date.from(userDateTime.toInstant());
    }

    private TimeZone getUserTimeZone() {
        ConfluenceUserPreferences userPreferences = this.getUserAccessor().getConfluenceUserPreferences(this.getAuthenticatedUser());
        return userPreferences.getTimeZone();
    }

    public BlogPost getNextPost() {
        if (this.nextPost == null && this.getPage() instanceof BlogPost) {
            BlogPost post = (BlogPost)this.getPage().getLatestVersion();
            this.nextPost = this.pageManager.findNextBlogPost(post);
        }
        return this.nextPost;
    }

    public BlogPost getPreviousPost() {
        if (this.previousPost == null && this.getPage() instanceof BlogPost) {
            BlogPost post = (BlogPost)this.getPage().getLatestVersion();
            this.previousPost = this.pageManager.findPreviousBlogPost(post);
        }
        return this.previousPost;
    }

    @Override
    public String getTinyUrl() {
        if (this.getPage() == null) {
            return null;
        }
        return new TinyUrl(this.getPage()).getIdentifier();
    }

    protected boolean isSpaceAdmin() {
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", this.getSpace(), this.getAuthenticatedUser());
    }

    @Deprecated
    public boolean isSuperUser() {
        return this.getUserAccessor().isSuperUser(this.getAuthenticatedUser());
    }

    public boolean isUserWatchingOwnContent() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        if (remoteUser == null || this.getPage() == null) {
            return false;
        }
        return this.getUserAccessor().getConfluenceUserPreferences(remoteUser).isWatchingOwnContent();
    }

    public List getAvailableSpaces() {
        return new AvailableSpaces(this.spaceManager).getAvailableSpaces(this.getSpace(), this.getAuthenticatedUser());
    }

    public AbstractPage getPreviousVersion(int version) {
        return this.pageManager.getPageByVersion(this.getPage(), version);
    }

    protected Object getBeanKey() {
        return "confluence.edit.page.bean" + this.getPage().getId();
    }

    @Override
    public ThemeHelper getHelper() {
        if (this.pageHelper == null) {
            this.pageHelper = new PageHelper(this);
        }
        return this.pageHelper;
    }

    public List<Page> getPermittedChildren() {
        if (!(this.getPage() instanceof Page)) {
            return Collections.emptyList();
        }
        if (this.permittedChildren == null) {
            this.permittedChildren = this.getPage() instanceof Page ? this.contentPermissionManager.getPermittedChildren((Page)this.getPage(), this.getAuthenticatedUser()) : Collections.emptyList();
        }
        return this.permittedChildren;
    }

    public boolean hasPermittedChildren() {
        return this.getPermittedChildren().size() > 0;
    }

    public boolean hasAttachFilePermissions() {
        return this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.getPage(), Attachment.class);
    }

    public void setConfluenceWysiwygConverter(ConfluenceWysiwygConverter wysiwygConverter) {
        this.wysiwygConverter = wysiwygConverter;
    }

    public String getMode() {
        if (PREVIEW.equals(this.mode) && (this.getActionErrors().size() > 0 || this.getFieldErrors().size() > 0)) {
            this.mode = null;
        }
        if (this.mode == null) {
            this.setMode(RICHTEXT);
        }
        return this.mode;
    }

    public List getSuggestedLabels() {
        if (this.getPage() != null) {
            return new SuggestedLabels(this.labelManager).getSuggestedLabelsForPage(this.getPage(), this.getAuthenticatedUser());
        }
        return new SuggestedLabels(this.labelManager).getSuggestedLabelsForSpace(this.getSpaceKey(), this.getAuthenticatedUser());
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public String getEditorVersion() {
        return this.editorManager.getCurrentEditorVersion();
    }

    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext result = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        result.setParameter("tinyUrl", Boolean.TRUE);
        result.setLazyParameter("numLabelsString", (Supplier<Object>)((Supplier)this::getNumberOfLabelsAsString));
        result.setLazyParameter("labels", (Supplier<Object>)((Supplier)this::getLabels));
        return result;
    }

    protected String getNumberOfAttachmentsAsString() {
        return ((PageHelper)this.getHelper()).getNumberOfAttachmentsAsString();
    }

    protected List<Label> getLabels() {
        if (this.getPage() != null) {
            return this.getPage().getVisibleLabels(this.getAuthenticatedUser());
        }
        return Collections.emptyList();
    }

    protected String getNumberOfLabelsAsString() {
        int numLabels = this.getLabels().size();
        String property = numLabels > 1 ? "editor.labels.plural" : (numLabels == 0 ? "editor.labels.zero" : "editor.labels.singular");
        return this.getText(property, new Object[]{numLabels});
    }

    public ContentEntityObject getAttachmentSourceContent() {
        return this.getPage();
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }
}

