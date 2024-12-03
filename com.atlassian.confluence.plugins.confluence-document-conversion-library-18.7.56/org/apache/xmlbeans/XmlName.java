/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlName
extends XmlToken {
    public static final XmlObjectFactory<XmlName> Factory = new XmlObjectFactory("_BI_Name");
    public static final SchemaType type = Factory.getType();
}

