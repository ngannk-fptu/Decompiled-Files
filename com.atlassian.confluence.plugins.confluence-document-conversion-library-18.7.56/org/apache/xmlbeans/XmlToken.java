/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNormalizedString;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlToken
extends XmlNormalizedString {
    public static final XmlObjectFactory<XmlToken> Factory = new XmlObjectFactory("_BI_token");
    public static final SchemaType type = Factory.getType();
}

