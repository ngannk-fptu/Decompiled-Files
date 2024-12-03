/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import java.util.Locale;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLElementImpl
extends ElementImpl
implements HTMLElement {
    private static final long serialVersionUID = 5283925246324423495L;

    public HTMLElementImpl(HTMLDocumentImpl hTMLDocumentImpl, String string) {
        super(hTMLDocumentImpl, string.toUpperCase(Locale.ENGLISH));
    }

    @Override
    public String getId() {
        return this.getAttribute("id");
    }

    @Override
    public void setId(String string) {
        this.setAttribute("id", string);
    }

    @Override
    public String getTitle() {
        return this.getAttribute("title");
    }

    @Override
    public void setTitle(String string) {
        this.setAttribute("title", string);
    }

    @Override
    public String getLang() {
        return this.getAttribute("lang");
    }

    @Override
    public void setLang(String string) {
        this.setAttribute("lang", string);
    }

    @Override
    public String getDir() {
        return this.getAttribute("dir");
    }

    @Override
    public void setDir(String string) {
        this.setAttribute("dir", string);
    }

    @Override
    public String getClassName() {
        return this.getAttribute("class");
    }

    @Override
    public void setClassName(String string) {
        this.setAttribute("class", string);
    }

    int getInteger(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    boolean getBinary(String string) {
        return this.getAttributeNode(string) != null;
    }

    void setAttribute(String string, boolean bl) {
        if (bl) {
            this.setAttribute(string, string);
        } else {
            this.removeAttribute(string);
        }
    }

    @Override
    public Attr getAttributeNode(String string) {
        return super.getAttributeNode(string.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Attr getAttributeNodeNS(String string, String string2) {
        if (string != null && string.length() > 0) {
            return super.getAttributeNodeNS(string, string2);
        }
        return super.getAttributeNode(string2.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getAttribute(String string) {
        return super.getAttribute(string.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String getAttributeNS(String string, String string2) {
        if (string != null && string.length() > 0) {
            return super.getAttributeNS(string, string2);
        }
        return super.getAttribute(string2.toLowerCase(Locale.ENGLISH));
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

    String capitalize(String string) {
        char[] cArray = string.toCharArray();
        if (cArray.length > 0) {
            cArray[0] = Character.toUpperCase(cArray[0]);
            for (int i = 1; i < cArray.length; ++i) {
                cArray[i] = Character.toLowerCase(cArray[i]);
            }
            return String.valueOf(cArray);
        }
        return string;
    }

    String getCapitalized(String string) {
        char[] cArray;
        String string2 = this.getAttribute(string);
        if (string2 != null && (cArray = string2.toCharArray()).length > 0) {
            cArray[0] = Character.toUpperCase(cArray[0]);
            for (int i = 1; i < cArray.length; ++i) {
                cArray[i] = Character.toLowerCase(cArray[i]);
            }
            return String.valueOf(cArray);
        }
        return string2;
    }

    public HTMLFormElement getForm() {
        for (Node node = this.getParentNode(); node != null; node = node.getParentNode()) {
            if (!(node instanceof HTMLFormElement)) continue;
            return (HTMLFormElement)node;
        }
        return null;
    }
}

