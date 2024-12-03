/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlAnySimpleType
extends XmlObject {
    public static final XmlObjectFactory<XmlAnySimpleType> Factory = new XmlObjectFactory("_BI_anySimpleType");
    public static final SchemaType type = Factory.getType();

    public String getStringValue();

    public void setStringValue(String var1);
}

