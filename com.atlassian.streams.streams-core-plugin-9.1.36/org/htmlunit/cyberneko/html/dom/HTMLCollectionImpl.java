/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.CollectionIndex;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLAppletElement;
import org.w3c.dom.html.HTMLAreaElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLObjectElement;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableSectionElement;

class HTMLCollectionImpl
implements HTMLCollection {
    static final short ANCHOR = 1;
    static final short FORM = 2;
    static final short IMAGE = 3;
    static final short APPLET = 4;
    static final short LINK = 5;
    static final short OPTION = 6;
    static final short ROW = 7;
    static final short ELEMENT = 8;
    static final short AREA = -1;
    static final short TBODY = -2;
    static final short CELL = -3;
    private final short lookingFor_;
    private final Element topLevel_;

    HTMLCollectionImpl(HTMLElement topLevel, short lookingFor) {
        if (topLevel == null) {
            throw new NullPointerException("HTM011 Argument 'topLevel' is null.");
        }
        this.topLevel_ = topLevel;
        this.lookingFor_ = lookingFor;
    }

    @Override
    public final int getLength() {
        return this.getLength(this.topLevel_);
    }

    @Override
    public final Node item(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("HTM012 Argument 'index' is negative.");
        }
        return this.item(this.topLevel_, new CollectionIndex(index));
    }

    @Override
    public final Node namedItem(String name) {
        if (name == null) {
            throw new NullPointerException("HTM013 Argument 'name' is null.");
        }
        return this.namedItem(this.topLevel_, name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getLength(Element topLevel) {
        int length;
        Element element = topLevel;
        synchronized (element) {
            length = 0;
            for (Node node = topLevel.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, null)) {
                    ++length;
                    continue;
                }
                if (!this.recurse()) continue;
                length += this.getLength((Element)node);
            }
        }
        return length;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node item(Element topLevel, CollectionIndex index) {
        Element element = topLevel;
        synchronized (element) {
            for (Node node = topLevel.getFirstChild(); node != null; node = node.getNextSibling()) {
                Node result;
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, null)) {
                    if (index.isZero()) {
                        return node;
                    }
                    index.decrement();
                    continue;
                }
                if (!this.recurse() || (result = this.item((Element)node, index)) == null) continue;
                return result;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node namedItem(Element topLevel, String name) {
        Element element = topLevel;
        synchronized (element) {
            Node node;
            for (node = topLevel.getFirstChild(); node != null; node = node.getNextSibling()) {
                Node result;
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, name)) {
                    return node;
                }
                if (!this.recurse() || (result = this.namedItem((Element)node, name)) == null) continue;
                return result;
            }
            return node;
        }
    }

    protected boolean recurse() {
        return this.lookingFor_ > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean collectionMatch(Element elem, String name) {
        boolean match;
        Element element = elem;
        synchronized (element) {
            match = false;
            switch (this.lookingFor_) {
                case 1: {
                    match = elem instanceof HTMLAnchorElement && elem.getAttribute("name").length() > 0;
                    break;
                }
                case 2: {
                    match = elem instanceof HTMLFormElement;
                    break;
                }
                case 3: {
                    match = elem instanceof HTMLImageElement;
                    break;
                }
                case 4: {
                    match = elem instanceof HTMLAppletElement || elem instanceof HTMLObjectElement && ("application/java".equals(elem.getAttribute("codetype")) || elem.getAttribute("classid").startsWith("java:"));
                    break;
                }
                case 8: {
                    match = elem instanceof HTMLFormControl;
                    break;
                }
                case 5: {
                    match = (elem instanceof HTMLAnchorElement || elem instanceof HTMLAreaElement) && elem.getAttribute("href").length() > 0;
                    break;
                }
                case -1: {
                    match = elem instanceof HTMLAreaElement;
                    break;
                }
                case 6: {
                    match = elem instanceof HTMLOptionElement;
                    break;
                }
                case 7: {
                    match = elem instanceof HTMLTableRowElement;
                    break;
                }
                case -2: {
                    match = elem instanceof HTMLTableSectionElement && elem.getTagName().equals("TBODY");
                    break;
                }
                case -3: {
                    match = elem instanceof HTMLTableCellElement;
                }
            }
            if (match && name != null) {
                if (elem instanceof HTMLAnchorElement && name.equals(elem.getAttribute("name"))) {
                    return true;
                }
                match = name.equals(elem.getAttribute("id"));
            }
        }
        return match;
    }
}

