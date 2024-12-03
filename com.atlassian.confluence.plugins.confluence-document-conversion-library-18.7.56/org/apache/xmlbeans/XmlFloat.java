/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlFloat
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlFloat> Factory = new XmlObjectFactory("_BI_float");
    public static final SchemaType type = Factory.getType();

    public float getFloatValue();

    public void setFloatValue(float var1);
}

