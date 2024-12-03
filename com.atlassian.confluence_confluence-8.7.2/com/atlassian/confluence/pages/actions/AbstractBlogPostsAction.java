/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.util.DateUtils;
import com.atlassian.user.User;
import java.util.Calendar;
import java.util.Date;

public abstract class AbstractBlogPostsAction
extends AbstractSpaceAction {
    protected PageManager pageManager;
    private BlogPost firstPostInNextMonth;
    private BlogPost lastPostInPreviousMonth;
    private Renderer viewRenderer;
    private boolean hasCreatePermission;

    public String execute() throws Exception {
        this.hasCreatePermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)this.space, BlogPost.class);
        return super.execute();
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @HtmlSafe
    public String renderBlogPost(BlogPost post) {
        if (post == null) {
            return "";
        }
        return this.viewRenderer.render(post);
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
            this.firstPostInNextMonth = this.pageManager.findNextBlogPost(this.getKey(), lastDayOfMonth);
        }
        return this.firstPostInNextMonth;
    }

    public BlogPost getLastPostInPreviousMonth(Calendar postingDate) {
        if (this.lastPostInPreviousMonth == null) {
            Calendar postDate = (Calendar)postingDate.clone();
            DateUtils.toStartOfPeriod((Calendar)postDate, (int)2);
            this.lastPostInPreviousMonth = this.pageManager.findPreviousBlogPost(this.getKey(), postDate.getTime());
        }
        return this.lastPostInPreviousMonth;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }

    public boolean getHasCreatePermission() {
        return this.hasCreatePermission;
    }
}

