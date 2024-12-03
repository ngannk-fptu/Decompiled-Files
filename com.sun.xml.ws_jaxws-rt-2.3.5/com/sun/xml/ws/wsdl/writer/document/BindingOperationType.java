/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.annotation.XmlAttribute
 *  com.sun.xml.txw2.annotation.XmlElement
 */
package com.sun.xml.ws.wsdl.writer.document;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.ws.wsdl.writer.document.Fault;
import com.sun.xml.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.ws.wsdl.writer.document.soap.SOAPOperation;

public interface BindingOperationType
extends TypedXmlWriter,
StartWithExtensionsType {
    @XmlAttribute
    public BindingOperationType name(String var1);

    @XmlElement(value="operation", ns="http://schemas.xmlsoap.org/wsdl/soap/")
    public SOAPOperation soapOperation();

    @XmlElement(value="operation", ns="http://schemas.xmlsoap.org/wsdl/soap12/")
    public com.sun.xml.ws.wsdl.writer.document.soap12.SOAPOperation soap12Operation();

    @XmlElement
    public Fault fault();

    @XmlElement
    public StartWithExtensionsType output();

    @XmlElement
    public StartWithExtensionsType input();
}

