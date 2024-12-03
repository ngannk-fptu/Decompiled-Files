/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.annotation.XmlAttribute
 */
package com.sun.xml.ws.wsdl.writer.document;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.ws.wsdl.writer.document.Documented;
import javax.xml.namespace.QName;

public interface FaultType
extends TypedXmlWriter,
Documented {
    @XmlAttribute
    public FaultType message(QName var1);

    @XmlAttribute
    public FaultType name(String var1);
}

