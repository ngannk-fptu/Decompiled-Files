/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.xhtml;

import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.XhtmlElementType;
import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.XhtmlValueType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class Elements
extends JAXBElement<XhtmlElementType> {
    private static final long serialVersionUID = 1L;

    public static Elements el(String elementName) {
        return Elements.createElement(elementName);
    }

    public static Object val(String elementName, String value) {
        return Elements.createElement(elementName, value);
    }

    public Elements(QName name, Class<XhtmlElementType> clazz, XhtmlElementType element) {
        super(name, clazz, (Object)element);
    }

    public Elements add(Object ... childNodes) {
        if (childNodes != null) {
            for (Object childNode : childNodes) {
                ((XhtmlElementType)this.getValue()).getChildNodes().add(childNode);
            }
        }
        return this;
    }

    public Elements addChild(Object child) {
        ((XhtmlElementType)this.getValue()).getChildNodes().add(child);
        return this;
    }

    private static Elements createElement(String elementName) {
        try {
            XhtmlElementType element = new XhtmlElementType();
            Elements jaxbElement = new Elements(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlElementType.class, element);
            return jaxbElement;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static JAXBElement<XhtmlValueType> createElement(String elementName, String value) {
        try {
            XhtmlValueType element = new XhtmlValueType();
            element.value = value;
            JAXBElement jaxbElement = new JAXBElement(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlValueType.class, (Object)element);
            return jaxbElement;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

