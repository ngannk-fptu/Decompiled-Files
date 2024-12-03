/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.AttributeBase;
import com.sun.xml.fastinfoset.stax.events.Util;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceBase
extends AttributeBase
implements Namespace {
    static final String DEFAULT_NS_PREFIX = "";
    static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";
    static final String XML_NS_PREFIX = "xml";
    static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";
    static final String XMLNS_ATTRIBUTE = "xmlns";
    static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
    static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private boolean defaultDeclaration = false;

    public NamespaceBase(String namespaceURI) {
        super(XMLNS_ATTRIBUTE, DEFAULT_NS_PREFIX, namespaceURI);
        this.setEventType(13);
    }

    public NamespaceBase(String prefix, String namespaceURI) {
        super(XMLNS_ATTRIBUTE, prefix, namespaceURI);
        this.setEventType(13);
        if (Util.isEmptyString(prefix)) {
            this.defaultDeclaration = true;
        }
    }

    void setPrefix(String prefix) {
        if (prefix == null) {
            this.setName(new QName(XMLNS_ATTRIBUTE_NS_URI, DEFAULT_NS_PREFIX, XMLNS_ATTRIBUTE));
        } else {
            this.setName(new QName(XMLNS_ATTRIBUTE_NS_URI, prefix, XMLNS_ATTRIBUTE));
        }
    }

    @Override
    public String getPrefix() {
        if (this.defaultDeclaration) {
            return DEFAULT_NS_PREFIX;
        }
        return super.getLocalName();
    }

    void setNamespaceURI(String uri) {
        this.setValue(uri);
    }

    @Override
    public String getNamespaceURI() {
        return this.getValue();
    }

    @Override
    public boolean isNamespace() {
        return true;
    }

    @Override
    public boolean isDefaultNamespaceDeclaration() {
        return this.defaultDeclaration;
    }
}

