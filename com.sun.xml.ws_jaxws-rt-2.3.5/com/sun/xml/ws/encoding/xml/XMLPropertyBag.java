/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.encoding.xml;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;

public class XMLPropertyBag
extends BasePropertySet {
    private String contentType;
    private static final BasePropertySet.PropertyMap model = XMLPropertyBag.parse(XMLPropertyBag.class);

    @Override
    protected BasePropertySet.PropertyMap getPropertyMap() {
        return model;
    }

    @PropertySet.Property(value={"com.sun.jaxws.rest.contenttype"})
    public String getXMLContentType() {
        return this.contentType;
    }

    public void setXMLContentType(String content) {
        this.contentType = content;
    }
}

