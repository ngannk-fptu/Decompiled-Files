/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.Page;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ContentTree {
    private final List<ContentNode> rootContentNodes;
    private List<BlogPost> blogPosts;

    public ContentTree() {
        this.rootContentNodes = new ArrayList<ContentNode>();
    }

    public ContentTree(ContentNode rootNode) {
        this.rootContentNodes = new ArrayList<ContentNode>(1);
        this.rootContentNodes.add(rootNode);
    }

    public ContentTree(List<ContentNode> rootContentNodes) {
        this.rootContentNodes = rootContentNodes;
    }

    public List<ContentNode> getRootNodes() {
        return this.rootContentNodes;
    }

    public boolean isRootNode(ContentNode node) {
        return this.rootContentNodes.contains(node);
    }

    public void filter(List<String> ids) {
        List<ContentNode> allContentNodes = this.getAllContentNodes();
        for (ContentNode contentNode : allContentNodes) {
            String contentNodeId = String.valueOf(contentNode.getPage().getId());
            if (ids.contains(contentNodeId)) continue;
            this.removeNode(contentNode);
        }
    }

    public void filter(Set<Long> includedIds, Set<Long> excludedIds) {
        ArrayList rootNodesCopy = Lists.newArrayList(this.getRootNodes());
        for (ContentNode contentNode : rootNodesCopy) {
            if (includedIds.contains(contentNode.getPage().getId())) {
                this.filterIncludedNode(contentNode, includedIds, excludedIds);
                continue;
            }
            this.filterExcludedNode(contentNode, includedIds, excludedIds);
            this.rootContentNodes.remove(contentNode);
        }
    }

    private void filterIncludedNode(ContentNode contentNode, Set<Long> includedIds, Set<Long> excludedIds) {
        ArrayList children = Lists.newArrayList(contentNode.getChildren());
        for (ContentNode childNode : children) {
            if (includedIds.contains(childNode.getPage().getId())) {
                this.filterIncludedNode(childNode, includedIds, excludedIds);
                continue;
            }
            if (!excludedIds.contains(childNode.getPage().getId())) continue;
            this.filterExcludedNode(childNode, includedIds, excludedIds);
            contentNode.removeChild(childNode);
        }
    }

    private void filterExcludedNode(ContentNode contentNode, Set<Long> includedIds, Set<Long> excludedIds) {
        ArrayList children = Lists.newArrayList(contentNode.getChildren());
        for (ContentNode childNode : children) {
            if (includedIds.contains(childNode.getPage().getId())) {
                this.rootContentNodes.add(childNode);
                this.filterIncludedNode(childNode, includedIds, excludedIds);
                continue;
            }
            this.filterExcludedNode(childNode, includedIds, excludedIds);
        }
    }

    public void addRootNode(ContentNode contentNode) {
        contentNode.setParent(null);
        this.rootContentNodes.add(contentNode);
    }

    public void removeNode(ContentNode contentNode) {
        if (this.isRootNode(contentNode)) {
            for (ContentNode childContentNode : contentNode.getChildren()) {
                this.addRootNode(childContentNode);
            }
            this.rootContentNodes.remove(contentNode);
        } else {
            contentNode.getParent().addChildren(contentNode.getChildren());
            contentNode.getParent().removeChild(contentNode);
        }
    }

    public List<ContentNode> getAllContentNodes() {
        ArrayList<ContentNode> allContentNodes = new ArrayList<ContentNode>();
        for (ContentNode contentNode : this.rootContentNodes) {
            this.collectChildNodes(allContentNodes, contentNode);
        }
        return allContentNodes;
    }

    public Page getPage(long pageId) {
        for (ContentNode contentNode : this.rootContentNodes) {
            Page matchedPage = this.getPage(contentNode, pageId);
            if (matchedPage == null) continue;
            return matchedPage;
        }
        return null;
    }

    private Page getPage(ContentNode node, long pageId) {
        if (node.getPage().getId() == pageId) {
            return node.getPage();
        }
        if (node.getChildren() != null) {
            for (ContentNode child : node.getChildren()) {
                Page childPage = this.getPage(child, pageId);
                if (childPage == null) continue;
                return childPage;
            }
        }
        return null;
    }

    public List<Page> getPages() {
        ArrayList<Page> pages = new ArrayList<Page>();
        for (ContentNode contentNode : this.getAllContentNodes()) {
            pages.add(contentNode.getPage());
        }
        return pages;
    }

    public List<BlogPost> getBlogPosts() {
        return this.blogPosts;
    }

    public void setBlogPosts(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    public int size() {
        return this.getAllContentNodes().size();
    }

    private void collectChildNodes(List<ContentNode> allContentNodes, ContentNode contentNode) {
        allContentNodes.add(contentNode);
        if (contentNode.getChildren().isEmpty()) {
            return;
        }
        for (ContentNode childNode : contentNode.getChildren()) {
            this.collectChildNodes(allContentNodes, childNode);
        }
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ContentTree other = (ContentTree)obj;
        return new EqualsBuilder().append(this.rootContentNodes, other.rootContentNodes).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(this.rootContentNodes).hashCode();
    }
}

