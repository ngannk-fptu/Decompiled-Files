/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import java.util.Locale;
import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLElementImpl
extends ElementImpl
implements HTMLElement {
    public HTMLElementImpl(HTMLDocumentImpl owner, String tagName) {
        super(owner, tagName.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }

    @Override
    public void setId(String id) {
        this.setAttribute("id", id);
    }

    @Override
    public String getTitle() {
        return this.getAttribute("title");
    }

    @Override
    public void setTitle(String title) {
        this.setAttribute("title", title);
    }

    @Override
    public String getLang() {
        return this.getAttribute("lang");
    }

    @Override
    public void setLang(String lang) {
        this.setAttribute("lang", lang);
    }

    @Override
    public String getDir() {
        return this.getAttribute("dir");
    }

    @Override
    public void setDir(String dir) {
        this.setAttribute("dir", dir);
    }

    @Override
    public String getClassName() {
        return this.getAttribute("class");
    }

    @Override
    public void setClassName(String classname) {
        this.setAttribute("class", classname);
    }

    int getInteger(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException except) {
            return 0;
        }
    }

    boolean getBinary(String attrName) {
        return this.getAttributeNode(attrName) != null;
    }

    void setAttribute(String name, boolean value) {
        if (value) {
            this.setAttribute(name, name);
        } else {
            this.removeAttribute(name);
        }
    }

    @Override
    public Attr getAttributeNode(String attrName) {
        return super.getAttributeNode(attrName.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        if (namespaceURI != null && namespaceURI.length() > 0) {
            return super.getAttributeNodeNS(namespaceURI, localName);
        }
        return super.getAttributeNode(localName.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getAttribute(String attrName) {
        return super.getAttribute(attrName.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        if (namespaceURI != null && namespaceURI.length() > 0) {
            return super.getAttributeNS(namespaceURI, localName);
        }
        return super.getAttribute(localName.toLowerCase(Locale.ENGLISH));
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

    String capitalize(String value) {
        char[] chars = value.toCharArray();
        if (chars.length > 0) {
            chars[0] = Character.toUpperCase(chars[0]);
            for (int i = 1; i < chars.length; ++i) {
                chars[i] = Character.toLowerCase(chars[i]);
            }
            return String.valueOf(chars);
        }
        return value;
    }

    String getCapitalized(String attrname) {
        char[] chars;
        String value = this.getAttribute(attrname);
        if (value != null && (chars = value.toCharArray()).length > 0) {
            chars[0] = Character.toUpperCase(chars[0]);
            for (int i = 1; i < chars.length; ++i) {
                chars[i] = Character.toLowerCase(chars[i]);
            }
            return String.valueOf(chars);
        }
        return value;
    }

    public HTMLFormElement getForm() {
        for (Node parent = this.getParentNode(); parent != null; parent = parent.getParentNode()) {
            if (!(parent instanceof HTMLFormElement)) continue;
            return (HTMLFormElement)parent;
        }
        return null;
    }
}

