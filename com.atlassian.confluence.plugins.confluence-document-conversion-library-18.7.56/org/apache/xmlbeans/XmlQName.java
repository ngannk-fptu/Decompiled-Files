/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlQName
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlQName> Factory = new XmlObjectFactory("_BI_QName");
    public static final SchemaType type = Factory.getType();

    public QName getQNameValue();

    public void setQNameValue(QName var1);
}

