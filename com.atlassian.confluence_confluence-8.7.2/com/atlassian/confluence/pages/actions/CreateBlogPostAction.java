/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.blogpost.BlogPostProvider;
import com.atlassian.confluence.content.service.blogpost.CreateBlogPostCommand;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.core.service.CommandActionHelper;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.actions.AbstractCreatePageAction;
import com.atlassian.confluence.pages.actions.CalendarLanguageUtil;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.User;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public class CreateBlogPostAction
extends AbstractCreatePageAction {
    private BlogPostService blogPostService;
    private CommandActionHelper helper;
    private SimpleBlogPostProvider blogPostProvider;
    private static final long ONE_DAY = TimeUnit.DAYS.toMillis(1L);

    @Override
    protected CommandActionHelper getCommandActionHelper() {
        if (this.helper == null) {
            this.helper = new CommandActionHelper(this.createCommand());
        }
        return this.helper;
    }

    @Override
    protected void initialiseProvider(AbstractPage abstractPage) {
        this.blogPostProvider.setBlogPost((BlogPost)abstractPage);
    }

    protected ServiceCommand createCommand() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        boolean notifySelf = false;
        if (remoteUser != null) {
            notifySelf = this.userAccessor.getConfluenceUserPreferences(remoteUser).isWatchingOwnContent();
        }
        this.blogPostProvider = new SimpleBlogPostProvider();
        return this.blogPostService.newCreateBlogPostCommand((BlogPostProvider)this.blogPostProvider, null, (CreateContextProvider)this.contextProvider, this.getDraftAsCEO(), (User)remoteUser, notifySelf);
    }

    @Override
    public void validate() {
        super.validate();
        this.getCommandActionHelper();
        BlogPost blog = new BlogPost();
        if (this.getPostingDate() != null) {
            blog.setCreationDate(this.getPostingDate());
        } else {
            blog.setCreationDate(new Date());
        }
        this.checkCreationDate();
        blog.setSpace(this.getSpace());
        try {
            blog.setTitle(this.getTitle());
            this.getCommandActionHelper().validate(this);
        }
        catch (InfrastructureException infrastructureException) {
            // empty catch block
        }
    }

    @Override
    public boolean isPermitted() {
        this.getCommandActionHelper();
        BlogPost blog = new BlogPost();
        blog.setSpace(this.getSpace());
        blog.setTitle(this.getTitle());
        blog.setCreationDate(this.getPostingDate());
        this.blogPostProvider.setBlogPost(blog);
        return this.getCommandActionHelper().isAuthorized() && this.hasDraftPermission();
    }

    @Override
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    @Override
    protected void populateContextProvider() {
        this.contextProvider.getContext().put("PostingDate", this.getPostingDate());
    }

    private void checkCreationDate() {
        if (this.getPostingDate().after(new Date(System.currentTimeMillis() + ONE_DAY))) {
            this.addActionError(this.getText("news.date.in.future"));
        }
    }

    @Override
    protected AbstractPage getCreatedAbstractPage() {
        return ((CreateBlogPostCommand)this.getCommandActionHelper().getCommand()).getCreatedBlogPost();
    }

    @Override
    protected AbstractPage getPageToCreate() {
        BlogPost blogPost = new BlogPost();
        blogPost.setCreationDate(this.getPostingDate());
        return blogPost;
    }

    @Override
    public String getCancelRedirectUrl() {
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getSpace().getKey())) {
            return super.getCancelRedirectUrl();
        }
        if (this.getPage() == null) {
            return "/dashboard.action#recently-worked";
        }
        if (StringUtils.isNotBlank((CharSequence)this.originalReferrer) && !UrlUtils.isEditingUrl(this.originalReferrer)) {
            return this.originalReferrer;
        }
        return "viewrecentblogposts.action?key=" + HtmlUtil.urlEncode(this.space.getKey());
    }

    @Override
    public Date getPostingDate() {
        Date postingDate = super.getPostingDate();
        return postingDate != null ? postingDate : new Date();
    }

    public String getCalendarI18nFile() {
        return CalendarLanguageUtil.getInstance().getCalendarFilenameForLanguage(this.getLocale().getLanguage());
    }

    public void setBlogPostService(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @Override
    protected Breadcrumb getContentBreadcrumb() {
        return this.breadcrumbGenerator.getContentCollectorBreadcrumb(this.getSpace(), BlogPost.class);
    }

    private static class SimpleBlogPostProvider
    implements BlogPostProvider {
        private BlogPost blog;

        private SimpleBlogPostProvider() {
        }

        void setBlogPost(BlogPost blog) {
            this.blog = blog;
        }

        @Override
        public BlogPost getBlogPost() {
            return this.blog;
        }
    }
}

