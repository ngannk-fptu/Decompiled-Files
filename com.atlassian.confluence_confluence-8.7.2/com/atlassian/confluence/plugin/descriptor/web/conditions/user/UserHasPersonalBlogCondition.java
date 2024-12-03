/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import java.util.List;

public class UserHasPersonalBlogCondition
extends BaseConfluenceCondition {
    private SpaceManager spaceManager;
    private PageManager pageManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        Space space = this.spaceManager.getPersonalSpace(context.getCurrentUser());
        if (space == null) {
            return false;
        }
        List<BlogPost> blogs = this.pageManager.getBlogPosts(space, false);
        return blogs != null && !blogs.isEmpty();
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }
}

