/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlNormalizedString
extends XmlString {
    public static final XmlObjectFactory<XmlNormalizedString> Factory = new XmlObjectFactory("_BI_normalizedString");
    public static final SchemaType type = Factory.getType();
}

