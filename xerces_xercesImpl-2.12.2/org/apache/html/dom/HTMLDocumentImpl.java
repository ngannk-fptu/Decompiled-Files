/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.html.dom.HTMLBodyElementImpl;
import org.apache.html.dom.HTMLCollectionImpl;
import org.apache.html.dom.HTMLElementImpl;
import org.apache.html.dom.HTMLHeadElementImpl;
import org.apache.html.dom.HTMLHtmlElementImpl;
import org.apache.html.dom.HTMLTitleElementImpl;
import org.apache.html.dom.NameNodeListImpl;
import org.apache.html.dom.ObjectFactory;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
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
    private static final long serialVersionUID = 4285791750126227180L;
    private HTMLCollectionImpl _anchors;
    private HTMLCollectionImpl _forms;
    private HTMLCollectionImpl _images;
    private HTMLCollectionImpl _links;
    private HTMLCollectionImpl _applets;
    private StringWriter _writer;
    private static Hashtable _elementTypesHTML;
    private static final Class[] _elemClassSigHTML;

    public HTMLDocumentImpl() {
        HTMLDocumentImpl.populateElementTypes();
    }

    @Override
    public synchronized Element getDocumentElement() {
        Node node;
        for (node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof HTMLHtmlElement)) continue;
            return (HTMLElement)node;
        }
        node = new HTMLHtmlElementImpl(this, "HTML");
        Node node2 = this.getFirstChild();
        while (node2 != null) {
            Node node3 = node2.getNextSibling();
            node.appendChild(node2);
            node2 = node3;
        }
        this.appendChild(node);
        return (HTMLElement)node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized HTMLElement getHead() {
        Node node;
        Element element;
        Element element2 = element = this.getDocumentElement();
        synchronized (element2) {
            for (node = element.getFirstChild(); node != null && !(node instanceof HTMLHeadElement); node = node.getNextSibling()) {
            }
            if (node != null) {
                Node node2 = node;
                synchronized (node2) {
                    Node node3 = element.getFirstChild();
                    while (node3 != null && node3 != node) {
                        Node node4 = node3.getNextSibling();
                        node.insertBefore(node3, node.getFirstChild());
                        node3 = node4;
                    }
                }
                return (HTMLElement)node;
            }
            node = new HTMLHeadElementImpl(this, "HEAD");
            element.insertBefore(node, element.getFirstChild());
        }
        return (HTMLElement)node;
    }

    @Override
    public synchronized String getTitle() {
        HTMLElement hTMLElement = this.getHead();
        NodeList nodeList = hTMLElement.getElementsByTagName("TITLE");
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return ((HTMLTitleElement)node).getText();
        }
        return "";
    }

    @Override
    public synchronized void setTitle(String string) {
        HTMLElement hTMLElement = this.getHead();
        NodeList nodeList = hTMLElement.getElementsByTagName("TITLE");
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node.getParentNode() != hTMLElement) {
                hTMLElement.appendChild(node);
            }
            ((HTMLTitleElement)node).setText(string);
        } else {
            HTMLTitleElementImpl hTMLTitleElementImpl = new HTMLTitleElementImpl(this, "TITLE");
            ((HTMLTitleElement)hTMLTitleElementImpl).setText(string);
            hTMLElement.appendChild(hTMLTitleElementImpl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized HTMLElement getBody() {
        Node node;
        Element element = this.getDocumentElement();
        HTMLElement hTMLElement = this.getHead();
        Element element2 = element;
        synchronized (element2) {
            for (node = hTMLElement.getNextSibling(); node != null && !(node instanceof HTMLBodyElement) && !(node instanceof HTMLFrameSetElement); node = node.getNextSibling()) {
            }
            if (node != null) {
                Node node2 = node;
                synchronized (node2) {
                    Node node3 = hTMLElement.getNextSibling();
                    while (node3 != null && node3 != node) {
                        Node node4 = node3.getNextSibling();
                        node.insertBefore(node3, node.getFirstChild());
                        node3 = node4;
                    }
                }
                return (HTMLElement)node;
            }
            node = new HTMLBodyElementImpl(this, "BODY");
            element.appendChild(node);
        }
        return (HTMLElement)node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void setBody(HTMLElement hTMLElement) {
        HTMLElement hTMLElement2 = hTMLElement;
        synchronized (hTMLElement2) {
            Element element = this.getDocumentElement();
            HTMLElement hTMLElement3 = this.getHead();
            Element element2 = element;
            synchronized (element2) {
                NodeList nodeList = this.getElementsByTagName("BODY");
                if (nodeList.getLength() > 0) {
                    Node node;
                    Node node2 = node = nodeList.item(0);
                    synchronized (node2) {
                        for (Node node3 = hTMLElement3; node3 != null; node3 = node3.getNextSibling()) {
                            if (!(node3 instanceof Element)) continue;
                            if (node3 != node) {
                                element.insertBefore(hTMLElement, node3);
                            } else {
                                element.replaceChild(hTMLElement, node);
                            }
                            return;
                        }
                        element.appendChild(hTMLElement);
                    }
                    return;
                }
                element.appendChild(hTMLElement);
            }
        }
    }

    @Override
    public synchronized Element getElementById(String string) {
        Element element = super.getElementById(string);
        if (element != null) {
            return element;
        }
        return this.getElementById(string, this);
    }

    @Override
    public NodeList getElementsByName(String string) {
        return new NameNodeListImpl(this, string);
    }

    @Override
    public final NodeList getElementsByTagName(String string) {
        return super.getElementsByTagName(string.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public final NodeList getElementsByTagNameNS(String string, String string2) {
        if (string != null && string.length() > 0) {
            return super.getElementsByTagNameNS(string, string2.toUpperCase(Locale.ENGLISH));
        }
        return super.getElementsByTagName(string2.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public Element createElementNS(String string, String string2, String string3) throws DOMException {
        return this.createElementNS(string, string2);
    }

    @Override
    public Element createElementNS(String string, String string2) {
        if (string == null || string.length() == 0) {
            return this.createElement(string2);
        }
        return super.createElementNS(string, string2);
    }

    @Override
    public Element createElement(String string) throws DOMException {
        Class clazz = (Class)_elementTypesHTML.get(string = string.toUpperCase(Locale.ENGLISH));
        if (clazz != null) {
            try {
                Constructor constructor = clazz.getConstructor(_elemClassSigHTML);
                return (Element)constructor.newInstance(this, string);
            }
            catch (Exception exception) {
                throw new IllegalStateException("HTM15 Tag '" + string + "' associated with an Element class that failed to construct.\n" + string);
            }
        }
        return new HTMLElementImpl(this, string);
    }

    @Override
    public Attr createAttribute(String string) throws DOMException {
        return super.createAttribute(string.toLowerCase(Locale.ENGLISH));
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
    public void setCookie(String string) {
    }

    @Override
    public HTMLCollection getImages() {
        if (this._images == null) {
            this._images = new HTMLCollectionImpl(this.getBody(), 3);
        }
        return this._images;
    }

    @Override
    public HTMLCollection getApplets() {
        if (this._applets == null) {
            this._applets = new HTMLCollectionImpl(this.getBody(), 4);
        }
        return this._applets;
    }

    @Override
    public HTMLCollection getLinks() {
        if (this._links == null) {
            this._links = new HTMLCollectionImpl(this.getBody(), 5);
        }
        return this._links;
    }

    @Override
    public HTMLCollection getForms() {
        if (this._forms == null) {
            this._forms = new HTMLCollectionImpl(this.getBody(), 2);
        }
        return this._forms;
    }

    @Override
    public HTMLCollection getAnchors() {
        if (this._anchors == null) {
            this._anchors = new HTMLCollectionImpl(this.getBody(), 1);
        }
        return this._anchors;
    }

    @Override
    public void open() {
        if (this._writer == null) {
            this._writer = new StringWriter();
        }
    }

    @Override
    public void close() {
        if (this._writer != null) {
            this._writer = null;
        }
    }

    @Override
    public void write(String string) {
        if (this._writer != null) {
            this._writer.write(string);
        }
    }

    @Override
    public void writeln(String string) {
        if (this._writer != null) {
            this._writer.write(string + "\n");
        }
    }

    @Override
    public Node cloneNode(boolean bl) {
        HTMLDocumentImpl hTMLDocumentImpl = new HTMLDocumentImpl();
        this.callUserDataHandlers(this, hTMLDocumentImpl, (short)1);
        this.cloneNode(hTMLDocumentImpl, bl);
        return hTMLDocumentImpl;
    }

    @Override
    protected boolean canRenameElements(String string, String string2, ElementImpl elementImpl) {
        Class clazz;
        if (elementImpl.getNamespaceURI() != null) {
            return string != null;
        }
        Class clazz2 = (Class)_elementTypesHTML.get(string2.toUpperCase(Locale.ENGLISH));
        return clazz2 == (clazz = (Class)_elementTypesHTML.get(elementImpl.getTagName()));
    }

    private Element getElementById(String string, Node node) {
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (!(node2 instanceof Element)) continue;
            if (string.equals(((Element)node2).getAttribute("id"))) {
                return (Element)node2;
            }
            Element element = this.getElementById(string, node2);
            if (element == null) continue;
            return element;
        }
        return null;
    }

    private static synchronized void populateElementTypes() {
        if (_elementTypesHTML != null) {
            return;
        }
        _elementTypesHTML = new Hashtable(63);
        HTMLDocumentImpl.populateElementType("A", "HTMLAnchorElementImpl");
        HTMLDocumentImpl.populateElementType("APPLET", "HTMLAppletElementImpl");
        HTMLDocumentImpl.populateElementType("AREA", "HTMLAreaElementImpl");
        HTMLDocumentImpl.populateElementType("BASE", "HTMLBaseElementImpl");
        HTMLDocumentImpl.populateElementType("BASEFONT", "HTMLBaseFontElementImpl");
        HTMLDocumentImpl.populateElementType("BLOCKQUOTE", "HTMLQuoteElementImpl");
        HTMLDocumentImpl.populateElementType("BODY", "HTMLBodyElementImpl");
        HTMLDocumentImpl.populateElementType("BR", "HTMLBRElementImpl");
        HTMLDocumentImpl.populateElementType("BUTTON", "HTMLButtonElementImpl");
        HTMLDocumentImpl.populateElementType("DEL", "HTMLModElementImpl");
        HTMLDocumentImpl.populateElementType("DIR", "HTMLDirectoryElementImpl");
        HTMLDocumentImpl.populateElementType("DIV", "HTMLDivElementImpl");
        HTMLDocumentImpl.populateElementType("DL", "HTMLDListElementImpl");
        HTMLDocumentImpl.populateElementType("FIELDSET", "HTMLFieldSetElementImpl");
        HTMLDocumentImpl.populateElementType("FONT", "HTMLFontElementImpl");
        HTMLDocumentImpl.populateElementType("FORM", "HTMLFormElementImpl");
        HTMLDocumentImpl.populateElementType("FRAME", "HTMLFrameElementImpl");
        HTMLDocumentImpl.populateElementType("FRAMESET", "HTMLFrameSetElementImpl");
        HTMLDocumentImpl.populateElementType("HEAD", "HTMLHeadElementImpl");
        HTMLDocumentImpl.populateElementType("H1", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("H2", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("H3", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("H4", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("H5", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("H6", "HTMLHeadingElementImpl");
        HTMLDocumentImpl.populateElementType("HR", "HTMLHRElementImpl");
        HTMLDocumentImpl.populateElementType("HTML", "HTMLHtmlElementImpl");
        HTMLDocumentImpl.populateElementType("IFRAME", "HTMLIFrameElementImpl");
        HTMLDocumentImpl.populateElementType("IMG", "HTMLImageElementImpl");
        HTMLDocumentImpl.populateElementType("INPUT", "HTMLInputElementImpl");
        HTMLDocumentImpl.populateElementType("INS", "HTMLModElementImpl");
        HTMLDocumentImpl.populateElementType("ISINDEX", "HTMLIsIndexElementImpl");
        HTMLDocumentImpl.populateElementType("LABEL", "HTMLLabelElementImpl");
        HTMLDocumentImpl.populateElementType("LEGEND", "HTMLLegendElementImpl");
        HTMLDocumentImpl.populateElementType("LI", "HTMLLIElementImpl");
        HTMLDocumentImpl.populateElementType("LINK", "HTMLLinkElementImpl");
        HTMLDocumentImpl.populateElementType("MAP", "HTMLMapElementImpl");
        HTMLDocumentImpl.populateElementType("MENU", "HTMLMenuElementImpl");
        HTMLDocumentImpl.populateElementType("META", "HTMLMetaElementImpl");
        HTMLDocumentImpl.populateElementType("OBJECT", "HTMLObjectElementImpl");
        HTMLDocumentImpl.populateElementType("OL", "HTMLOListElementImpl");
        HTMLDocumentImpl.populateElementType("OPTGROUP", "HTMLOptGroupElementImpl");
        HTMLDocumentImpl.populateElementType("OPTION", "HTMLOptionElementImpl");
        HTMLDocumentImpl.populateElementType("P", "HTMLParagraphElementImpl");
        HTMLDocumentImpl.populateElementType("PARAM", "HTMLParamElementImpl");
        HTMLDocumentImpl.populateElementType("PRE", "HTMLPreElementImpl");
        HTMLDocumentImpl.populateElementType("Q", "HTMLQuoteElementImpl");
        HTMLDocumentImpl.populateElementType("SCRIPT", "HTMLScriptElementImpl");
        HTMLDocumentImpl.populateElementType("SELECT", "HTMLSelectElementImpl");
        HTMLDocumentImpl.populateElementType("STYLE", "HTMLStyleElementImpl");
        HTMLDocumentImpl.populateElementType("TABLE", "HTMLTableElementImpl");
        HTMLDocumentImpl.populateElementType("CAPTION", "HTMLTableCaptionElementImpl");
        HTMLDocumentImpl.populateElementType("TD", "HTMLTableCellElementImpl");
        HTMLDocumentImpl.populateElementType("TH", "HTMLTableCellElementImpl");
        HTMLDocumentImpl.populateElementType("COL", "HTMLTableColElementImpl");
        HTMLDocumentImpl.populateElementType("COLGROUP", "HTMLTableColElementImpl");
        HTMLDocumentImpl.populateElementType("TR", "HTMLTableRowElementImpl");
        HTMLDocumentImpl.populateElementType("TBODY", "HTMLTableSectionElementImpl");
        HTMLDocumentImpl.populateElementType("THEAD", "HTMLTableSectionElementImpl");
        HTMLDocumentImpl.populateElementType("TFOOT", "HTMLTableSectionElementImpl");
        HTMLDocumentImpl.populateElementType("TEXTAREA", "HTMLTextAreaElementImpl");
        HTMLDocumentImpl.populateElementType("TITLE", "HTMLTitleElementImpl");
        HTMLDocumentImpl.populateElementType("UL", "HTMLUListElementImpl");
    }

    private static void populateElementType(String string, String string2) {
        try {
            _elementTypesHTML.put(string, ObjectFactory.findProviderClass("org.apache.html.dom." + string2, HTMLDocumentImpl.class.getClassLoader(), true));
        }
        catch (Exception exception) {
            throw new RuntimeException("HTM019 OpenXML Error: Could not find or execute class " + string2 + " implementing HTML element " + string + "\n" + string2 + "\t" + string);
        }
    }

    static {
        _elemClassSigHTML = new Class[]{HTMLDocumentImpl.class, String.class};
    }
}

