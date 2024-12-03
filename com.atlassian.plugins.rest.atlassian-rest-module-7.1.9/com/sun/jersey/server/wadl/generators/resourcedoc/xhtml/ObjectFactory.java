/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRegistry
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.xhtml;

import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.XhtmlElementType;
import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.XhtmlValueType;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public XhtmlElementType createXhtmlElementType() {
        return new XhtmlElementType();
    }

    public XhtmlValueType createXhtmlCodeType() {
        return new XhtmlValueType();
    }
}

