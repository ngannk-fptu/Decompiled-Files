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
import com.sun.xml.ws.wsdl.writer.document.OpenAtts;
import javax.xml.namespace.QName;

@XmlElement(value="part")
public interface Part
extends TypedXmlWriter,
OpenAtts {
    @XmlAttribute
    public Part element(QName var1);

    @XmlAttribute
    public Part type(QName var1);

    @XmlAttribute
    public Part name(String var1);
}

