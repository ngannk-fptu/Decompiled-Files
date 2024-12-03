/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import org.htmlunit.cyberneko.html.dom.HTMLAnchorElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLAppletElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLAreaElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLBRElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLBaseElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLBaseFontElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLBodyElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLButtonElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLCollectionImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDListElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDirectoryElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLDivElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFieldSetElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFontElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFrameElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFrameSetElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLHRElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLHeadElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLHeadingElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLHtmlElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLIFrameElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLImageElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLInputElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLIsIndexElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLLIElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLLabelElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLLegendElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLLinkElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLMapElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLMenuElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLMetaElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLModElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLOListElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLObjectElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLOptGroupElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLOptionElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLParagraphElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLParamElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLPreElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLQuoteElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLScriptElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLSelectElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLStyleElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableCaptionElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableCellElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableColElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableRowElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTableSectionElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTextAreaElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLTitleElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLUListElementImpl;
import org.htmlunit.cyberneko.html.dom.NameNodeListImpl;
import org.htmlunit.cyberneko.xerces.dom.DocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFrameSetElement;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLHtmlElement;
import org.w3c.dom.html.HTMLTitleElement;

public class HTMLDocumentImpl
extends DocumentImpl
implements HTMLDocument {
    private HTMLCollectionImpl anchors_;
    private HTMLCollectionImpl forms_;
    private HTMLCollectionImpl images_;
    private HTMLCollectionImpl links_;
    private HTMLCollectionImpl applets_;
    private StringWriter writer_;
    private static final HashMap<String, Class<? extends HTMLElementImpl>> elementTypesHTML_ = new HashMap();
    private static final Class<?>[] elemClassSigHTML_ = new Class[]{HTMLDocumentImpl.class, String.class};

    @Override
    public synchronized Element getDocumentElement() {
        Node html;
        for (html = this.getFirstChild(); html != null; html = html.getNextSibling()) {
            if (!(html instanceof HTMLHtmlElement)) continue;
            return (HTMLElement)html;
        }
        html = new HTMLHtmlElementImpl(this, "HTML");
        Node child = this.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            html.appendChild(child);
            child = next;
        }
        this.appendChild(html);
        return (HTMLElement)html;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized HTMLElement getHead() {
        Node head;
        Element html;
        Element element = html = this.getDocumentElement();
        synchronized (element) {
            for (head = html.getFirstChild(); head != null && !(head instanceof HTMLHeadElement); head = head.getNextSibling()) {
            }
            if (head != null) {
                Node node = head;
                synchronized (node) {
                    Node child = html.getFirstChild();
                    while (child != null && child != head) {
                        Node next = child.getNextSibling();
                        head.insertBefore(child, head.getFirstChild());
                        child = next;
                    }
                }
                return (HTMLElement)head;
            }
            head = new HTMLHeadElementImpl(this, "HEAD");
            html.insertBefore(head, html.getFirstChild());
        }
        return (HTMLElement)head;
    }

    @Override
    public synchronized String getTitle() {
        HTMLElement head = this.getHead();
        NodeList list = head.getElementsByTagName("TITLE");
        if (list.getLength() > 0) {
            Node title = list.item(0);
            return ((HTMLTitleElement)title).getText();
        }
        return "";
    }

    @Override
    public synchronized void setTitle(String newTitle) {
        HTMLElement head = this.getHead();
        NodeList list = head.getElementsByTagName("TITLE");
        if (list.getLength() > 0) {
            Node title = list.item(0);
            if (title.getParentNode() != head) {
                head.appendChild(title);
            }
            ((HTMLTitleElement)title).setText(newTitle);
        } else {
            HTMLTitleElementImpl title = new HTMLTitleElementImpl(this, "TITLE");
            ((HTMLTitleElement)title).setText(newTitle);
            head.appendChild(title);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized HTMLElement getBody() {
        Node body;
        Element html = this.getDocumentElement();
        HTMLElement head = this.getHead();
        Element element = html;
        synchronized (element) {
            for (body = head.getNextSibling(); body != null && !(body instanceof HTMLBodyElement) && !(body instanceof HTMLFrameSetElement); body = body.getNextSibling()) {
            }
            if (body != null) {
                Node node = body;
                synchronized (node) {
                    Node child = head.getNextSibling();
                    while (child != null && child != body) {
                        Node next = child.getNextSibling();
                        body.insertBefore(child, body.getFirstChild());
                        child = next;
                    }
                }
                return (HTMLElement)body;
            }
            body = new HTMLBodyElementImpl(this, "BODY");
            html.appendChild(body);
        }
        return (HTMLElement)body;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void setBody(HTMLElement newBody) {
        HTMLElement hTMLElement = newBody;
        synchronized (hTMLElement) {
            Element html = this.getDocumentElement();
            HTMLElement head = this.getHead();
            Element element = html;
            synchronized (element) {
                NodeList list = this.getElementsByTagName("BODY");
                if (list.getLength() > 0) {
                    Node body;
                    Node node = body = list.item(0);
                    synchronized (node) {
                        for (Node child = head; child != null; child = child.getNextSibling()) {
                            if (!(child instanceof Element)) continue;
                            if (child != body) {
                                html.insertBefore(newBody, child);
                            } else {
                                html.replaceChild(newBody, body);
                            }
                            return;
                        }
                        html.appendChild(newBody);
                    }
                    return;
                }
                html.appendChild(newBody);
            }
        }
    }

    @Override
    public synchronized Element getElementById(String elementId) {
        Element idElement = super.getElementById(elementId);
        if (idElement != null) {
            return idElement;
        }
        return this.getElementById(elementId, this);
    }

    @Override
    public NodeList getElementsByName(String elementname) {
        return new NameNodeListImpl(this, elementname);
    }

    @Override
    public final NodeList getElementsByTagName(String tagName) {
        return super.getElementsByTagName(tagName.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        if (namespaceURI != null && namespaceURI.length() > 0) {
            return super.getElementsByTagNameNS(namespaceURI, localName.toUpperCase(Locale.ENGLISH));
        }
        return super.getElementsByTagName(localName.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
        return this.createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedname) {
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return this.createElement(qualifiedname);
        }
        return super.createElementNS(namespaceURI, qualifiedname);
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        Class<? extends HTMLElementImpl> elemClass = elementTypesHTML_.get(tagName = tagName.toUpperCase(Locale.ENGLISH));
        if (elemClass != null) {
            try {
                Constructor<? extends HTMLElementImpl> cnst = elemClass.getConstructor(elemClassSigHTML_);
                return cnst.newInstance(this, tagName);
            }
            catch (Exception e) {
                throw new IllegalStateException("HTM15 Tag '" + tagName + "' associated with an Element class that failed to construct.\n" + tagName, e);
            }
        }
        return new HTMLElementImpl(this, tagName);
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        return super.createAttribute(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getReferrer() {
        return null;
    }

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getCookie() {
        return null;
    }

    @Override
    public void setCookie(String cookie) {
    }

    @Override
    public HTMLCollection getImages() {
        if (this.images_ == null) {
            this.images_ = new HTMLCollectionImpl(this.getBody(), 3);
        }
        return this.images_;
    }

    @Override
    public HTMLCollection getApplets() {
        if (this.applets_ == null) {
            this.applets_ = new HTMLCollectionImpl(this.getBody(), 4);
        }
        return this.applets_;
    }

    @Override
    public HTMLCollection getLinks() {
        if (this.links_ == null) {
            this.links_ = new HTMLCollectionImpl(this.getBody(), 5);
        }
        return this.links_;
    }

    @Override
    public HTMLCollection getForms() {
        if (this.forms_ == null) {
            this.forms_ = new HTMLCollectionImpl(this.getBody(), 2);
        }
        return this.forms_;
    }

    @Override
    public HTMLCollection getAnchors() {
        if (this.anchors_ == null) {
            this.anchors_ = new HTMLCollectionImpl(this.getBody(), 1);
        }
        return this.anchors_;
    }

    @Override
    public void open() {
        if (this.writer_ == null) {
            this.writer_ = new StringWriter();
        }
    }

    @Override
    public void close() {
        if (this.writer_ != null) {
            this.writer_ = null;
        }
    }

    @Override
    public void write(String text) {
        if (this.writer_ != null) {
            this.writer_.write(text);
        }
    }

    @Override
    public void writeln(String text) {
        if (this.writer_ != null) {
            this.writer_.write(text + "\n");
        }
    }

    @Override
    public Node cloneNode(boolean deep) {
        HTMLDocumentImpl newdoc = new HTMLDocumentImpl();
        this.cloneNode(newdoc, deep);
        return newdoc;
    }

    @Override
    protected boolean canRenameElements(String newNamespaceURI, String newNodeName, ElementImpl el) {
        Class<? extends HTMLElementImpl> oldClass;
        if (el.getNamespaceURI() != null) {
            return newNamespaceURI != null;
        }
        Class<? extends HTMLElementImpl> newClass = elementTypesHTML_.get(newNodeName.toUpperCase(Locale.ENGLISH));
        return newClass == (oldClass = elementTypesHTML_.get(el.getTagName()));
    }

    private Element getElementById(String elementId, Node node) {
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Element)) continue;
            if (elementId.equals(((Element)child).getAttribute("id"))) {
                return (Element)child;
            }
            Element result = this.getElementById(elementId, child);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    static {
        elementTypesHTML_.put("A", HTMLAnchorElementImpl.class);
        elementTypesHTML_.put("APPLET", HTMLAppletElementImpl.class);
        elementTypesHTML_.put("AREA", HTMLAreaElementImpl.class);
        elementTypesHTML_.put("BASE", HTMLBaseElementImpl.class);
        elementTypesHTML_.put("BASEFONT", HTMLBaseFontElementImpl.class);
        elementTypesHTML_.put("BLOCKQUOTE", HTMLQuoteElementImpl.class);
        elementTypesHTML_.put("BODY", HTMLBodyElementImpl.class);
        elementTypesHTML_.put("BR", HTMLBRElementImpl.class);
        elementTypesHTML_.put("BUTTON", HTMLButtonElementImpl.class);
        elementTypesHTML_.put("DEL", HTMLModElementImpl.class);
        elementTypesHTML_.put("DIR", HTMLDirectoryElementImpl.class);
        elementTypesHTML_.put("DIV", HTMLDivElementImpl.class);
        elementTypesHTML_.put("DL", HTMLDListElementImpl.class);
        elementTypesHTML_.put("FIELDSET", HTMLFieldSetElementImpl.class);
        elementTypesHTML_.put("FONT", HTMLFontElementImpl.class);
        elementTypesHTML_.put("FORM", HTMLFormElementImpl.class);
        elementTypesHTML_.put("FRAME", HTMLFrameElementImpl.class);
        elementTypesHTML_.put("FRAMESET", HTMLFrameSetElementImpl.class);
        elementTypesHTML_.put("HEAD", HTMLHeadElementImpl.class);
        elementTypesHTML_.put("H1", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("H2", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("H3", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("H4", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("H5", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("H6", HTMLHeadingElementImpl.class);
        elementTypesHTML_.put("HR", HTMLHRElementImpl.class);
        elementTypesHTML_.put("HTML", HTMLHtmlElementImpl.class);
        elementTypesHTML_.put("IFRAME", HTMLIFrameElementImpl.class);
        elementTypesHTML_.put("IMG", HTMLImageElementImpl.class);
        elementTypesHTML_.put("INPUT", HTMLInputElementImpl.class);
        elementTypesHTML_.put("INS", HTMLModElementImpl.class);
        elementTypesHTML_.put("ISINDEX", HTMLIsIndexElementImpl.class);
        elementTypesHTML_.put("LABEL", HTMLLabelElementImpl.class);
        elementTypesHTML_.put("LEGEND", HTMLLegendElementImpl.class);
        elementTypesHTML_.put("LI", HTMLLIElementImpl.class);
        elementTypesHTML_.put("LINK", HTMLLinkElementImpl.class);
        elementTypesHTML_.put("MAP", HTMLMapElementImpl.class);
        elementTypesHTML_.put("MENU", HTMLMenuElementImpl.class);
        elementTypesHTML_.put("META", HTMLMetaElementImpl.class);
        elementTypesHTML_.put("OBJECT", HTMLObjectElementImpl.class);
        elementTypesHTML_.put("OL", HTMLOListElementImpl.class);
        elementTypesHTML_.put("OPTGROUP", HTMLOptGroupElementImpl.class);
        elementTypesHTML_.put("OPTION", HTMLOptionElementImpl.class);
        elementTypesHTML_.put("P", HTMLParagraphElementImpl.class);
        elementTypesHTML_.put("PARAM", HTMLParamElementImpl.class);
        elementTypesHTML_.put("PRE", HTMLPreElementImpl.class);
        elementTypesHTML_.put("Q", HTMLQuoteElementImpl.class);
        elementTypesHTML_.put("SCRIPT", HTMLScriptElementImpl.class);
        elementTypesHTML_.put("SELECT", HTMLSelectElementImpl.class);
        elementTypesHTML_.put("STYLE", HTMLStyleElementImpl.class);
        elementTypesHTML_.put("TABLE", HTMLTableElementImpl.class);
        elementTypesHTML_.put("CAPTION", HTMLTableCaptionElementImpl.class);
        elementTypesHTML_.put("TD", HTMLTableCellElementImpl.class);
        elementTypesHTML_.put("TH", HTMLTableCellElementImpl.class);
        elementTypesHTML_.put("COL", HTMLTableColElementImpl.class);
        elementTypesHTML_.put("COLGROUP", HTMLTableColElementImpl.class);
        elementTypesHTML_.put("TR", HTMLTableRowElementImpl.class);
        elementTypesHTML_.put("TBODY", HTMLTableSectionElementImpl.class);
        elementTypesHTML_.put("THEAD", HTMLTableSectionElementImpl.class);
        elementTypesHTML_.put("TFOOT", HTMLTableSectionElementImpl.class);
        elementTypesHTML_.put("TEXTAREA", HTMLTextAreaElementImpl.class);
        elementTypesHTML_.put("TITLE", HTMLTitleElementImpl.class);
        elementTypesHTML_.put("UL", HTMLUListElementImpl.class);
    }
}

