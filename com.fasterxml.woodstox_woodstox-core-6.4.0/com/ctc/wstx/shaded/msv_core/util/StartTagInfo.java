/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.util.IDContextProviderWrapper;
import org.xml.sax.Attributes;

public class StartTagInfo {
    public String namespaceURI;
    public String localName;
    public String qName;
    public Attributes attributes;
    public IDContextProvider2 context;

    protected StartTagInfo() {
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider context) {
        this.reinit(namespaceURI, localName, qName, attributes, context);
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider2 context) {
        this.reinit(namespaceURI, localName, qName, attributes, context);
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes) {
        this.reinit(namespaceURI, localName, qName, attributes, (IDContextProvider2)null);
    }

    public void reinit(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider context) {
        this.reinit(namespaceURI, localName, qName, attributes, IDContextProviderWrapper.create(context));
    }

    public void reinit(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider2 context) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.attributes = attributes;
        this.context = context;
    }

    public final boolean containsAttribute(String attrName) {
        return this.containsAttribute("", attrName);
    }

    public final boolean containsAttribute(String namespaceURI, String attrName) {
        return this.attributes.getIndex(namespaceURI, attrName) != -1;
    }

    public final String getAttribute(String attrName) {
        return this.getAttribute("", attrName);
    }

    public final String getAttribute(String namespaceURI, String attrName) {
        return this.attributes.getValue(namespaceURI, attrName);
    }

    public final String getCollapsedAttribute(String attrName) {
        String s = this.getAttribute(attrName);
        if (s == null) {
            return null;
        }
        return WhiteSpaceProcessor.collapse(s);
    }

    public final String getDefaultedAttribute(String attrName, String defaultValue) {
        return this.getDefaultedAttribute("", attrName, defaultValue);
    }

    public final String getDefaultedAttribute(String namespaceURI, String attrName, String defaultValue) {
        String v = this.getAttribute(namespaceURI, attrName);
        if (v != null) {
            return v;
        }
        return defaultValue;
    }
}

