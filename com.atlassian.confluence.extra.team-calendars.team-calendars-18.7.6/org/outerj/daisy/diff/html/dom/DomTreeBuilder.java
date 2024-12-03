/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom;

import java.util.ArrayList;
import java.util.List;
import org.outerj.daisy.diff.html.dom.BodyNode;
import org.outerj.daisy.diff.html.dom.DomTree;
import org.outerj.daisy.diff.html.dom.ImageNode;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.SeparatingNode;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;
import org.outerj.daisy.diff.html.dom.WhiteSpaceNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DomTreeBuilder
extends DefaultHandler
implements DomTree {
    private List<TextNode> textNodes = new ArrayList<TextNode>(50);
    private BodyNode bodyNode = new BodyNode();
    private TagNode currentParent = this.bodyNode;
    private StringBuilder newWord = new StringBuilder();
    protected boolean documentStarted = false;
    protected boolean documentEnded = false;
    protected boolean bodyStarted = false;
    protected boolean bodyEnded = false;
    private boolean whiteSpaceBeforeThis = false;
    private int numberOfActivePreTags = 0;
    private Node lastSibling = null;

    @Override
    public BodyNode getBodyNode() {
        return this.bodyNode;
    }

    @Override
    public List<TextNode> getTextNodes() {
        return this.textNodes;
    }

    @Override
    public void startDocument() throws SAXException {
        if (this.documentStarted) {
            throw new IllegalStateException("This Handler only accepts one document");
        }
        this.documentStarted = true;
    }

    @Override
    public void endDocument() throws SAXException {
        if (!this.documentStarted || this.documentEnded) {
            throw new IllegalStateException();
        }
        this.endWord();
        this.documentEnded = true;
        this.documentStarted = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!this.documentStarted || this.documentEnded) {
            throw new IllegalStateException();
        }
        if (this.bodyStarted && !this.bodyEnded) {
            TagNode newTagNode;
            this.endWord();
            this.currentParent = newTagNode = new TagNode(this.currentParent, localName, attributes);
            this.lastSibling = null;
            if (this.whiteSpaceBeforeThis && newTagNode.isInline()) {
                newTagNode.setWhiteBefore(true);
            }
            this.whiteSpaceBeforeThis = false;
            if (newTagNode.isPre()) {
                ++this.numberOfActivePreTags;
            }
            if (this.isSeparatingTag(newTagNode)) {
                this.addSeparatorNode();
            }
        } else if (!this.bodyStarted && localName.equalsIgnoreCase("body")) {
            this.bodyStarted = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!this.documentStarted || this.documentEnded) {
            throw new IllegalStateException();
        }
        if (localName.equalsIgnoreCase("body")) {
            this.bodyEnded = true;
        } else if (this.bodyStarted && !this.bodyEnded) {
            if (localName.equalsIgnoreCase("img")) {
                ImageNode img = new ImageNode(this.currentParent, this.currentParent.getAttributes());
                img.setWhiteBefore(this.whiteSpaceBeforeThis);
                this.lastSibling = img;
                this.textNodes.add(img);
            }
            this.endWord();
            this.lastSibling = this.currentParent.isInline() ? this.currentParent : null;
            if (localName.equalsIgnoreCase("pre")) {
                --this.numberOfActivePreTags;
            }
            if (this.isSeparatingTag(this.currentParent)) {
                this.addSeparatorNode();
            }
            this.currentParent = this.currentParent.getParent();
            this.whiteSpaceBeforeThis = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!this.documentStarted || this.documentEnded) {
            throw new IllegalStateException();
        }
        for (int i = start; i < start + length; ++i) {
            char c = ch[i];
            if (DomTreeBuilder.isDelimiter(c)) {
                this.endWord();
                if (WhiteSpaceNode.isWhiteSpace(c) && this.numberOfActivePreTags == 0) {
                    if (this.lastSibling != null) {
                        this.lastSibling.setWhiteAfter(true);
                    }
                    this.whiteSpaceBeforeThis = true;
                    continue;
                }
                TextNode textNode = new TextNode(this.currentParent, Character.toString(c));
                textNode.setWhiteBefore(this.whiteSpaceBeforeThis);
                this.whiteSpaceBeforeThis = false;
                this.lastSibling = textNode;
                this.textNodes.add(textNode);
                continue;
            }
            this.newWord.append(c);
        }
    }

    private void endWord() {
        if (this.newWord.length() > 0) {
            TextNode node = new TextNode(this.currentParent, this.newWord.toString());
            node.setWhiteBefore(this.whiteSpaceBeforeThis);
            this.whiteSpaceBeforeThis = false;
            this.lastSibling = node;
            this.textNodes.add(node);
            this.newWord.setLength(0);
        }
    }

    private boolean isSeparatingTag(TagNode aTagNode) {
        return aTagNode.isBlockLevel();
    }

    private void addSeparatorNode() {
        if (this.textNodes.isEmpty()) {
            return;
        }
        if (this.textNodes.get(this.textNodes.size() - 1) instanceof SeparatingNode) {
            return;
        }
        this.textNodes.add(new SeparatingNode(this.currentParent));
    }

    public static boolean isDelimiter(char c) {
        if (WhiteSpaceNode.isWhiteSpace(c)) {
            return true;
        }
        switch (c) {
            case '!': 
            case '\"': 
            case '&': 
            case '\'': 
            case '(': 
            case ')': 
            case '*': 
            case '+': 
            case ',': 
            case '-': 
            case '.': 
            case '/': 
            case ':': 
            case ';': 
            case '=': 
            case '?': 
            case '[': 
            case '\\': 
            case ']': 
            case '_': 
            case '{': 
            case '|': 
            case '}': {
                return true;
            }
        }
        return false;
    }
}

