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
import com.sun.xml.ws.wsdl.writer.document.Binding;
import com.sun.xml.ws.wsdl.writer.document.Documented;
import com.sun.xml.ws.wsdl.writer.document.Import;
import com.sun.xml.ws.wsdl.writer.document.Message;
import com.sun.xml.ws.wsdl.writer.document.PortType;
import com.sun.xml.ws.wsdl.writer.document.Service;
import com.sun.xml.ws.wsdl.writer.document.Types;

@XmlElement(value="definitions")
public interface Definitions
extends TypedXmlWriter,
Documented {
    @XmlAttribute
    public Definitions name(String var1);

    @XmlAttribute
    public Definitions targetNamespace(String var1);

    @XmlElement
    public Service service();

    @XmlElement
    public Binding binding();

    @XmlElement
    public PortType portType();

    @XmlElement
    public Message message();

    @XmlElement
    public Types types();

    @XmlElement(value="import")
    public Import _import();
}

