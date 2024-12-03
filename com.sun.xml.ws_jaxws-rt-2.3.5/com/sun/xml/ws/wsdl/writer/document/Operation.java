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
import com.sun.xml.ws.wsdl.writer.document.FaultType;
import com.sun.xml.ws.wsdl.writer.document.ParamType;

@XmlElement(value="operation")
public interface Operation
extends TypedXmlWriter,
Documented {
    @XmlElement
    public ParamType input();

    @XmlElement
    public ParamType output();

    @XmlElement
    public FaultType fault();

    @XmlAttribute
    public Operation name(String var1);

    @XmlAttribute
    public Operation parameterOrder(String var1);
}

