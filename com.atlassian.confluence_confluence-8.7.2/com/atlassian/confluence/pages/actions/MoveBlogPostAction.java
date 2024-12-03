/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.content.service.BlogPostService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.blogpost.BlogPostLocator;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.pages.actions.AbstractCommandAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@ReadOnlyAccessBlocked
public class MoveBlogPostAction
extends AbstractCommandAction {
    private BlogPostService blogPostService;
    private SpaceService spaceService;
    private long blogPostId;
    private String spaceKey;

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() {
        return super.execute();
    }

    @Override
    protected ServiceCommand createCommand() {
        BlogPostLocator sourceBlogPostLocator = this.blogPostService.getIdBlogPostLocator(this.blogPostId);
        assert (this.spaceKey != null);
        return this.blogPostService.newMoveBlogPostCommand(sourceBlogPostLocator, this.spaceService.getKeySpaceLocator(this.spaceKey));
    }

    public void setBlogPostService(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setBlogPostId(long blogPostId) {
        this.blogPostId = blogPostId;
    }
}

