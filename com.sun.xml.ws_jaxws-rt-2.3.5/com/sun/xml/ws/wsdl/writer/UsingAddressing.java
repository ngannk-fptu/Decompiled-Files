/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.annotation.XmlAttribute
 *  com.sun.xml.txw2.annotation.XmlElement
 */
package com.sun.xml.ws.wsdl.writer;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.ws.wsdl.writer.document.StartWithExtensionsType;

@XmlElement(value="http://www.w3.org/2006/05/addressing/wsdl", ns="UsingAddressing")
public interface UsingAddressing
extends TypedXmlWriter,
StartWithExtensionsType {
    @XmlAttribute(value="required", ns="http://schemas.xmlsoap.org/wsdl/")
    public void required(boolean var1);
}

