/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlName;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlNCName
extends XmlName {
    public static final XmlObjectFactory<XmlNCName> Factory = new XmlObjectFactory("_BI_NCName");
    public static final SchemaType type = Factory.getType();
}

