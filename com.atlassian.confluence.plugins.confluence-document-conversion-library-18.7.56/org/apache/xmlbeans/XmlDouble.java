/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlDouble
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlDouble> Factory = new XmlObjectFactory("_BI_double");
    public static final SchemaType type = Factory.getType();

    public double getDoubleValue();

    public void setDoubleValue(double var1);
}

