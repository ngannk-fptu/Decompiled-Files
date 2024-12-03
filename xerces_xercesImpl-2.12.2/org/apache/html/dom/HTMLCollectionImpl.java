/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import java.io.Serializable;
import org.apache.html.dom.CollectionIndex;
import org.apache.html.dom.HTMLFormControl;
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
implements HTMLCollection,
Serializable {
    private static final long serialVersionUID = 9112122196669185082L;
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
    private short _lookingFor;
    private Element _topLevel;

    HTMLCollectionImpl(HTMLElement hTMLElement, short s) {
        if (hTMLElement == null) {
            throw new NullPointerException("HTM011 Argument 'topLevel' is null.");
        }
        this._topLevel = hTMLElement;
        this._lookingFor = s;
    }

    @Override
    public final int getLength() {
        return this.getLength(this._topLevel);
    }

    @Override
    public final Node item(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("HTM012 Argument 'index' is negative.");
        }
        return this.item(this._topLevel, new CollectionIndex(n));
    }

    @Override
    public final Node namedItem(String string) {
        if (string == null) {
            throw new NullPointerException("HTM013 Argument 'name' is null.");
        }
        return this.namedItem(this._topLevel, string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getLength(Element element) {
        int n;
        Element element2 = element;
        synchronized (element2) {
            n = 0;
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, null)) {
                    ++n;
                    continue;
                }
                if (!this.recurse()) continue;
                n += this.getLength((Element)node);
            }
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node item(Element element, CollectionIndex collectionIndex) {
        Element element2 = element;
        synchronized (element2) {
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                Node node2;
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, null)) {
                    if (collectionIndex.isZero()) {
                        return node;
                    }
                    collectionIndex.decrement();
                    continue;
                }
                if (!this.recurse() || (node2 = this.item((Element)node, collectionIndex)) == null) continue;
                return node2;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Node namedItem(Element element, String string) {
        Element element2 = element;
        synchronized (element2) {
            Node node;
            for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                Node node2;
                if (!(node instanceof Element)) continue;
                if (this.collectionMatch((Element)node, string)) {
                    return node;
                }
                if (!this.recurse() || (node2 = this.namedItem((Element)node, string)) == null) continue;
                return node2;
            }
            return node;
        }
    }

    protected boolean recurse() {
        return this._lookingFor > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean collectionMatch(Element element, String string) {
        boolean bl;
        Element element2 = element;
        synchronized (element2) {
            bl = false;
            switch (this._lookingFor) {
                case 1: {
                    bl = element instanceof HTMLAnchorElement && element.getAttribute("name").length() > 0;
                    break;
                }
                case 2: {
                    bl = element instanceof HTMLFormElement;
                    break;
                }
                case 3: {
                    bl = element instanceof HTMLImageElement;
                    break;
                }
                case 4: {
                    bl = element instanceof HTMLAppletElement || element instanceof HTMLObjectElement && ("application/java".equals(element.getAttribute("codetype")) || element.getAttribute("classid").startsWith("java:"));
                    break;
                }
                case 8: {
                    bl = element instanceof HTMLFormControl;
                    break;
                }
                case 5: {
                    bl = (element instanceof HTMLAnchorElement || element instanceof HTMLAreaElement) && element.getAttribute("href").length() > 0;
                    break;
                }
                case -1: {
                    bl = element instanceof HTMLAreaElement;
                    break;
                }
                case 6: {
                    bl = element instanceof HTMLOptionElement;
                    break;
                }
                case 7: {
                    bl = element instanceof HTMLTableRowElement;
                    break;
                }
                case -2: {
                    bl = element instanceof HTMLTableSectionElement && element.getTagName().equals("TBODY");
                    break;
                }
                case -3: {
                    bl = element instanceof HTMLTableCellElement;
                }
            }
            if (bl && string != null) {
                if (element instanceof HTMLAnchorElement && string.equals(element.getAttribute("name"))) {
                    return true;
                }
                bl = string.equals(element.getAttribute("id"));
            }
        }
        return bl;
    }
}

