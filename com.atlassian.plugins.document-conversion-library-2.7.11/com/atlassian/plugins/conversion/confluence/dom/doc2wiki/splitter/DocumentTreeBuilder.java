/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Bookmark
 *  com.aspose.words.BookmarkCollection
 *  com.aspose.words.BookmarkStart
 *  com.aspose.words.DocumentVisitor
 *  com.aspose.words.Paragraph
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter;

import com.aspose.words.Bookmark;
import com.aspose.words.BookmarkCollection;
import com.aspose.words.BookmarkStart;
import com.aspose.words.DocumentVisitor;
import com.aspose.words.Paragraph;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import java.util.Stack;

public class DocumentTreeBuilder<DocumentType>
extends DocumentVisitor {
    private final Stack<DocumentTreeNode<DocumentType>> _nodeStack;
    private DocumentTreeNode<DocumentType> _currentNode;
    private DocumentTreeNode<DocumentType> _lastNode;
    private int _currentLvl;
    private final BookmarkInfo<DocumentType> _bookmarks;

    public DocumentTreeBuilder(DocumentTreeNode<DocumentType> rootNode) {
        this._currentLvl = rootNode.getLvl();
        this._currentNode = rootNode;
        this._lastNode = null;
        this._nodeStack = new Stack();
        this._bookmarks = new BookmarkInfo();
    }

    public int visitParagraphStart(Paragraph paragraph) throws Exception {
        int headingLvl = 0;
        String text = paragraph.toString(70).trim();
        if (text.length() == 0) {
            return 0;
        }
        switch (paragraph.getParagraphFormat().getStyleIdentifier()) {
            case 1: {
                headingLvl = 1;
                break;
            }
            case 2: {
                headingLvl = 2;
                break;
            }
            case 3: {
                headingLvl = 3;
                break;
            }
            case 4: {
                headingLvl = 4;
                break;
            }
            case 5: {
                headingLvl = 5;
                break;
            }
            case 6: {
                headingLvl = 6;
                break;
            }
            case 7: {
                headingLvl = 7;
                break;
            }
            case 8: {
                headingLvl = 8;
                break;
            }
            case 9: {
                headingLvl = 9;
            }
        }
        if (headingLvl > 0) {
            BookmarkCollection bkmks = paragraph.getRange().getBookmarks();
            for (Bookmark bkmk : bkmks) {
                this._bookmarks.setInHeading(bkmk.getName());
            }
            DocumentTreeNode<DocumentType> newNode = new DocumentTreeNode<DocumentType>(text, headingLvl);
            if (headingLvl > this._currentLvl) {
                if (this._lastNode != null) {
                    this._nodeStack.push(this._currentNode);
                    this._currentNode = this._lastNode;
                }
            } else if (headingLvl < this._currentLvl) {
                while (this._currentNode.getLvl() >= headingLvl) {
                    this._currentNode = this._nodeStack.pop();
                }
            }
            this._currentNode.addChild(newNode);
            newNode.setParent(this._currentNode);
            this._lastNode = newNode;
            this._currentLvl = headingLvl;
        }
        return 0;
    }

    public int visitBookmarkStart(BookmarkStart bkmk) {
        String name = bkmk.getBookmark().getName();
        this._bookmarks.put(name, this._lastNode != null ? this._lastNode : this._currentNode);
        return 0;
    }

    public BookmarkInfo<DocumentType> getBookmarks() {
        return this._bookmarks;
    }
}

