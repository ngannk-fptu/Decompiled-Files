/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter;

import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import java.util.HashMap;
import java.util.HashSet;

public class BookmarkInfo<DocumentType> {
    private HashMap<String, DocumentTreeNode<DocumentType>> _bookmarks = new HashMap();
    private HashSet<String> _headingBookmarks = new HashSet();

    public void put(String name, DocumentTreeNode<DocumentType> node) {
        this._bookmarks.put(name, node);
    }

    public void setInHeading(String name) {
        this._headingBookmarks.add(name);
    }

    public boolean isInHeading(String name) {
        return this._headingBookmarks.contains(name);
    }

    public DocumentTreeNode<DocumentType> get(String name) {
        return this._bookmarks.get(name);
    }

    public boolean containsKey(String name) {
        return this._bookmarks.containsKey(name);
    }
}

