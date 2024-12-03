/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeBuilder {
    private final User user;
    private final ContentPermissionManager contentPermissionManager;
    private final PageManager pageManager;

    public TreeBuilder(User user, ContentPermissionManager contentPermissionManager, PageManager pageManager) {
        this.user = user;
        this.contentPermissionManager = contentPermissionManager;
        this.pageManager = pageManager;
    }

    public ContentTree createPageTree(Space space) {
        Map<Long, Boolean> permissions = this.contentPermissionManager.getPermissionSets(this.user, space);
        ContentTree contentTree = new ContentTree(this.getSortedTopLevelPages(space, permissions));
        contentTree.getRootNodes().stream().forEach(contentNode -> this.addSortedChildren((ContentNode)contentNode, permissions));
        return contentTree;
    }

    public ContentTree createPageBlogTree(Space space) {
        ContentTree contentTree = this.createPageTree(space);
        contentTree.setBlogPosts(this.getPermittedBlogPosts(space));
        return contentTree;
    }

    private List<BlogPost> getPermittedBlogPosts(Space space) {
        List<BlogPost> allBlogPosts = this.pageManager.getBlogPosts(space, true);
        return Lists.newArrayList((Iterable)Collections2.filter(allBlogPosts, blogPost -> this.contentPermissionManager.hasContentLevelPermission(this.user, "View", (ContentEntityObject)blogPost)));
    }

    private List getSortedTopLevelPages(Space space, Map<Long, Boolean> permissions) {
        ArrayList result = new ArrayList();
        List topLevelPages = this.pageManager.getTopLevelPages(space);
        topLevelPages.stream().filter(rootPage -> this.hasViewPermission(permissions, (Page)rootPage)).map(ContentNode::new).forEach(result::add);
        return result;
    }

    private boolean hasViewPermission(Map<Long, Boolean> permissions, Page page) {
        Boolean hasPermission = permissions.get(page.getContentId().asLong());
        return hasPermission == null ? true : hasPermission;
    }

    private void addSortedChildren(ContentNode contentNode, Map<Long, Boolean> permissions) {
        List<Page> pageChildren = contentNode.getPage().getSortedChildren();
        pageChildren.stream().filter(child -> this.hasViewPermission(permissions, (Page)child)).map(ContentNode::new).forEach(childNode -> {
            contentNode.addChild((ContentNode)childNode);
            this.addSortedChildren((ContentNode)childNode, permissions);
        });
    }
}

