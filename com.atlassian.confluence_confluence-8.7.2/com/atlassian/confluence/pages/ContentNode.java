/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.pages.Page;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ContentNode {
    private Page page;
    private List<ContentNode> children;
    private ContentNode parent;

    public ContentNode() {
        this.page = null;
        this.children = new ArrayList<ContentNode>();
    }

    public ContentNode(Page page) {
        this.page = page;
        this.children = new ArrayList<ContentNode>();
    }

    public Page getPage() {
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<ContentNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<ContentNode> children) {
        this.children = children;
    }

    public void addChild(ContentNode contentNode) {
        this.getChildren().add(contentNode);
        contentNode.setParent(this);
    }

    public void addChildren(List<ContentNode> children) {
        for (ContentNode childContentNode : children) {
            this.addChild(childContentNode);
        }
    }

    public void removeChild(ContentNode node) {
        if (this.children.contains(node)) {
            node.setParent(null);
            this.children.remove(node);
        }
    }

    public ContentNode getParent() {
        return this.parent;
    }

    public void setParent(ContentNode parent) {
        this.parent = parent;
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
        ContentNode other = (ContentNode)obj;
        return new EqualsBuilder().append((Object)this.page, (Object)other.page).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.page).hashCode();
    }

    public String toString() {
        return this.getPage() == null ? super.toString() : this.getPage().toString();
    }
}

