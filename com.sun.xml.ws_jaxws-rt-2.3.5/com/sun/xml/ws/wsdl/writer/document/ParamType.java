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

public interface ParamType
extends TypedXmlWriter,
Documented {
    @XmlAttribute
    public ParamType message(QName var1);

    @XmlAttribute
    public ParamType name(String var1);
}

