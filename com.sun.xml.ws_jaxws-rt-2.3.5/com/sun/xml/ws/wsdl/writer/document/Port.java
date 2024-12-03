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
import com.sun.xml.ws.wsdl.writer.document.Documented;
import javax.xml.namespace.QName;

@XmlElement(value="port")
public interface Port
extends TypedXmlWriter,
Documented {
    @XmlAttribute
    public Port name(String var1);

    @XmlAttribute
    public Port arrayType(String var1);

    @XmlAttribute
    public Port binding(QName var1);
}

