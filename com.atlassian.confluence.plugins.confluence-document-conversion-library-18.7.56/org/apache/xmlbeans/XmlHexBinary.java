/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlHexBinary
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlHexBinary> Factory = new XmlObjectFactory("_BI_hexBinary");
    public static final SchemaType type = Factory.getType();

    public byte[] getByteArrayValue();

    public void setByteArrayValue(byte[] var1);
}

