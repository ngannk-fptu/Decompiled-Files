/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostsCalendar;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.pages.actions.CommentAware;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.core.util.DateUtils;
import com.atlassian.renderer.links.Link;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Supplier;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiresAnyConfluenceAccess
public class ViewPageAction
extends AbstractPageAction
implements CommentAware,
Evented<ConfluenceEvent> {
    private static final Logger log = LoggerFactory.getLogger(ViewPageAction.class);
    public static final String REQUEST_KEY_HELPER = "view.page.action.helper";
    protected NotificationManager notificationManager;
    protected CommentManager commentManager;
    private CaptchaManager captchaManager;
    private ThemeManager themeManager;
    private CollaborativeEditingHelper collaborativeEditingHelper;
    protected Renderer editRenderer;
    private XhtmlContent xhtmlContent;
    private List externalReferences;
    private Map<Comment, String> xHtmlComments;
    private String pageXHtmlContent;
    private long replyToComment;
    private BlogPostsCalendar calendar;
    private boolean showCommentArea = false;
    private boolean editComment = false;
    private BlogPost lastPostInPreviousMonth;
    private BlogPost firstPostInNextMonth;
    private boolean navigatingVersions;
    private String editingUser;
    private Comment comment;

    @Override
    public Comment getComment() {
        return this.comment;
    }

    @Override
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Page getParentPage() {
        AbstractPage abstractPage = this.getPage();
        if (abstractPage instanceof Page) {
            Page page = (Page)abstractPage;
            return page.getParent();
        }
        return null;
    }

    public List getExternalReferences() {
        return this.externalReferences;
    }

    @Override
    public boolean isPageRequired() {
        return true;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return false;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Internal
    public String getEditingUser() {
        return this.editingUser;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        User user = (User)FlashScope.get("editingUser");
        if (user != null) {
            this.editingUser = user.getFullName();
        }
        if (this.pageIsLatestVersionAndDoesNotHaveSpace()) {
            this.addActionError("error.corrupt.page", "" + this.getPage().getId(), this.getPage().getBodyContent().getBody());
            return "error";
        }
        AbstractPage page = this.xhtmlContent.convertWikiBodyToStorage(this.getPage());
        ServletContextThreadLocal.getRequest().setAttribute(REQUEST_KEY_HELPER, (Object)this.getHelper());
        HttpRequestStats.elapse("viewPageRenderingStarted");
        this.pageXHtmlContent = this.viewRenderer.render(page);
        HttpRequestStats.elapse("viewPageRenderingFinished");
        this.externalReferences = this.getPage().toPageContext().getExternalReferences();
        this.addToHistory(this.getPage());
        ServletContextThreadLocal.getRequest().setAttribute("defer.js.opt.in", (Object)true);
        return this.getPage().getType();
    }

    protected boolean pageIsLatestVersionAndDoesNotHaveSpace() {
        return this.getPage() != null && this.getPage().getSpace() == null && this.getPage().isLatestVersion();
    }

    @Override
    public ConfluenceEvent getEventToPublish(String result) {
        LocaleInfo localeInfo = this.getLocaleManager().getLocaleInfo(AuthenticatedUserThreadLocal.get());
        if (this.getPage() instanceof Page) {
            return new PageViewEvent((Object)this, (Page)this.getPage(), localeInfo);
        }
        if (this.getPage() instanceof BlogPost) {
            return new BlogPostViewEvent((Object)this, (BlogPost)this.getPage(), localeInfo);
        }
        return null;
    }

    @HtmlSafe
    public String getPageXHtmlContent() {
        return this.pageXHtmlContent;
    }

    @HtmlSafe
    public Map<Comment, String> getXHtmlComments() {
        if (this.xHtmlComments == null) {
            this.xHtmlComments = new HashMap<Comment, String>();
            for (Comment comment : this.getPage().getComments()) {
                this.xHtmlComments.put(comment, this.viewRenderer.render(comment));
            }
        }
        return this.xHtmlComments;
    }

    public Boolean getChildrenShowing() {
        return this.getUserInterfaceState().getChildrenShowing();
    }

    public void setShowChildren(Boolean showChildren) {
        this.getUserInterfaceState().setChildrenShowing(showChildren);
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public boolean isThreadComments() {
        return this.settingsManager.getGlobalSettings().isAllowThreadedComments();
    }

    public boolean isUserWatchingPage() {
        if (this.isAnonymousUser() || this.getPage() == null) {
            return false;
        }
        try {
            return this.notificationManager.isWatchingContent(this.getAuthenticatedUser(), this.getPage());
        }
        catch (Exception e) {
            log.error("Error finding if user is watching page", (Throwable)e);
            return false;
        }
    }

    public boolean isUserWatchingSpace() {
        if (this.isAnonymousUser() || this.getPage() == null) {
            return false;
        }
        ContentTypeEnum typeEnum = ContentTypeEnum.getByRepresentation(this.getPage().getType());
        return this.notificationManager.getNotificationByUserAndSpaceAndType(this.getAuthenticatedUser(), this.getSpace(), typeEnum) != null;
    }

    public String getDateString(Date date) {
        return this.getDateFormatter().formatDateFull(date);
    }

    public String renderExternalLink(Link link) {
        String linkBody;
        String extLink = linkBody = link.getLinkBody();
        if (!linkBody.contains("<")) {
            extLink = GeneralUtil.displayShortUrl(linkBody);
        }
        return "<a href=\"" + link.getUrl() + "\" title=\"" + link.getTitle() + "\">" + extLink + "</a>";
    }

    public long getReplyToComment() {
        return this.replyToComment;
    }

    public void setReplyToComment(long replyToComment) {
        this.replyToComment = replyToComment;
    }

    public BlogPostsCalendar getCalendarForThisMonth() {
        if (this.calendar == null && this.getPage() instanceof BlogPost) {
            BlogPost post = (BlogPost)this.getPage();
            Calendar cal = Calendar.getInstance();
            cal.setTime(post.getCreationDate());
            this.calendar = new BlogPostsCalendar(post.getCreationDate(), this.pageManager.getBlogPosts(this.getSpaceKey(), cal, 2), this.getSpaceKey(), this.getDateFormatter());
            this.calendar.setFirstPostInNextMonth(this.getFirstPostInNextMonth(cal));
            this.calendar.setLastPostInPreviousMonth(this.getLastPostInPreviousMonth(cal));
        }
        return this.calendar;
    }

    public BlogPost getFirstPostInNextMonth(Calendar postingDate) {
        if (this.firstPostInNextMonth == null) {
            boolean isSqlServer = false;
            SystemInformationService systemInformationService = this.getSystemInformationService();
            DatabaseInfo databaseInfo = systemInformationService.getDatabaseInfo();
            if (databaseInfo != null) {
                isSqlServer = HibernateConfig.isSqlServerDialect((String)databaseInfo.getDialect());
            }
            Date lastDayOfMonth = GeneralUtil.toEndOfMonth(postingDate, isSqlServer);
            this.firstPostInNextMonth = this.pageManager.findNextBlogPost(this.getSpaceKey(), lastDayOfMonth);
        }
        return this.firstPostInNextMonth;
    }

    public BlogPost getLastPostInPreviousMonth(Calendar postingDate) {
        if (this.lastPostInPreviousMonth == null) {
            Calendar postDate = (Calendar)postingDate.clone();
            DateUtils.toStartOfPeriod((Calendar)postDate, (int)2);
            this.lastPostInPreviousMonth = this.pageManager.findPreviousBlogPost(this.getSpaceKey(), postDate.getTime());
        }
        return this.lastPostInPreviousMonth;
    }

    public List<ContentPermission> getInheritedContentPermissions() {
        return this.contentPermissionManager.getInheritedContentPermissionSets(this.getPage()).stream().flatMap(set -> StreamSupport.stream(set.spliterator(), false)).collect(Collectors.toList());
    }

    public List<ContentPermission> getThisPagePermissions() {
        return this.getPage().getPermissions();
    }

    public long getPageIdOfVersionBefore(AbstractPage page) {
        ContentEntityObject entity = this.pageManager.getPreviousVersion(page);
        return entity == null ? -1L : entity.getId();
    }

    public long getPageIdOfVersionAfter(AbstractPage page) {
        ContentEntityObject entity = this.pageManager.getNextVersion(page);
        return entity == null ? -1L : entity.getId();
    }

    public boolean hasPreviousVersion(AbstractPage page) {
        try {
            return this.getPageIdOfVersionBefore(page) > 0L;
        }
        catch (Exception e) {
            log.error("Error retrieving version of page previous to: " + page, (Throwable)e);
            return false;
        }
    }

    public boolean hasNextVersion(AbstractPage page) {
        try {
            return this.getPageIdOfVersionAfter(page) > 0L;
        }
        catch (Exception e) {
            log.error("Error retrieving version of page after: " + page, (Throwable)e);
            return false;
        }
    }

    public boolean isShowCommentArea() {
        return this.showCommentArea;
    }

    public void setShowCommentArea(boolean showCommentArea) {
        this.showCommentArea = showCommentArea;
    }

    public boolean isEditComment() {
        return this.editComment;
    }

    public void setEditComment(boolean editComment) {
        this.editComment = editComment;
    }

    public boolean isNavigatingVersions() {
        return this.navigatingVersions;
    }

    public void setNavigatingVersions(boolean navigatingVersions) {
        this.navigatingVersions = navigatingVersions;
    }

    public void setCommentManager(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    @HtmlSafe
    public String getCommentAsXHtmlForWysiwyg() {
        return this.editRenderer.render(this.getComment());
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext result = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        result.setLazyParameter("labels", (Supplier<Object>)((Supplier)() -> this.getPage() == null ? Collections.emptyList() : this.getPage().getLabelsForDisplay(this.getAuthenticatedUser())));
        result.setLazyParameter("parentPage", (Supplier<Object>)((Supplier)this::getParentPage));
        if (this.getClass().equals(ViewPageAction.class)) {
            result.setParameter("viewMode", Boolean.TRUE);
        }
        return result;
    }

    public WebInterfaceContext getWebInterfaceContext(Comment comment) {
        DefaultWebInterfaceContext result = DefaultWebInterfaceContext.copyOf(this.getWebInterfaceContext());
        result.setComment(comment);
        return result;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getPage());
    }

    public List getViewInheritedContentPermissionSets() {
        return this.contentPermissionManager.getContentPermissionSets(((Page)this.getPage()).getParent(), "View");
    }

    public boolean hasAnyPermissions() {
        return this.getPage().hasContentPermissions() || this.getInheritedContentPermissions().size() > 0;
    }

    public void setEditRenderer(Renderer editRenderer) {
        this.editRenderer = editRenderer;
    }

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public boolean isChildrenNotShown() {
        Theme spaceTheme = this.themeManager.getSpaceTheme(this.getSpaceKey());
        return spaceTheme.hasSpaceSideBar();
    }

    @ExperimentalApi
    public void setCollaborativeEditingHelper(CollaborativeEditingHelper collaborativeEditingHelper) {
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    @ExperimentalApi
    public CollaborativeEditingHelper getCollaborativeEditingHelper() {
        return this.collaborativeEditingHelper;
    }
}

