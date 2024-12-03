/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.Doc2Wiki
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.plugins.conversion.dom;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.conversion.dom.DefaultImportContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.Doc2Wiki;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext;
import com.google.common.base.Throwables;
import java.awt.Dimension;
import java.util.List;

public class DefaultSplitImportContext
extends DefaultImportContext
implements SplitImportContext {
    private static final int MAX_TITLE_LENGTH = 254;
    private List<DocumentTreeNode<Page>> _orderedNodes;
    private int _nodeIndex;
    private final int _splitLvl;
    private final ConfluenceUser _user;
    private int _lastLevel;
    private DocumentTreeNode<Page> _currentNode;

    public DefaultSplitImportContext(PageManager pageManager, Page page, Page oldPage, AttachmentManager attachmentManager, BookmarkInfo bookmarks, List<DocumentTreeNode<Page>> orderedNodes, int splitLvl, Dimension maxImportImageSize) {
        super(pageManager, (AbstractPage)page, (AbstractPage)oldPage, attachmentManager, bookmarks, maxImportImageSize);
        this._splitLvl = splitLvl;
        this._orderedNodes = orderedNodes;
        this._user = AuthenticatedUserThreadLocal.get();
    }

    public DocumentTreeNode<Page> getNextNode() {
        return this._orderedNodes.get(this._nodeIndex++);
    }

    public boolean splitPage(StringBuilder content, Doc2Wiki doc2wiki) {
        this._currentNode = this.getNextNode();
        int nodeLvl = this._currentNode.getLvl();
        if (nodeLvl > this._splitLvl) {
            return false;
        }
        doc2wiki.pageEnd();
        this.finish(content);
        Page newPage = null;
        this.oldPage = (AbstractPage)this._currentNode.getOldPage();
        if (this.oldPage != null) {
            try {
                newPage = (Page)this.oldPage;
                this.oldPage = (Page)newPage.clone();
                newPage.setTitle(this._currentNode.getText());
                newPage.setPosition(Integer.valueOf(this._nodeIndex));
            }
            catch (Exception ex) {
                throw Throwables.propagate((Throwable)ex);
            }
        } else {
            newPage = this.createNewPage(this._currentNode.getText());
        }
        while (nodeLvl <= this._lastLevel) {
            this.page = ((Page)this.page).getParent();
            --this._lastLevel;
        }
        this._lastLevel = nodeLvl;
        ((Page)this.page).addChild(newPage);
        this.page = newPage;
        return true;
    }

    public int getNodeLevel() {
        return this._currentNode.getLvl();
    }

    public int getSplitLevel() {
        return this._splitLvl;
    }

    @Override
    public String createHyperlinkReference(StringBuffer codeBuf) {
        Object ref = this.parseFieldCode(codeBuf);
        if (ref != null && this.bookmarks.containsKey((String)ref)) {
            DocumentTreeNode originalNode;
            DocumentTreeNode node = originalNode = this.bookmarks.get((String)ref);
            while (node.getLvl() > this._splitLvl) {
                node = node.getParent();
            }
            ref = this.bookmarks.isInHeading((String)ref) && node == originalNode ? node.getText() : (node != this._currentNode && node.getLvl() != 0 ? node.getText() + "#" + (String)ref : "#" + (String)ref);
        }
        return ref;
    }

    private Page createNewPage(String text) {
        Page page = new Page();
        page.setTitle(text.substring(0, Math.min(text.length(), 254)));
        page.setSpace(this.page.getSpace());
        page.setCreator(this._user);
        page.setPosition(Integer.valueOf(this._nodeIndex));
        return page;
    }
}

