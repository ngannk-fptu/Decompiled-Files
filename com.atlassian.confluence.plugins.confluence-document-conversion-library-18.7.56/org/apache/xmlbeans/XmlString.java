/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlString
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlString> Factory = new XmlObjectFactory("_BI_string");
    public static final SchemaType type = Factory.getType();
}

