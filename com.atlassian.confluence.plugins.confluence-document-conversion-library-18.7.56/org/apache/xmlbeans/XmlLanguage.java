/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlLanguage
extends XmlToken {
    public static final XmlObjectFactory<XmlLanguage> Factory = new XmlObjectFactory("_BI_language");
    public static final SchemaType type = Factory.getType();
}

