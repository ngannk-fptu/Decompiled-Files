/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlAnyURI
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlAnyURI> Factory = new XmlObjectFactory("_BI_anyURI");
    public static final SchemaType type = Factory.getType();
}

