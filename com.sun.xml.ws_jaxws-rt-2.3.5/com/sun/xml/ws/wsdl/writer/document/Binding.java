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
import com.sun.xml.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.ws.wsdl.writer.document.soap.SOAPBinding;
import javax.xml.namespace.QName;

@XmlElement(value="binding")
public interface Binding
extends TypedXmlWriter,
StartWithExtensionsType {
    @XmlAttribute
    public Binding type(QName var1);

    @XmlAttribute
    public Binding name(String var1);

    @XmlElement
    public BindingOperationType operation();

    @XmlElement(value="binding", ns="http://schemas.xmlsoap.org/wsdl/soap/")
    public SOAPBinding soapBinding();

    @XmlElement(value="binding", ns="http://schemas.xmlsoap.org/wsdl/soap12/")
    public com.sun.xml.ws.wsdl.writer.document.soap12.SOAPBinding soap12Binding();
}

