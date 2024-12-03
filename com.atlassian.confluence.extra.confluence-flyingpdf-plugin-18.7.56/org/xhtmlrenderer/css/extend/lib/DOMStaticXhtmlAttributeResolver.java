/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend.lib;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xhtmlrenderer.css.extend.AttributeResolver;

public class DOMStaticXhtmlAttributeResolver
implements AttributeResolver {
    @Override
    public String getAttributeValue(Object e, String attrName) {
        return ((Element)e).getAttribute(attrName);
    }

    @Override
    public String getAttributeValue(Object o, String namespaceURI, String attrName) {
        Element e = (Element)o;
        if (namespaceURI == "") {
            return e.getAttribute(attrName);
        }
        if (namespaceURI == null) {
            if (e.getLocalName() == null) {
                return e.getAttribute(attrName);
            }
            NamedNodeMap attrs = e.getAttributes();
            int l = attrs.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attrs.item(i);
                if (!attrName.equals(attr.getLocalName())) continue;
                return attr.getValue();
            }
            return "";
        }
        return e.getAttributeNS(namespaceURI, attrName);
    }

    @Override
    public String getClass(Object e) {
        return ((Element)e).getAttribute("class");
    }

    @Override
    public String getID(Object e) {
        return ((Element)e).getAttribute("id");
    }

    @Override
    public String getNonCssStyling(Object e) {
        return null;
    }

    @Override
    public String getLang(Object e) {
        return ((Element)e).getAttribute("lang");
    }

    @Override
    public String getElementStyling(Object el) {
        Element e = (Element)el;
        StringBuffer style = new StringBuffer();
        if (e.getNodeName().equals("td")) {
            String s = e.getAttribute("colspan");
            if (!s.equals("")) {
                style.append("-fs-table-cell-colspan: ");
                style.append(s);
                style.append(";");
            }
            if (!(s = e.getAttribute("rowspan")).equals("")) {
                style.append("-fs-table-cell-rowspan: ");
                style.append(s);
                style.append(";");
            }
        }
        style.append(e.getAttribute("style"));
        return style.toString();
    }

    @Override
    public boolean isActive(Object e) {
        return false;
    }

    @Override
    public boolean isFocus(Object e) {
        return false;
    }

    @Override
    public boolean isHover(Object e) {
        return false;
    }

    @Override
    public boolean isLink(Object el) {
        Element e = (Element)el;
        return e.getNodeName().equalsIgnoreCase("a") && !e.getAttribute("href").equals("");
    }

    @Override
    public boolean isVisited(Object e) {
        return false;
    }
}

