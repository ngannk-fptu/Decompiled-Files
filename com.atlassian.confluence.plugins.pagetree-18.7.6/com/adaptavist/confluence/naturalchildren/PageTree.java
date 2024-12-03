/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 */
package com.adaptavist.confluence.naturalchildren;

import com.adaptavist.confluence.naturalchildren.AncestorList;
import com.atlassian.confluence.pages.Page;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageTree {
    private Map<Long, PageTreeNode> topLevelPages = new LinkedHashMap<Long, PageTreeNode>();
    private Map<Long, PageTreeNode> nodesWithChildren = new HashMap<Long, PageTreeNode>();

    public boolean hasChildren(Long id) {
        return this.nodesWithChildren.get(id) != null;
    }

    public void addPage(AncestorList ancestorList, Page page) {
        Iterator<Long> iterator = ancestorList.iterator();
        long rootId = iterator.next();
        PageTreeNode parentNode = this.topLevelPages.get(rootId);
        if (parentNode == null) {
            parentNode = new PageTreeNode(rootId);
            this.topLevelPages.put(rootId, parentNode);
        }
        while (iterator.hasNext()) {
            Long currentId = iterator.next();
            if (parentNode.children == null) {
                this.nodesWithChildren.put(parentNode.id, parentNode);
            }
            parentNode = parentNode.addChildIfDoesNotExist(currentId);
        }
        parentNode.page = page;
    }

    public List<Page> getTopLevelPageList() {
        return this.topLevelPages.values().stream().map(node -> node.page).collect(Collectors.toList());
    }

    public List<Page> getChildrenPages(long pageId) {
        PageTreeNode currentNode = this.nodesWithChildren.get(pageId);
        if (currentNode == null) {
            return Collections.emptyList();
        }
        return currentNode.children.values().stream().filter(node -> node.page != null).map(node -> node.page).collect(Collectors.toList());
    }

    static class PageTreeNode {
        long id;
        Page page;
        Map<Long, PageTreeNode> children;

        private PageTreeNode(long id) {
            this.id = id;
        }

        PageTreeNode addChildIfDoesNotExist(long childId) {
            PageTreeNode childNode;
            if (this.children == null) {
                this.children = new LinkedHashMap<Long, PageTreeNode>();
            }
            if ((childNode = this.children.get(childId)) == null) {
                childNode = new PageTreeNode(childId);
                this.children.put(childId, childNode);
            }
            return childNode;
        }
    }
}

