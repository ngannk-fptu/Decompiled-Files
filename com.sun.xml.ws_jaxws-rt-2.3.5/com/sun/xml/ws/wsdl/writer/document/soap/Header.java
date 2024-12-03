/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.annotation.XmlAttribute
 *  com.sun.xml.txw2.annotation.XmlElement
 */
package com.sun.xml.ws.wsdl.writer.document.soap;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.ws.wsdl.writer.document.soap.BodyType;
import com.sun.xml.ws.wsdl.writer.document.soap.HeaderFault;
import javax.xml.namespace.QName;

@XmlElement(value="header")
public interface Header
extends TypedXmlWriter,
BodyType {
    @XmlAttribute
    public Header message(QName var1);

    @XmlElement
    public HeaderFault headerFault();

    @XmlAttribute
    public BodyType part(String var1);
}

