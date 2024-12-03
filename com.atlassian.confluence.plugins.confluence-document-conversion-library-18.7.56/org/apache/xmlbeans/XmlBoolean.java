/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlBoolean
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlBoolean> Factory = new XmlObjectFactory("_BI_boolean");
    public static final SchemaType type = XmlBeans.getBuiltinTypeSystem().typeForHandle("_BI_boolean");

    public boolean getBooleanValue();

    public void setBooleanValue(boolean var1);
}

