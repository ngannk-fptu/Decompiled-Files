/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 *  com.sun.xml.txw2.annotation.XmlAttribute
 */
package com.sun.xml.ws.wsdl.writer.document.soap12;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;

public interface BodyType
extends TypedXmlWriter {
    @XmlAttribute
    public BodyType encodingStyle(String var1);

    @XmlAttribute
    public BodyType namespace(String var1);

    @XmlAttribute
    public BodyType use(String var1);

    @XmlAttribute
    public BodyType parts(String var1);
}

